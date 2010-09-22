# Manages interfaces for sending and receiving of battle information.
class BattlesController < ApplicationController

  # Allows androids to post actions for battles.
  def update
    success = true
    @update = params[:battle]
    if @update['id'].present?
      @character = Character.find(@update['id'])
    elsif current_character.present?
      @character = current_character
    else
      success = false
      @response = { :status => 'error',
                    :message => 'Could not find Character.' }
    end
    
    @opponent = Character.find(@update['opponent'])
    @action = @update['action']
    @battle = @character.battle
    
    if @opponent.blank? or
          @opponent == @character or
          (@battle.present? and not @battle.characters.include?(@opponent))
      success = false
    elsif @action['type'] == 'fight'
      action = @battle.fight!(@character,@opponent,@action['ability'])
      if action.blank?
        success = false
        @response = { :status => 'error',
                      :message => 'Fight action was invalid.' }
      end
    else
      result = Battle.initiate!(@character,@opponent,@action['type'])
      if not result
        success = false
        case @action['type']
          when  'initiate'
            error = 'Initiation has failed.'
          when 'accept'
            error = 'Accept has failed.'
          when 'reject'
            error = 'Reject has failed.'
          when 'run'
            error = 'Run has failed.'
          else
            error = 'Action type was invalid.'
        end
        
        if @character.battle.present?
          error += ' You are in battle.'
        elsif @opponent.battle.present?
          error += ' Opponent is in battle.'
        end
        
        @response = { :status => 'error',
                      :message => error }
      end
    end
    
    # Create hash to signify to Android that the post succeeded.
    if success
      @battle.touch if @battle.present?
      @response = { :status => 'success',
                    :message => 'Battle request sent.' }
    # Create has to signify to Android that the post has failed.
    else
      # Use existing response
    end
    
    respond_to do |format|
      # Convert @response to XML with root node "response"
      format.xml { render :xml => @response.to_xml({:root => 'battle', :skip_types => true}) }
    end
  end

  # Displays XML information on status of battle and players involved.
  def fetch
    # Retrieve pending battle information for current character
    if params[:id].present?
      @character = Character.find(params[:id])
    else
      @character = current_character
    end
    
    if @character.present?
      # Touch the player so we know he's online
      @character.touch
    
      @battle = Battle.send_updates(@character)
    else
      @battle = { :status => 'error',
                  :message => 'Character not found.' }
    end
    
    respond_to do |format|
      # Convert @battle to XML with root node "battle"
      format.xml { render :xml => @battle.to_xml({:root => 'battle', :skip_types => true}) }
    end
  end

end
