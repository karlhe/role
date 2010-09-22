require 'spec_helper'

INITIATE = <<DOC
<?xml version="1.0" encoding="utf-8"?>
<battle>
  <opponent>4</opponent>
  <action>
    <type>initiate</type>
  </action>
</battle>
DOC

describe BattlesController do
  before(:each) do
    @adam = characters(:adam)
    @glen = characters(:glen)
    controller.stub!(:current_character).and_return(@adam)
    Character.stub!(:find).with("4").and_return(@glen)
    Character.stub!(:find).with(:all).and_return(characters)
  end

  describe "when given a initiate XML post" do
    after(:each) do
      post :update, Hash.from_xml(INITIATE), :content_type => 'application/xml'
    end
    
    it "should call Battle.request!" do
      Battle.should_receive(:request!)
    end
    
  end

end
