import java.io.Serializable;
import java.util.ArrayList;

public class WaitingMessage extends Message implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private int numOfWaiting;
	private boolean doneAccepting;
	private String teamName;
	private ArrayList<String> teamNames;
	private GameData gameData;
	private int randomTurn;
	private String command;

	public WaitingMessage(){
		numOfWaiting = 0;
		doneAccepting = false;
		setTeamNames(new ArrayList<>());
	
	}
	public WaitingMessage(int num){
		this();
		numOfWaiting = num;
	}
	
	public int getNumOfWaiting(){
		return numOfWaiting;
	}
	
	public void setDoneAceepting(Boolean bool){
		doneAccepting = bool;
	}
	public Boolean isDoneAccepting(){
		return doneAccepting;
	}
	
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	public ArrayList<String> getTeamNames() {
		return teamNames;
	}
	public void setTeamNames(ArrayList<String> teamNames) {
		this.teamNames = teamNames;
	}
	
	public void setGameData(GameData gameData) {
		this.gameData = gameData;
	}
	public GameData getGameData() {
		return gameData;
	}
	public int getRandomTurn() {
		return randomTurn;
	}
	public void setRandomTurn(int randomTurn) {
		this.randomTurn = randomTurn;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}

}
