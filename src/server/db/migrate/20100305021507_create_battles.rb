class CreateBattles < ActiveRecord::Migration
  def self.up
    create_table :battles do |t|
      t.boolean :initialized, :default => false
      t.boolean :completed, :default => false
      t.references :initiator
      t.references :receiver

      t.timestamps
    end
  end

  def self.down
    drop_table :battles
  end
end
