# A Skill instance owned by a Character.
# Stores the skill level, owner, and the Skill it is based on.
class Ability < ActiveRecord::Base
  belongs_to :character
  belongs_to :skill
  has_many :actions, :dependent => :destroy
  
  validates_presence_of :level, :character, :skill
  
  # Levels up the ability skill level.
  # TODO: Notify character of decrease in skill points, if that's stored anywhere.
  # XXX: This is untested!
  # @return true if ability level was increased.
  def level_up!
    if self.character.skill_points > 0
      self.level += 1
      return self.save
    else
      return false
    end
  end
  
  # Create a new ability for a Character based on a Skill
  def self.learn!(skill,character)
    ability = Ability.new( :skill => skill, :character => character, :level => 1 )
    if ability.save
      return ability
    else
      return nil
    end
  end
  
  # Alias of Skill.damage
  # @return Integer skill damage for this ability level
  def damage
    self.skill.damage(self.level,self.character)
  end
  
  # Utilizes this ability against a target
  def target!(battle,opponent)
    action = Action.new
    action.ability = self
    action.target = opponent
    action.battle = battle
    action.damage = self.damage
    if action.save
      return action
    else
      return nil
    end
  end
  
end
