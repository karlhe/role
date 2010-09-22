require 'spec_helper'

describe Ability do
  before(:each) do
    @battle = battles(:karl_saung)
    @saung = characters(:saung)
    @karl = characters(:karl)
    @skill = skills(:slap)
    @valid_attributes = {
      :level => 1,
      :character => @karl,
      :skill => @skill
    }
  end

  it "should create a new instance given valid attributes" do
    Ability.create!(@valid_attributes)
  end
  
  it "should return damage when asked for damage" do
    @ability = Ability.create!(@valid_attributes)
    @ability.damage.should be_an_instance_of(Fixnum)
  end
  
  it "should be able to level up if you have enough skill points" do
    @ability = Ability.create!(@valid_attributes)
    @karl.stub!(:skill_points).and_return(1)
    @ability.level_up!.should == true
  end
  
  it "should not level up if you do not have sufficient skill points" do
    @ability = Ability.create!(@valid_attributes)
    @karl.stub!(:skill_points).and_return(0)
    @ability.level_up!.should == false
  end
  
  it "should create a valid action when targeting an opponent" do
    @ability = Ability.create!(@valid_attributes)
    @battle.start!
    @action = @ability.target!(@battle,@saung)
    @action.should be_an_instance_of(Action)
    @action.ability.should == @ability
    @action.battle.should == @battle
    @action.damage.should == @ability.damage
  end
end
