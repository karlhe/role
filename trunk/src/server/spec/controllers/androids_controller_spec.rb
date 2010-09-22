require 'spec_helper'

LOCATIONXML = <<DOC
<?xml version="1.0" encoding="utf-8"?>
<update>
  <version>1</version>
  <id>1</id>
  <location>
    <latitude>123.123</latitude>
    <longitude>456.456</longitude>
  </location>
</update>
DOC

MYXML = <<DOC
<?xml version="1.0" encoding="utf-8"?>
<update>
  <version>1</version>
  <id>1</id>
  <mode>active</mode>
  <status>idle</status>
</update>
DOC

RECEIVEXML = <<DOC
<?xml version="1.0" encoding="utf-8"?>
<updates>
  <health>100</health>
  <experience>99</experience>
  <level>1</level>
  <locations>
    <character id="2">
      <name>Saung</name>
      <location>
        <latitude>123.123</latitude>
        <longitude>456.456</longitude>
      </location>
    </character>
  </locations>
  <battle>
    <id>1</id>
    <initiator>1</initiator>
  </battle>
</updates>
DOC

describe AndroidsController do
  fixtures :characters, :character_classes, :abilities, :skills
  
  #Delete these examples and add some real ones
  it "should use AndroidsController" do
    controller.should be_an_instance_of(AndroidsController)
  end


  describe "PUT 'update'" do
    before(:each) do
      @karl = characters(:karl)
      @saung = characters(:saung)
      Character.stub!(:find).with("1").and_return(@karl)
      Character.stub!(:find).with("2").and_return(@saung)
      Character.stub!(:find).with(:all).and_return(characters)
      @ability = abilities(:karl_slap)
      Ability.stub!(:find).and_return(@ability)
      Battle.stub(:find).and_return(battles)
    end
    
    it "should update location information when posted" do
      @karl.should_receive(:update_location!)
      post :update, Hash.from_xml(LOCATIONXML), :content_type => 'application/xml'
    end
  end

  describe "GET 'fetch'" do
    before(:each) do
      @adam = characters(:adam)
      Character.stub!(:find).with(:all).and_return(characters)
    end
    
    it "should return updated information" do
      controller.stub!(:current_character).and_return(@adam)
      get :fetch
      response.should render_template('fetch.xml.builder')
    end
  end
end
