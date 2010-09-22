package ucb.cs169.project7;

public class Player {
	private int id;
	private String name;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}
}