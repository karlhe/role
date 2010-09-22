xml.instruct!

xml.character do
  xml.id @character.id
  xml.status @character.status.to_s
  xml.name @character.name
  xml.level @character.level
  xml.class @character.character_class.name
  xml.health @character.health
  xml.tag! 'max-health', @character.max_health
  xml.experience @character.experience
  xml.tag! 'tnl', @character.to_next_level
  xml.skills do
    @character.abilities.each do |a|
    xml.skill do
      xml.tag! 'skill-id', a.id
      xml.tag! 'skill-name', a.skill.name
      xml.tag! 'skill-level', a.level
      xml.tag! 'skill-type', a.skill.type_desc.to_s
    end
    end
  end
end
