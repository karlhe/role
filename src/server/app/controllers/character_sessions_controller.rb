# The login session of a Character.
class CharacterSessionsController < ApplicationController

  # Log in form for the game
  def new
    @character_session = CharacterSession.new
  end
  
  # Log into the game
  def create
    if params[:character].present? then create_xml
    else create_web
    end
  end
  
  # Log out of the game
  def destroy
    current_character_session.destroy
    flash[:notice] = "Logout successful!"
    redirect_back_or_default new_character_session_path
  end

  private
  
  # Create action for XML POST
  def create_xml
    input = params[:character]
    @character_session = CharacterSession.new({
      :name => input['name'],
      :password => input['password'],
      :remember_me => true
    })
    if @character_session.save
      @character = current_character
      @response = { :status => 'success',
                    :message => "Logged in as character #{input['name']}",
                    :id => @character.id }
    else
      @response = { :status => 'error',
                    :message => 'Log in failed.' }
    end
    respond_to do |format|
      format.xml { render :xml => @response.to_xml(:root => 'character_session') }
    end
  end
  
  # Create action for HTTP POST
  def create_web
    @character_session = CharacterSession.new(params[:character_session])
    if @character_session.save
      flash[:notice] = "Login as #{@character_session.character.name} successful!"
      redirect_back_or_default root_path
    else
      render :action => :new
    end
  end
  
end
