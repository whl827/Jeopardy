import java.io.Serializable;

public class FinalMessage extends Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String command;
	private int turn;
	private int otherTeamBet;
	private int finalAnsweredTeams; //keep track of number of team fnished
	
	
	private boolean finalQuestionCorrect;
	private String teamName; 

	
	FinalMessage(){}
	
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

	public int getFinalAnsweredTeams() {
		return finalAnsweredTeams;
	}

	public void setFinalAnsweredTeams(int finalAnsweredTeams) {
		this.finalAnsweredTeams = finalAnsweredTeams;
	}

	public int getOtherTeamBet() {
		return otherTeamBet;
	}

	public void setOtherTeamBet(int otherTeamBet) {
		this.otherTeamBet = otherTeamBet;
	}

	public boolean isFinalQuestionCorrect() {
		return finalQuestionCorrect;
	}

	public void setFinalQuestionCorrect(boolean finalQuestionCorrect) {
		this.finalQuestionCorrect = finalQuestionCorrect;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	

}
