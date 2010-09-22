package ucb.cs169.project7;

public abstract class CharacterClass {
	protected String className;
	protected int id; //TODO use IDs for server.
	protected int startHealth;
	protected int startAttack;
	protected int startDefense;
	protected int startStealth;
	protected Skill[] startSkills;
	protected String description;

	public void setName(String name) {
		className = name;
	}	
	
	public String getName() {
		return className;
	}
	
	public void setSkills(Skill[] skills) {
		startSkills = skills;
	}
	
	public Skill[] getSkills() {
		return startSkills;
	}
	
	public void setHealth(int val) {
		startHealth = val;
	}
	
	public int getHealth() {
		return startHealth;
	}
	
	public void setAttack(int val) {
		startAttack = val;
	}
	
	public int getAttack() {
		return startAttack;
	}
	
	public void setDefense(int val) {
		startDefense = val;
	}
	
	public int getDefense() {
		return startDefense;
	}
	
	public void setStealth(int val) {
		startStealth = val;
	}
	
	public int getStealth() {
		return startStealth;
	}
	
	public void setDescription(String desc) {
		description = desc;
	}
	
	public String getDescription() {
		return description;
	}
}
