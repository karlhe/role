# This file is auto-generated from the current state of the database. Instead of editing this file, 
# please use the migrations feature of Active Record to incrementally modify your database, and
# then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your database schema. If you need
# to create the application database on another system, you should be using db:schema:load, not running
# all the migrations from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20100409063859) do

  create_table "abilities", :force => true do |t|
    t.integer  "level"
    t.integer  "character_id"
    t.integer  "skill_id"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "actions", :force => true do |t|
    t.integer  "ability_id"
    t.integer  "target_id"
    t.integer  "battle_id"
    t.integer  "damage"
    t.boolean  "completed",  :default => false
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "battles", :force => true do |t|
    t.boolean  "started",      :default => false
    t.boolean  "completed",    :default => false
    t.integer  "initiator_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "winner_id"
  end

  create_table "character_classes", :force => true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.text     "description"
    t.integer  "health"
  end

  create_table "characters", :force => true do |t|
    t.string   "name"
    t.integer  "character_class_id"
    t.integer  "health"
    t.integer  "experience"
    t.integer  "level"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "battle_id"
    t.datetime "fetched_at"
    t.string   "crypted_password"
    t.string   "password_salt"
    t.string   "persistence_token"
    t.decimal  "latitude"
    t.decimal  "longitude"
  end

  create_table "skills", :force => true do |t|
    t.string   "name"
    t.integer  "skill_type"
    t.integer  "character_class_id"
    t.integer  "min_level"
    t.string   "formula"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

end
