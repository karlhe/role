class AddBattleWinner < ActiveRecord::Migration
  def self.up
    add_column :battles, :winner_id, :integer
  end

  def self.down
    remove_column :battles, :winner_id
  end
end
