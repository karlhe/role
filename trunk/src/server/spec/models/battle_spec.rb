require 'spec_helper'

describe Battle do
  before(:each) do
    @karl = characters(:karl)
    @saung = characters(:saung)
    @adam = characters(:adam)
    @glen = characters(:glen)
    @valid_attributes = {
      :started => false,
      :completed => false,
      :initiator => @adam
    }
  end

  it "should create a new instance given valid attributes" do
    Battle.create!(@valid_attributes)
  end
  
  it "should create a pending battle when requested" do
    @battle = Battle.request!(@karl,@saung)
    @battle.should be_valid
    @battle.status.should == :pending
    @battle.characters.should include(@karl)
    @battle.characters.should include(@saung)
  end
  
  describe "when given action requests" do
    it "should process initiate requests" do
      result = Battle.initiate!(@adam,@glen,'initiate')
      result.should == true
      @adam.battle.should be_an_instance_of(Battle)
      @adam.battle.should == @glen.battle
      @adam.battle.initiator.should == @adam
    end
    it "should process accept requests" do
      result = Battle.initiate!(@saung,@karl,'accept')
      result.should == true
      @saung.battle.status.should == :active
    end
    it "should not allow the initiator to accept the battle" do
      result = Battle.initiate!(@karl,@saung,'accept')
      result.should == false
      @karl.battle.status.should == :pending
    end
    it "should process reject requests" do
      battle = @karl.battle
      result = Battle.initiate!(@karl,@saung,'reject')
      result.should == true
      battle.status.should == :rejected
    end
    it "should process fight requests" do
      @karl.battle.start!
      karl_slap = abilities(:karl_slap)
      old_health = @saung.health
      Ability.stub!(:find).and_return(karl_slap)
      action = @karl.battle.fight!(@karl,@saung,1)
      action.should_not be_nil
      action.ability.should == karl_slap
      @karl.battle.actions.should include(action)
      @saung.health.should == old_health - action.damage
    end
    it "should process string fight requests" do
      @karl.battle.start!
      karl_slap = abilities(:karl_slap)
      old_health = @saung.health
      Ability.stub!(:find).and_return(karl_slap)
      action = @karl.battle.fight!(@karl,@saung,'Slap')
      action.should_not be_nil
      action.ability.should == karl_slap
      @karl.battle.actions.should include(action)
      @saung.health.should == old_health - action.damage
    end
  end
  
  it "should start a battle when accepted" do
    @battle = Battle.request!(@karl,@saung)
    @battle.start!.should == true
    @battle.status.should == :active
  end
  
  it "should be able to complete the battle" do
    @battle = Battle.request!(@karl,@saung)
    @battle.start!
    @battle.complete!.should == true
    @battle.status.should == :completed
  end
  
  it "should know whether a player is dead" do
    @karl.health = 0
    @battle = Battle.request!(@karl,@saung)
    @battle.start!
    @battle.is_done?.should == true
  end
  
  it "should send updated information" do
    @battle = battles(:karl_saung)
    @battle.start!
    @updates = Battle.send_updates(@karl)
    @updates['status'].should == @battle.status.to_s
    @updates['player']['health'].should == @karl.health
    @updates['opponent']['health'].should == @saung.health
    @karl_action = @updates['player']['action']
    @saung_action = @updates['opponent']['action']
    @karl_action['status'].should == 'pending'
    @karl_action['effect'].should > 0
    @saung_action['status'].should == 'pending'
    @saung_action['effect'].should > 0
  end
  
  it "should not send updated information twice" do
    @battle = battles(:karl_saung)
    @battle.start!
    Battle.send_updates(@karl)
    
    @updates = Battle.send_updates(@karl)
    @updates['player']['action'].should be_nil
    @updates['opponent']['action'].should be_nil
  end
  
  it "should complete battles properly" do
    @battle = battles(:karl_saung)
    @battle.start!
    characters(:karl).health = 0
    characters(:karl).save
    @battle.complete!
    @battle.winner.should == characters(:saung)
  end
  
  it "should cleanup battles properly" do
    @battle = battles(:karl_saung)
    @battle.start!
    @battle.characters.each do |character|
      character.battle.should == @battle
    end
    
    characters(:karl).health = 0
    characters(:karl).save
    
    @battle.characters.each do |character|
      character.fetched_at = 1.year.from_now
      character.fetched_at.should > @battle.updated_at
      character.save
    end
    
    @battle.cleanup!
    @battle.characters.each do |character|
      character.battle.should be_nil
    end
  end
  
  it "should set the battle status of all players to completed" do
    @battle = battles(:karl_saung)
    @battle.start!
    @battle.characters.each do |character|
      character.battle.should == @battle
    end
    
    @battle.characters.each do |character|
      character.fetched_at = 1.year.from_now
      character.health = 0 if character.name == characters(:karl).name
      character.save
    end
    
    @battle.is_done?.should == true
    
    @battle.status.should_not == :completed
    @battle.check_done!
    @battle.characters.each do |character|
      character.battle.should be_nil
    end
  end
  
  it "should calculate experience" do
    @battle = battles(:karl_saung)
    @battle.experience_for(@battle.characters.first).should > 0
    @battle.experience_for(@battle.characters.last).should > 0
  end
  
  it "should allow you to run from a battle" do
    @battle = battles(:karl_saung)
    @battle.start!
    char = @battle.characters.first
    @battle.run!(char).should == true
    @battle.winner.should == char.opponent
    
    @battle.characters.each do |character|
      character.fetched_at = 1.year.from_now
      character.save
    end
    
    @battle.check_done!
    @battle.characters.each do |character|
      character.battle.should be_nil
    end
  end
  
end
