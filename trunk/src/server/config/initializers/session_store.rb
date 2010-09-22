# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_role_session',
  :secret      => '42ff34bfc955d2dd1db5167cd8b7a88acac17575273908049008cfa9293a6c8ce54f9fc5cb295a857c66fc78b7061cb9cac948a6f2fd242ea7ae98380083fb7e'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
