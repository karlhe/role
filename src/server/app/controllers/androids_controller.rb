# This controller is the interface for communicating with the Android device.
class AndroidsController < ApplicationController
  
  # This is the home page.
  def index
  end
  
  # Allows the Android to post information to the server.
  def update
    @update = params[:update]
    if @update['id']
      @character = Character.find(@update['id'])
    else
      @character = current_character
    end
    
    if @character.present?
      @status = @update['status']
      # TODO: Save status somewhere.
      
      @location = @update['location']
      if @location.present?
        @character.update_location!(@location['latitude'], @location['longitude'])
      end
      
      @response = { :status => 'success',
                    :message => 'Character information received.' }
    else
      @response = { :status => 'error',
                    :message => 'Character could not be found.' }
    end
    
    respond_to do |format|
      format.xml { render :xml => @response.to_xml({
          :root => 'update',
          :skip_types => true }) }
    end
  end
  
  # Allows the Android to retrieve information from the server.
  def fetch
    if params[:id].present?
      @character = Character.find(params[:id])
    elsif current_character.present?
      @character = current_character
    end
    
    if @character.present?
      # Touch the character so we know he's online
      @character.touch
      
      @characters = Character.find(:all, :conditions => ['updated_at > ?', 1.minute.ago]).delete_if { |c| c.id == @character.id }
      
      if @character.battle.present?
        @battle = HashWithIndifferentAccess.new({
          :id => @character.battle.id,
          :initiator => @character.battle.initiator.id,
          :status => @character.battle.status.to_s
        })
        if @character.opponent.present?
          @battle[:opponent] = @character.opponent.id
        else
          @battle[:opponent] = 0  # The "nil" opponent
        end
      end
      
      # The XML view template is at views/androids/fetch.xml.builder
      render 'fetch.xml.builder'
    else
      @response = { :status => 'error',
                    :message => 'Character could not be found.' }
      render :xml => @response.to_xml({
          :root => 'updates',
          :skip_types => true })
    end
  end
  
  # This is a test action that generates a starter database
  def generate
    @warrior = CharacterClass.create!({
      :name => 'Warrior',
      :description => "A fierce brawler, the Warrior has versatile skills that scale well.",
      :health => 100
    })
    @paladin = CharacterClass.create!({
      :name => 'Paladin',
      :description => "A holy warrior, the Paladin's greatest strength is his fortitude.",
      :health => 140
    })
    @assassin = CharacterClass.create!({
      :name => 'Assassin',
      :description => "Silent, but deadly, the Assassin specializes in massive explosions of damage.",
      :health => 60
    })
    @warrior.create_skill!({
      :name => 'Slap',
      :min_level => 1,
      :formula => 'random(8)+1'
    })
    @warrior.create_skill!({
      :name => 'Strike',
      :min_level => 2,
      :formula => 'c_lvl*3 + 5',
      :skill_type => 2
    })
    @paladin.create_skill!({
      :name => 'Pray',
      :min_level => 1,
      :formula => 'random(c_lvl)+2'
    })
    @paladin.create_skill!({
      :name => 'Cross',
      :min_level => 2,
      :formula => 'c_hp/10',
      :skill_type => 2
    })
    @assassin.create_skill!({
      :name => 'Fart',
      :min_level => 1,
      :formula => '10'
    })
    @assassin.create_skill!({
      :name => 'Big One',
      :min_level => 2,
      :formula => 'random(1)*30',
      :skill_type => 2
    })
    
    # Generate Ultimate Skills
    # skill_type = 0 specifies accelerometer-type attack
    @warrior.create_skill!({
      :name => 'Omnislap',
      :min_level => 1,
      :formula => 'c_lvl+8+random(c_lvl+8)+random(c_lvl+8)+random(c_lvl+8)+random(c_lvl+8)',
      :skill_type => 0
    })
    @paladin.create_skill!({
      :name => 'Judgement',
      :min_level => 1,
      :formula => 'random(c_maxhp)*0.8',
      :skill_type => 0
    })
    @assassin.create_skill!({
      :name => 'Rasengan',
      :min_level => 1,
      :formula => '10+c_lvl*2+c_hp/2',
      :skill_type => 0
    })
    
    @karl = @warrior.create_character!(:name => 'Karl', :password => 'password', :latitude => '37.875518', :longitude => '-122.258759')
    @saung = @assassin.create_character!(:name => 'Saung', :password => 'password', :latitude => '37.875450', :longitude => '-122.259368')
    @glen = @paladin.create_character!(:name => 'Glen', :password => 'password', :latitude => '37.875260', :longitude => '-122.258796')
    
    flash[:notice] = "Stuff generated!"
    redirect_to root_path
  end
  
end
