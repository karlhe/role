require 'spec_helper'

describe Character do
  before(:each) do
    @valid_attributes = {
      :name => "George",
      :character_class => character_classes(:slapper),
      :health => 1,
      :experience => 1,
      :level => 1,
      :password => 'password'
    }
  end

  it "should create a new instance given valid attributes" do
    Character.create!(@valid_attributes)
  end
  
  it "should be associated with a valid class" do
    @char = Character.create!(@valid_attributes)
    @char.character_class = nil
    @char.should_not be_valid
  end
  
  it "should be able to level up with enough experience" do
    @char = Character.create!(@valid_attributes)
    @char.experience = @char.level*100
    @char.save
    @char.level_up!.should == true
  end
  
  it "should update location when 'update_location!' is called" do
    @karl = characters(:karl)
    @karl.update_location!('123.1001','-332.1023').should == true
    @karl.latitude.should == BigDecimal('123.1001')
    @karl.longitude.should == BigDecimal('-332.1023')
  end
  
  it "should display Character's max health when max_health is called" do
    @karl = characters(:karl)
    @karl.respond_to?(:max_health).should == true
    @karl.max_health.class.should == 1.class
    @karl.max_health.should >= @karl.health
  end
  
  it "should find the correct opponent when self.opponent is called" do
    @karl = characters(:karl)
    @saung = characters(:saung)
    @karl.opponent.should == @saung
    @saung.opponent.should == @karl
  end
end
