
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class PlayerServer extends Thread {
	
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private StartWindow sw;
	private Socket s;
	
	private GameData updatedGameData;
	private String teamName;
	private int teamNumber;
	
	private MainWindow mw;
	
	public PlayerServer(String hostname, int port, StartWindow startWindow) {

		this.sw = startWindow;
		try {
			s = new Socket(hostname, port);
			ois = new ObjectInputStream(s.getInputStream());
			oos = new ObjectOutputStream(s.getOutputStream());
			
			teamName = sw.getGameData().getTeams().get(0).getName(); //set the team name
			//SEND THE TEAMNAME TO BE ADDED TO THE LIST
			WaitingMessage m = new WaitingMessage();
			m.setTeamName(sw.getGameData().getTeams().get(0).getName());
			oos.writeObject(m);
			oos.flush();

			this.start(); //run method and rest of constructor will be running at the same time
		} catch(IOException ioe) {

		}
	}
	
	public void run() {
		try {
			while (true) {
				//receive text from the main server
				//when a player join, update the waiting text
				Message m = (Message) ois.readObject();
				
				//this object is received when we wait
				if(m instanceof WaitingMessage){
					WaitingMessage wm = (WaitingMessage) m;
					if(wm.isDoneAccepting()){ //done receiving players
						//get the updated game data from server
						updatedGameData = wm.getGameData();
						//get the team Number (which turn)
						for(int i=0; i< wm.getTeamNames().size(); i++){
							if(wm.getTeamNames().get(i).equals(teamName)){
								teamNumber = i; //you'll compare this with currennt team's turn
							}
						}
						int randomTurn = wm.getRandomTurn(); // rand num of the team to go first
						mw = new MainWindow(updatedGameData, this, randomTurn);
						sw.dispose();
						mw.setVisible(true);
					}
					//update the waiting label
					int wait = wm.getNumOfWaiting();
					if(wait>0){
						sw.setWaitingText(String.valueOf("Waiting for " + wait + " players to join"));
					}
				}
				//during game play
				else if(m instanceof GameMessage){
					GameMessage gm = (GameMessage) m;
					
					//for each question, disable buttons as neccessary
					if(gm.getCommand().equals("disable question")){
						if(teamNumber != gm.getTurn()){
							mw.disableAllQuestions();
						}
					}
					//set current turn of the team
					else if(gm.getCommand().equals("set turn")){
						mw.setTurn(gm.getTurn());
					}
					//when currentplayer click on a button (choose question)
					else if(gm.getCommand().equals("display question")){
						if(teamNumber != gm.getTurn()){
							int action = gm.getAction();
							mw.displayQuestion(action);
							mw.setSubmitAnswer(false);
						}
						else{
							mw.setWarningText("answer within 20 seconds!");
							mw.displayTeamName(gm.getTurn());
						}
					} //when currentplayer get the answer right
					else if(gm.getCommand().equals("update when correct")){
						String currentTeam = gm.getCurrentTeam();
						String user_answer = gm.getUser_answer();
						int value = gm.getValue();
						
						if(!teamName.equals(gm.getCurrentTeam())){
							mw.updateWhenCorrect(currentTeam, user_answer, value);
							mw.disableAllQuestions();
						}
						else{
							mw.enabledUnansweredQuestions();
						}
					}
					else if(gm.getCommand().equals("first chance warning update")){
						String currentTeam = gm.getCurrentTeam();
						if(!teamName.equals(gm.getCurrentTeam())){
							mw.firstChanceWarningUpdate(currentTeam);
						}
					}
					else if(gm.getCommand().equals("update when wrong")){
						String currentTeam = gm.getCurrentTeam();
						String answer = gm.getUser_answer();
						int value = gm.getValue();
						if(!teamName.equals(gm.getCurrentTeam())){
							mw.updateWhenWrong(currentTeam, answer, value, getTeamName(), getTeamTurn());
						}
					}
					else if(gm.getCommand().equals("update after buzz in")){
						String currentTeamname = gm.getCurrentTeam();
						int currentPlayerTurn = gm.getCurrentTeamTurn();
						if(!teamName.equals(gm.getCurrentTeam())){
							mw.updateAfterBuzzIn(currentTeamname, currentPlayerTurn);
						}
					}
				} else if(m instanceof FinalMessage){
					FinalMessage fm = (FinalMessage) m;
					
					if(fm.getCommand().equals("answer final question")){
						int currentTeamIndex = fm.getTurn();
						if(teamNumber!=(currentTeamIndex)){
							mw.answerFinalQuestion();
						}
					}
					else if(fm.getCommand().equals("update other team's bet")){
						int currentTeamIndex = fm.getTurn();
						int bet = fm.getOtherTeamBet();
						mw.updateOtherTeamsBet(currentTeamIndex, bet);
						
					}
					else if(fm.getCommand().equals("update bet waiting status")){
						int currentTeamIndex = fm.getTurn();
						String thisTeamName = fm.getTeamName();
						if(teamNumber!=(currentTeamIndex)){
							mw.updateBetWaitingStatus(teamName);
						}
					}
					else if(fm.getCommand().equals("keep track final answered team")){
						int x = fm.getFinalAnsweredTeams();
						mw.setFinalAnswerTeams(x);
					}
					else if(fm.getCommand().equals("check final answer")){
						//need to distinguish who is eligible for final
						if(mw.getTeams().get(teamNumber).getBet()!=0){
							mw.checkFinalAnswer();
						}
					}
					else if(fm.getCommand().equals("update after team answers final question")){
						String otherTeamName = fm.getTeamName();
						int otherTeamTurn = fm.getTurn();
						boolean correct = fm.isFinalQuestionCorrect();
						int bet = fm.getOtherTeamBet();
						//if you are not the one that sent it and you are eligible for final
						if(!teamName.equals(otherTeamName) && mw.getTeams().get(teamNumber).getBet()!=0){
							mw.updateAfterTeamAnswersFinalQuestion(correct, otherTeamName, bet, otherTeamTurn);
						}
					}
				} else if(m instanceof MenuMessage){
					MenuMessage mm = (MenuMessage) m;
					if(mm.getCommand().equals("restart game")){
						int newTurn = mm.getNewTurn();
						mw.restartGameNetwork(newTurn);
					} else if(mm.getCommand().equals("choose new file")){
						int currentTeamTurn = mm.getTeamNumber();
						if(currentTeamTurn == teamNumber){
							closeSocket();
							mw.dispose();
							StartWindow sw = new StartWindow(teamName);
							sw.setVisible(true);
						}
						else{
							mw.goToStartWindowWhenSomeoneExit();
						}
					} else if(mm.getCommand().equals("exit")){
						int currentTeamTurn = mm.getTeamNumber();
						if(currentTeamTurn == teamNumber){
							closeSocket();
							mw.dispose();
							System.exit(0);
						}else{
							mw.goToStartWindowWhenSomeoneExit();
						}
					} else if(mm.getCommand().equals("logout")){
						int currentTeamTurn = mm.getTeamNumber();
						if(currentTeamTurn == teamNumber){
							closeSocket();
							mw.dispose();
							LoginWindow lw = new LoginWindow();
							lw.setVisible(true);
						}else{
							mw.goToStartWindowWhenSomeoneExit();
						}
					}
				}
			}
		}
		catch (IOException ioe) {
			//this is when host disconnects/ enabled all the buttons of other windows
			sw.setWaitingText("So sorry, the host canceled the game! Please choose another game to join");
			sw.buttonsWhileWaiting(true);
		}
		catch (ClassNotFoundException cnfe) {
			
		} 
	}
	//when client logs out during waiting state
	public void clientLogsOut(String clientName){
		
		//send only if it's this client, tell it to log out
		if(clientName.equals(teamName)){
			WaitingMessage wm = new WaitingMessage();
			wm.setCommand("client logs out");
			try {
				oos.writeObject(wm);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//every new question, disable buttons for teams that are not their turn
	public void disableQuestions(int turn){

		GameMessage gm = new GameMessage();
		gm.setCommand("disable question");
		gm.setTurn(turn);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
		
		}
	}
	//set turn
	public void setTurn(int turn){
		GameMessage gm = new GameMessage();
		gm.setCommand("set turn");
		gm.setTurn(turn);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//display a correct question
	public void displayQuestion(int action, int turn){
		GameMessage gm = new GameMessage();
		gm.setCommand("display question");
		gm.setTurn(turn);
		gm.setAction(action);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//update changes when currentplayer get somethign correct
	public void updateWhenCorrect(String currentTeam, String user_answer, int value){
		GameMessage gm = new GameMessage();
		gm.setCommand("update when correct");
		gm.setCurrentTeam(currentTeam);
		gm.setUser_answer(user_answer);
		gm.setValue(value);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//update other players game progress when first chance is used{
	public void firstChanceWarningUpdate(String currentTeam){
		GameMessage gm = new GameMessage();
		gm.setCommand("first chance warning update");
		gm.setCurrentTeam(currentTeam);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//update other players when wrong
	public void updateWhenWrong(String currentTeam, String answer, int value){
		GameMessage gm = new GameMessage();
		gm.setCommand("update when wrong");
		gm.setCurrentTeam(currentTeam);
		gm.setUser_answer(answer);
		gm.setValue(value);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//update after someon buzzes in
	public void updateAfterBuzzIn(String currentTeam, int currentTeamTurn){
		GameMessage gm = new GameMessage();
		gm.setCommand("update after buzz in");
		gm.setCurrentTeam(currentTeam);
		gm.setCurrentTeamTurn(currentTeamTurn);
		try {
			oos.writeObject(gm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void answerFinalQuestion(int turn){
		FinalMessage fm = new FinalMessage();
		fm.setCommand("answer final question");
		fm.setTurn(turn);
		try {
			oos.writeObject(fm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void updateBetWaitingStatus(int turn, String thisTeamName){
		FinalMessage fm = new FinalMessage();
		fm.setCommand("update bet waiting status");
		fm.setTurn(turn);
		fm.setTeamName(thisTeamName);
		try {
			oos.writeObject(fm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void keepTrackFinalAnsweredTeam(int finalAnsweredTeams){
		FinalMessage fm = new FinalMessage();
		fm.setCommand("keep track final answered team");
		fm.setFinalAnsweredTeams(finalAnsweredTeams);
		try {
			oos.writeObject(fm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void updateOtherTeamsBet(int currentTeam, int bet){
		

		FinalMessage fm = new FinalMessage();
		fm.setCommand("update other team's bet");
		fm.setTurn(currentTeam);
		fm.setOtherTeamBet(bet);
		
		try {
			oos.writeObject(fm);
			oos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void checkFinalAnswer(){
		FinalMessage fm = new FinalMessage();
		fm.setCommand("check final answer");
		try {
			oos.writeObject(fm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void updateAfterTeamAnswersFinalQuestion(boolean finalQuestionCorrect, String name, int bet, int teamTurn){
		FinalMessage fm = new FinalMessage();
		fm.setCommand("update after team answers final question");
		fm.setTurn(teamTurn);
		fm.setOtherTeamBet(bet);
		fm.setTeamName(name);
		fm.setFinalQuestionCorrect(finalQuestionCorrect);
		try {
			oos.writeObject(fm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//when restart is pressed
	public void restartGame(int newTurn){
		MenuMessage mm = new MenuMessage();
		mm.setCommand("restart game");
		mm.setNewTurn(newTurn);
		try {
			oos.writeObject(mm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void chooseNewFile(int teamTurn){
		MenuMessage mm = new MenuMessage();
		mm.setCommand("choose new file");
		mm.setTeamNumber(teamTurn);
		try {
			oos.writeObject(mm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void exit(int teamTurn){
		MenuMessage mm = new MenuMessage();
		mm.setCommand("exit");
		mm.setTeamNumber(teamTurn);
		try {
			oos.writeObject(mm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void logout(int teamTurn){
		MenuMessage mm = new MenuMessage();
		mm.setCommand("logout");
		mm.setTeamNumber(teamTurn);
		try {
			oos.writeObject(mm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void closeSocket(){
		
		try{
			if (ois != null) {
				ois.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (s!= null) {
				s.close();
			}
		} catch (IOException ioe) {

		}
	}
	//close host server
	public void closeHostServer(){
		if(teamNumber==0){
			MenuMessage mm = new MenuMessage();
			mm.setCommand("close host server");
			try {
				oos.writeObject(mm);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public String getTeamName(){
		return teamName;
	}
	public int getTeamTurn(){
		return teamNumber;
	}
	
	

	
}
