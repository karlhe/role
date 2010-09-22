class AddClassDescriptions < ActiveRecord::Migration
  def self.up
    add_column :character_classes, :description, :text
  end

  def self.down
    remove_column :character_classes, :description
  end
end
