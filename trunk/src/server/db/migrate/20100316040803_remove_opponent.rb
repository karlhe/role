class RemoveOpponent < ActiveRecord::Migration
  def self.up
    remove_column :characters, :opponent
  end

  def self.down
    add_column :characters, :opponent, :integer
  end
end
