class AddClassHealth < ActiveRecord::Migration
  def self.up
    add_column :character_classes, :health, :integer
  end

  def self.down
    remove_column :character_classes, :health
  end
end
