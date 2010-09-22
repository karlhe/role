class RefactorBattles < ActiveRecord::Migration
  def self.up
    # The character is now owned by the battle.
    # Except for an initiator, to know who started it.
    remove_column :battles, :receiver_id
    rename_column :battles, :initialized, :started
    
    # Add foreign key to character.
    add_column :characters, :battle_id, :integer
    add_column :characters, :fetched_at, :datetime
  end

  def self.down
    add_column :battles, :receiver_id, :integer
    rename_column :battles, :started, :initialized
    
    remove_column :characters, :battle_id
    remove_column :characters, :fetched_at
  end
end
