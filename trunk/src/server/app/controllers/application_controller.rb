# Filters added to this controller apply to all controllers in the application.
# Likewise, all the methods added will be available for all controllers.

class ApplicationController < ActionController::Base
  helper :all # include all helpers, all the time
  protect_from_forgery # See ActionController::RequestForgeryProtection for details

  # Scrub sensitive parameters from your log
  filter_parameter_logging :password
  
  helper_method :current_character_session, :current_character
  
  private
  
  # Saves a location to return to into the session.
  def store_location
    session[:return_to] = request.request_uri unless request.xhr?
  end
  
  # Redirects back to the location stored by store_location
  def redirect_back_or_default(default)
    redirect_to(session[:return_to] || default)
    session[:return_to] = nil
  end
  
  # Returns the current session of the logged-in character.
  def current_character_session
    return @current_character_session if defined?(@current_character_session)
    @current_character_session = CharacterSession.find
  end
  
  # Returns the currently logged-in character.
  def current_character
    return @current_character if defined?(@current_character)
    @current_character = current_character_session && current_character_session.character
  end
  
  # Requires a character to be logged in.
  # Usage: before_filter
  def require_character
    unless current_character
      store_location
      flash[:notice] = "You must be logged in to access this page"
      redirect_to new_character_session_path
      return false
    end
  end
  
  # Requires character to not be logged in.
  # Usage: before_filter
  def require_no_character
    if current_character
      store_location
      flash[:notice] = "You must be logged out to access this page"
      redirect_to root_path
      return false
    end
  end

end
