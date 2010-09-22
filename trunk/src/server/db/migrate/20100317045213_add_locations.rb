class AddLocations < ActiveRecord::Migration
  def self.up
    add_column :characters, :latitude, :decimal, :precision => 7, :scale => 2
    add_column :characters, :longitude, :decimal, :precision => 7, :scale => 2
  end

  def self.down
    remove_column :characters, :latitude
    remove_column :characters, :longitude
  end
end
