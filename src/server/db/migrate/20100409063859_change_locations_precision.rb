class ChangeLocationsPrecision < ActiveRecord::Migration
  def self.up
    change_column :characters, :latitude, :decimal, :precision => 9, :scale => 6
    change_column :characters, :longitude, :decimal, :precision => 9, :scale => 6
  end

  def self.down
    change_column :characters, :latitude, :decimal, :precision => 7, :scale => 2
    change_column :characters, :longitude, :decimal, :precision => 7, :scale => 2
  end
end
