class CreateCharacterClasses < ActiveRecord::Migration
  def self.up
    create_table :character_classes do |t|
      t.string :name

      t.timestamps
    end
  end

  def self.down
    drop_table :character_classes
  end
end
