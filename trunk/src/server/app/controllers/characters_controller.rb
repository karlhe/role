class CharactersController < ApplicationController
  # Creates a character based on XML input:
  # <character>
  #   <name>Karl</name>
  #   <class>Assassin</class>
  #   <password>password</password>
  # </character>
  def create
    @input = params[:character]
    @class = CharacterClass.find(:first,
        :conditions => { :name => @input['class'] })
    if @class.present?
      @character = @class.create_character!({
        :name => @input['name'],
        :password => @input['password']
    })
    else
      error = 'Invalid character class given.'
    end
    
    if @character.blank?
      @response = { :status => 'error',
                    :message => error }
    elsif @character.new_record?
      if @character.errors[:name].present?
        error = 'Name ' + @character.errors[:name].to_a.first
      elsif @character.errors[:password].present?
        error = 'Password ' + @character.errors[:password].to_a.first
      else
        # Fallback error message
        error = 'Character creation request was invalid.'
      end
      @response = { :status => 'error',
                    :message => error }
    else
      # Success!
      @response = { :status => 'success',
                    :message => 'Character created.',
                    :id => @character.id }
    end
    
    respond_to do |format|
      format.xml { render :xml => @response.to_xml(:root => 'character') }
    end
  end
  
  def show
    @character = Character.find(params[:id])
    if @character.present?
      respond_to do |format|
        format.xml
      end
    else
      @response = { :status => 'error',
                    :message => 'Character not found.' }
      respond_to do |format|
        format.xml { render :xml => @response.to_xml(:root => 'character') }
      end
    end
  end

  # TODO: Have a way to delete a character.
  def destroy
  end

end
