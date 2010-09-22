# The avatar representing the player inside the game.
# Also contains the players login information.
class Character < ActiveRecord::Base
  belongs_to :character_class
  has_many :abilities, :dependent => :destroy
  belongs_to :battle, :dependent => :destroy
  acts_as_authentic do |c|
    c.login_field = :name
    c.require_password_confirmation = false
  end
  
  validates_presence_of :name, :health, :level, :experience, :character_class, :fetched_at
  before_validation_on_create do |character|
    character.fetched_at = Time.at(0)
  end
  
  # Gives the current number of available skill points.
  # TODO: Decide on a skill point formula. Currently it is 1 point per level.
  def skill_points
    points = self.level
    self.abilities.each do |ability|
      points -= ability.level
    end
    return points
  end
  
  # Levels up the player if he has enough experience.
  # TODO: Decide on an experience formula. Currently it is exp*100 per level.
  def level_up!
    unless self.experience < self.to_next_level
      self.level += 1
      self.learn_skills!
      return self.save
    else
      return false
    end
  end
  
  # Formula or something for how much experience you need to level up.
  def to_next_level
    self.level*5
  end
  
  # Sets the fetched_at time so battle information is not fetched twice
  def set_fetched!
    self.fetched_at = Time.now
    return self.save
  end
  
  # Learn your new skills for the current level
  def learn_skills!
    self.character_class.get_new_skills(self.level).each do |skill|
      Ability.learn!(skill,self)
    end
  end
  
  # Updates the Character's current location
  def update_location!(latitude,longitude)
    self.latitude = latitude
    self.longitude = longitude
    return self.save
  end
  
  # Returns the maximum health of the Character
  def max_health
      self.character_class.max_health(self.level)
  end
  
  # Returns your current opponent
  def opponent
    return nil if self.battle.blank?
    self.battle.characters.each do |char|
      if not char == self
        return char
      end
    end
    
    # Remove the battle, because you have no opponent. (Probably a rejected battle)
    self.battle = nil
    self.save
    return nil
  end
  
  # Return character status. XXX: Currently 'passive" is not possible.
  def status
    if self.battle.present?
      return :battle
    else
      return :active
    end
  end

end
