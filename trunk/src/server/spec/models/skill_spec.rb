require 'spec_helper'
require 'rexml/document'

describe Skill do
  before(:each) do
    character_classes(:slapper)
    @valid_attributes = {
      :name => "value for name",
      :skill_type => 1,
      :character_class=> character_classes(:slapper),
      :min_level => 1,
      :formula => "value for formula"
    }
  end

  it "should create a new instance given valid attributes" do
    Skill.create!(@valid_attributes)
  end
end
