require 'spec_helper'

describe Action do
  before(:each) do
    @ability = abilities(:karl_slap)
    @saung = characters(:saung)
    @battle = battles(:karl_saung)
    @valid_attributes = {
      :ability => @ability,
      :target => @saung,
      :battle => @battle,
      :damage => 1,
      :completed => false
    }
  end

  it "should create a new instance given valid attributes" do
    Action.create!(@valid_attributes)
  end
end
