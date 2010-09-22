class AddCharacterLogins < ActiveRecord::Migration
  def self.up
    add_column :characters, :crypted_password, :string, :null => :false
    add_column :characters, :password_salt, :string, :null => :false
    add_column :characters, :persistence_token, :string, :null => :false
  end

  def self.down
    remove_column :characters, :crypted_password
    remove_column :characters, :password_salt
    remove_column :characters, :persistence_token
  end
end
