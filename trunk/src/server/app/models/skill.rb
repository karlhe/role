class Skill < ActiveRecord::Base
  belongs_to :character_class
  has_many :abilities, :dependent => :destroy
  
  validates_presence_of :name, :skill_type, :character_class, :min_level, :formula
  
  # This calculates the skill damage based on the Skill.formula field.
  # We could probably specify some convention for "negative" damage as being
  # healing or some kind of status effect, but we can talk about that later.
  # XXX: This is very bad security wise. Alternative implementation should be
  # considered.
  def damage(level,character)
    # Variables to use in damage formulas.
    s_lvl = level
    c_lvl = character.level
    c_hp = character.health
    c_maxhp = character.max_health
    # Also, random(range) gives a random number in (0,range)
    eval self.formula
  end
  
  # An Integer randomizer
  def random(num)
    rand(num).to_i
  end
  
  # Returns a description for the skill_type corresponding to num
  def self.type_desc(num)
    {
      0 => :accelerometer,
      1 => :primary,
      2 => :secondary
    }[num]
  end
  
  # Returns the description for the current Skill's skill_type
  def type_desc
    Skill.type_desc(self.skill_type)
  end
end
