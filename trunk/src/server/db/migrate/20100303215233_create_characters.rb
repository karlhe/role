class CreateCharacters < ActiveRecord::Migration
  def self.up
    create_table :characters do |t|
      t.string :name
      t.references :character_class
      t.integer :health
      t.integer :experience
      t.integer :level
      t.integer :opponent
      
      t.timestamps
    end
  end

  def self.down
    drop_table :characters
  end
end
