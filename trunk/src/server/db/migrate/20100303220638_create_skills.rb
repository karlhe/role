class CreateSkills < ActiveRecord::Migration
  def self.up
    create_table :skills do |t|
      t.string :name
      t.integer :skill_type
      t.references :character_class
      t.integer :min_level
      t.string :formula

      t.timestamps
    end
  end

  def self.down
    drop_table :skills
  end
end
