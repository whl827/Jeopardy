import java.io.Serializable;

public class GameMessage extends Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String command;
	//used for disabling buttons for teams that are not their turn
	private int turn;
	//used for displaying a question
	private int action; //action number for which button is pressed
	private int currentPlayerturn;
	
	
	private String currentTeam, user_answer;
	private int value;
	
	GameMessage(){}

	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public int getTurn() {
		return turn;
	}
	public void setTurn(int turn) {
		this.turn = turn;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}

	public String getCurrentTeam() {
		return currentTeam;
	}

	public void setCurrentTeam(String currentTeam) {
		this.currentTeam = currentTeam;
	}

	public String getUser_answer() {
		return user_answer;
	}

	public void setUser_answer(String user_answer) {
		this.user_answer = user_answer;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getCurrentTeamTurn() {
		return currentPlayerturn;
	}

	public void setCurrentTeamTurn(int currentTeamTurn) {
		this.currentPlayerturn = currentTeamTurn;
	}
}
