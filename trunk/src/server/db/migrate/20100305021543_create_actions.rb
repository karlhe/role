class CreateActions < ActiveRecord::Migration
  def self.up
    create_table :actions do |t|
      t.references :ability
      t.references :target
      t.references :battle
      t.integer :damage
      t.boolean :completed, :default => false

      t.timestamps
    end
  end

  def self.down
    drop_table :actions
  end
end
