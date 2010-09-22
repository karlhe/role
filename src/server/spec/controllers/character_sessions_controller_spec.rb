require 'spec_helper'

CREATE = <<DOC
<?xml version="1.0" encoding="utf-8"?>
<character>
  <name>Karl</name>
  <password>password</password>
</character>
DOC

describe CharacterSessionsController do

  #Delete these examples and add some real ones
  it "should use CharacterSessionsController" do
    controller.should be_an_instance_of(CharacterSessionsController)
  end


  describe "GET 'new'" do
    it "should be successful" do
      get 'new'
      response.should be_success
    end
  end
  
  it "should respond to an XML POST on 'create'" do
    @karl = characters(:karl)
    Character.stub!(:find).and_return(@karl)
    controller.should_receive(:create_xml)
    controller.should_not_receive(:create_web)
    post :create, Hash.from_xml(CREATE), :content_type => 'application/xml'
  end
  
  it "should respond with 'success' with valid XML Post"
  it "should respond with 'error' on invalid XML Post"
end
