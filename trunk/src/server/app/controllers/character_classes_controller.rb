class CharacterClassesController < ApplicationController
  def index
    @character_classes = CharacterClass.all
    @hashes = []
    @character_classes.map do |character_class|
      @hashes.push(character_class.to_xml_hash)
    end
    
    respond_to do |format|
      format.xml { render :xml => @hashes.to_xml({
          :root => 'character_classes', :skip_types => true
        }) }
    end
  end
  
  def show
    @character_class = CharacterClass.find(params[:id])
    @hash = @character_class.to_xml_hash
    
    respond_to do |format|
      format.xml { render :xml => @hash.to_xml({
          :root => 'character_class', :skip_types => true
        }) }
    end
  end
end
