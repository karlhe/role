require 'spec_helper'

describe "/characters/show/1.xml" do
  before(:each) do
    @karl = characters(:karl)
    @slap = abilities(:karl_slap)
    @karl.stub!(:abilities).and_return([@slap])
    @slap.stub!(:skill).and_return(skills(:slap))
    assigns[:character] = @karl
    render 'characters/show.xml.builder'
  end

  # Tests if generated XML matches expectations
  it "should have a valid response" do
    response.should have_tag('character') do
      response.should have_tag('id', @karl.id.to_s)
      response.should have_tag('name', @karl.name)
      response.should have_tag('health', @karl.health.to_s)
      # TODO: Test rest of fields
      
      response.should have_tag('skills') do
        response.should have_tag('skill') do
          response.should have_tag('skill-id', @slap.id.to_s)
        end
        # TODO: Test rest of fields
      end
    end
  end
end
