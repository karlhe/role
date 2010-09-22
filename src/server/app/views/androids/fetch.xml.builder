# This Ruby code generates an XML view.

xml.instruct!
xml.updates do
  xml.health @character.health
  xml.experience @character.experience
  xml.level @character.level
  
  # Only add this block if the player is checking his location.
  xml.locations do
    @characters.each do |char|
      xml.character do
        xml.id char.id
        xml.name char.name
        xml.location do
          xml.latitude char.latitude
          xml.longitude char.longitude
        end
      end
    end
  end
  
  # Only add this if there is a battle request.
  if @battle.present?
    xml.battle do
      xml.id @battle['id']
      xml.initiator @battle['initiator']
      xml.opponent @battle['opponent']
      xml.status @battle['status']
    end
  end
  
end