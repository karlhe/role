# Specifies basic information such as Abilities, starting stats, etc.
# for a Character.
class CharacterClass < ActiveRecord::Base
  has_many :characters
  has_many :skills, :dependent => :destroy
  
  validates_presence_of :name, :health
  validates_uniqueness_of :name
  
  # Basically a test of 'create_character'
  def prepare_character!(options)
    character = Character.new
    character.character_class = self
    
    # Default values
    character.health = self.max_health(1)
    character.level = 1
    character.experience = 0
    character.latitude = BigDecimal("0")
    character.longitude = BigDecimal("0")
    
    options.each do |key,value|
      character.[]= key, value
    end
    # This doesn't work with []= for some reason:
    character.password = options[:password]
    
    return character
  end
  
  # Creates a member Character of this CharacterClass and returns it
  def create_character!(options)
    character = Character.new
    character.character_class = self
    
    # Default values
    character.health = self.max_health(1)
    character.level = 1
    character.experience = 0
    
    # Location of soda hall: 37.875644,-122.258742
    character.latitude = BigDecimal("37.875644")
    character.longitude = BigDecimal("-122.258742")
    
    options.each do |key,value|
      character.[]= key, value
    end
    # This doesn't work with []= for some reason:
    character.password = options[:password]
    
    Character.transaction do
      character.save
      # Find level 1 skill for class
      start_skills = self.get_new_skills(1)
      # Create ability based on skill
      start_skills.each do |skill|
          ability = Ability.new
          ability.level = 1
          ability.character = character
          ability.skill = skill
          ability.save
      end
    end
    
    return character
  end
  
  # Get a list of skills learned for a level
  def get_new_skills(level)
    self.skills.select do |skill|
        skill.min_level == level
    end
  end
  
  # Creates a member Skill of this CharacterClass and returns it
  def create_skill!(options)
    skill = Skill.new
    
    # Defailt values
    skill.character_class = self
    skill.skill_type = 1  # Defaults to "attack skill"
    skill.min_level = 1
    skill.formula = 'level'
    
    options.each do |key,value|
      skill.[]= key, value
    end
    
    return skill if skill.save
    return nil
  end
  
  def to_xml_hash
    hash = {}
    hash[:name] = self.name
    hash[:description] = self.description
    hash[:health] = self.health
    hash[:skills] = []
    self.skills.each do |skill|
      skill_hash = {
        :skill_name => skill.name,
        :min_level => skill.min_level,
        :formula => skill.formula,
        :skill_type => skill.type_desc.to_s
      }
      hash[:skills].push(skill_hash)
    end
    return hash
  end
  
  # Calculates what the health should be, based on level
  def max_health(level)
    (self.health + self.health*0.1*(level-1)).to_i
  end
end
