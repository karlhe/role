# A battle keeps track of the Actions performed between two Characters.
class Battle < ActiveRecord::Base
  has_many :characters
  has_many :actions, :dependent => :destroy
  belongs_to :initiator, :class_name => 'Character'
  belongs_to :winner, :class_name => 'Character'
  
  validates_presence_of :initiator
  
  
  # Request a battle with another player
  # FIXME: There is a strange bug when I run android-initiate-battle.sh
  # where the receiver was not added to the battle.
  # This is strange because there are numerous checks that should prevent this.
  def self.request!(initiator,receiver)
    return nil if initiator.blank? or receiver.blank?
    @battle = Battle.new
    @battle.initiator = initiator
    receiver.battle = @battle
    initiator.battle = @battle
    
    Battle.transaction do
      @battle.save
      initiator.save
      receiver.save
    end
    
    # Uncommment to test battles without battle initiation
    # @battle.start!
    
    return @battle unless @battle.new_record?
    return nil
  end
  
  # Creates or starts a battle based on 'initiate'
  def self.initiate!(character,opponent,initiate)
    if initiate == 'initiate'
      return false if character.battle.present? or opponent.battle.present?
      battle = Battle.request!(character,opponent)
      return battle.present?
    elsif initiate == 'accept'
      return false unless character.battle == opponent.battle and character.battle.initiator != character and not character.battle.started
      return character.battle.start!
    elsif initiate == 'reject'
      return false unless character.battle == opponent.battle and not character.battle.started
      return character.battle.reject!(character)
    elsif initiate == 'run'
      return false unless character.battle == opponent.battle and not character.battle.completed
      return character.battle.run!(character)
    else
      return false
    end
  end
  
  # The battle hasn't truly started until it is initialized.
  def start!
    self.started = true
    return self.save
  end
  
  # Invoked to end the conflict and designate a winner.
  # Also applies to Battle rejection.
  def complete!
    self.completed = true
    fail = false
    if self.started
      self.characters.each do |character|
        if character.health > 0
          fail = true unless set_winner!(character)
          break
        end
      end
    end
    return false if fail
    return self.save
  end
  
  # Invoked to reject a battle
  def reject!(character)
    self.completed = true
    character.battle = nil
    Battle.transaction do
      self.save
      character.save
    end
  end
  
  # Sets the character as the winner and rewards experience.
  def set_winner!(character)
    self.winner = character
    character.experience += self.experience_for(character)
    Battle.transaction do
      character.save
      self.save
    end
  end
  
  # Run away from your battle.
  def run!(character)
    self.set_winner!(character.opponent)
    self.completed = true
    character.battle = nil
    Battle.transaction do
      self.save
      character.save
    end
  end
  
  # Creates and executes an action
  def fight!(sender,receiver,ability_input)
    return nil if self.started == false  # Can't fight if they haven't accepted
    return nil if self.completed == true # Shouldn't be able to attack after battle ends
    
    if ability_input.class == Fixnum
      ability = sender.abilities.find(ability_input)
    else
      skill = Skill.find_by_name(ability_input)
      if skill then ability = sender.abilities.find(:first, :conditions => { :skill_id => skill.id }) end
    end
    return false if ability.blank?
    Action.transaction do
      action = ability.target!(self,receiver)
      # This immediately aplies the damage.
      # FIXME: This may not occur here in the future.
      if action.present?
        action.target.health = action.target.health - action.damage
        action.target.save
      end
      return action
    end
  end
  
  # Gives the current state of the battle
  def status
    return :pending if !started and !completed
    return :active if started and !completed
    return :rejected if !started and completed
    return :completed
  end
  
  # Tells you if a player in the battle is dead.
  def is_done?
    return true if self.winner.present?
    dead = self.characters.select do |char|
      char.health <= 0
    end
    dead.length == 0 ? (return false) : (return true)
  end
  
  # Performs actions related to completing a battle.
  def check_done!
    # This also applies to battle rejection.
    if self.completed == true
      self.cleanup!
      return true
    
    # This only occurs when someone has just died.
    elsif self.started and self.is_done?
      Battle.transaction do
        self.complete!
        self.cleanup!
      end
      return true
    
    # Nothing has happened.
    else
      return false
    end
  end
  
  # Removes the battle from the characters if both of them have checked the
  # battle status since the battle ended.
  def cleanup!
    can_remove = true
    self.characters.each do |char|
      if char.fetched_at < self.updated_at
        can_remove = false
        break
      end
    end
    
    if can_remove
      Battle.transaction do
        self.characters.each do |character|
          character.battle = nil if character.battle.present?
          # Revive character if he is dead.
          character.health = character.max_health if character.health <= 0
          character.save
        end
      end
    end
    
    can_remove
  end
  
  # Returns experience gained for a player
  def experience_for(character)
    exp = 5
    self.characters.each do |char|
      unless char == character
        bonus = char.level - character.level + 1
        exp += bonus if bonus > 0
      end
    end
    exp
  end
  
  # Prepares the information that should be sent to the client.
  def self.send_updates(character)
    return { :status => 'none' } if character.battle.blank?
    battle = character.battle
    @update = {}
    @update['initiator'] = battle.initiator_id
    @update['player'] = {}
    @update['opponent'] = {}
    battle.characters.each do |char|
      if character == char
        @update['player']['health'] = char.health
      else
        @update['opponent']['id'] = char.id
        @update['opponent']['health'] = char.health
        @update['opponent']['max-health'] = char.max_health
      end
    end
    if @update['opponent']['id'].blank?
      @update['opponent']['id'] = 0
      @update['opponent']['health'] = 0
      @update['opponent']['max-health'] = 0
    end
    @actions = battle.actions.find(:all, :conditions => ["updated_at > ?", character.fetched_at])
    @actions.each do |action|
      if action.completed != true
        if action.ability.character == character
          @update['player']['action'] = {}
          @update['player']['action']['ability'] = action.ability.skill.name
          @update['player']['action']['status'] = action.status.to_s
          @update['player']['action']['effect'] = action.effect
        else
          @update['opponent']['action'] = {}
          @update['opponent']['action']['ability'] = action.ability.skill.name
          @update['opponent']['action']['status'] = action.status.to_s
          @update['opponent']['action']['effect'] = action.effect
          action.update_attribute(:completed,true)
        end
      end
    end
    
    exp = battle.experience_for(character)
    battle.check_done!
    if battle.status == :completed
      # Makes a note about whether you won/lost and experience gained.
      if battle.winner == character
        @update['status'] = 'won'
        @update['experience'] = exp
      else
        @update['status'] = 'lost'
        @update['experience'] = 0
      end
      # Makes a note if you level up
      if character.level_up!
        @update['level-up'] = character.level
        # TODO: Refactor because this is retarded
        @update['new-skill'] = character.character_class.get_new_skills(character.level)[0].name
      end
      
      # battle.clean_up!
    else
      @update['status'] = battle.status.to_s
    end
    
    # TODO: Rethink how set_fetched should be used
    character.set_fetched!
    return @update
  end
end
