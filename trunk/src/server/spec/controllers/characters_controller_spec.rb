require 'spec_helper'

CHARXML = <<DOC
<?xml version="1.0" encoding="utf-8"?>
<character>
  <name>George</name>
  <class>Slapper</class>
  <password>password</password>
</character>
DOC

describe CharactersController do

  #Delete these examples and add some real ones
  it "should use CharactersController" do
    controller.should be_an_instance_of(CharactersController)
  end


  describe "PUT 'create'" do
    before(:each) do
      @slapper = character_classes(:slapper)
      @slapper.stub!(:skills).and_return(Skill)
      CharacterClass.stub!(:find).and_return(@slapper)
      @slap = skills(:slap)
      Skill.stub!(:find).and_return(@slap)
    end
    
    it "should create a new character given valid input" do
      post :create, Hash.from_xml(CHARXML), :content_type => 'application/xml'
      @response = Hash.from_xml(response.body)
      @response['character']['status'].should == 'success'
    end
    
    it "should not create a new character given invalid input" do
      @xml = Hash.from_xml(CHARXML)
      CharacterClass.stub!(:find).and_return(nil)
      post :create, @xml, :content_type => 'application/xml'
      @response = Hash.from_xml(response.body)
      @response['character']['status'].should == 'error'
    end
    
  end

end
