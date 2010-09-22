# An instance of the usage of an Ability.
class Action < ActiveRecord::Base
  belongs_to :ability
  belongs_to :target, :class_name => 'Character'
  belongs_to :battle
  
  validates_presence_of :ability, :target, :battle, :damage
  
  # States whether an Action's effect has been felt.
  def status
    return :completed if self.completed
    return :pending
  end
  
  # Returns the effect of this Action.
  def effect
    return self.damage
  end
  
  # Creates a new action.
  def self.initiate!(character,opponent,ability)
    return false if ability.character != character
    
    action = Action.new
    action.ability = ability
    action.target = opponent
    action.battle = character.battle
    action.damage = ability.damage
    return action.save
  end
end
