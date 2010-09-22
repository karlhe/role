package ucb.cs169.project7;

public class CharacterType {
	Assassin assassin;
	Warrior warrior;
	Paladin paladin;
	
	//id name level health experience
	
	public CharacterType() {
		assassin = new Assassin();
		warrior = new Warrior();
		paladin = new Paladin();
	}
	
	public class Assassin extends CharacterClass {
		public Assassin() {
			setName("Assassin");
			setHealth(100);
			setAttack(125);
			setDefense(50);
			setStealth(150);
			setDescription("A specialist in stealth. Difficult to detect.");
		}
	}
	
	public class Warrior extends CharacterClass {
		public Warrior() {
			setName("Warrior");
			setHealth(120);
			setAttack(150);
			setDefense(75);
			setStealth(50);
			setDescription("A fearless fighter with tremendous strength.");
		}
	}
	
	public class Paladin extends CharacterClass {
		public Paladin() {
			setName("Paladin");
			setHealth(120);
			setAttack(75);
			setDefense(150);
			setStealth(80);
			setDescription("A strong defender of the heavens.");
		}
	}
	
	public Assassin getAssassin() {
		return assassin;
	}
	
	public Warrior getWarrior() {
		return warrior;
	}
	
	public Paladin getPaladin() {
		return paladin;
	}
	
	public Assassin getNewAssassin() {
		Assassin a = new Assassin();
		return a;
	}
	
	public Warrior getNewWarrior() {
		Warrior w = new Warrior();
		return w;
	}
	
	public Paladin getNewPaladin() {
		Paladin p = new Paladin();
		return p;	
	}
	
}