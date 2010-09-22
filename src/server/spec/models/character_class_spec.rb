require 'spec_helper'

describe CharacterClass do
  before(:each) do
    @valid_attributes = {
      :name => "Bomber",
      :health => 100
    }
  end

  it "should create a new instance given valid attributes" do
    CharacterClass.create!(@valid_attributes)
  end
  
  it "should create a character based on hash of attributes" do
    @class = character_classes(:slapper)
    @character = @class.create_character!({
      :name => 'George',
      :password => 'password'
    })
    @character.should_not be_nil
    @character.name.should == 'George'
    @class.characters.should include(@character)
  end
  
  it "should create a skill based on hash of attributes" do
    @class = character_classes(:slapper)
    @skill = @class.create_skill!({
      :name => 'Slap',
      :min_level => 2,
      :formula => 'level**2'
    })
    @skill.should_not be_nil
    @skill.name.should == 'Slap'
    @skill.min_level.should == 2
    @skill.damage(5).should == 25
  end
end
