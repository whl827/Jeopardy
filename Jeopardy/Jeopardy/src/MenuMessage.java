import java.io.Serializable;

public class MenuMessage extends Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String command;
	private String teamName;
	private int teamNumber;
	private int newTurn;
	
	MenuMessage(){}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public void setTeamNumber(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public int getNewTurn() {
		return newTurn;
	}

	public void setNewTurn(int newTurn) {
		this.newTurn = newTurn;
	}


}
