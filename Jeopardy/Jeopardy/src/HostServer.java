import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class HostServer extends Thread {
	
	private GameData gameData;
	private Vector<ServerThread> serverThreads; 
	private ServerSocket ss;
	private int port;
	
	private ArrayList<String> teamNames;
	
	
	public HostServer(int port, GameData gameData) {
	
		this.gameData = gameData;
		serverThreads = new Vector<ServerThread>();
		ServerSocket ss = null;
		this.port=port;
		teamNames = new ArrayList<String>();
		
		
		start();
	}
	@Override
	public void run() {
		
		
		//this loop ends when we finish accepting the players
		try {
			ss  = new ServerSocket(port);
			while (true) {
				
				//wait for client to join
				Socket s = ss.accept();
				//add it to the serverthread(contains all clients)
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
				
				//get the number of players we need to wait in order for game to start
				int numOfPlayerWaiting = gameData.getNumOfTeam()-serverThreads.size();
				//if done accepting players, close and open main window
				if(numOfPlayerWaiting==0){
					break;
				}
				//update the number of players waiting whenever client joins
				WaitingMessage m = new WaitingMessage(numOfPlayerWaiting);
				sendMessageToAllClients(m);
		  }
		} catch(IOException ioe) {
			closeSocket();
			return;
			
		}
		//you are now done accepting players.,
		while(!getAllClientNameReceived()){
			//keep waiting to until all names get added
		}
		//now all clients names are received
		WaitingMessage m = new WaitingMessage();
		//pass game data and send message to client to open the window
		m.setDoneAceepting(true); //notify that waiting is done
		//make the array list of teams
		ArrayList<Team> teams = new ArrayList<>();
		for(int i=0; i<teamNames.size(); i++){
			Team team = new Team(teamNames.get(i));
			teams.add(team);
		}
		gameData.setTeams(teams);
		m.setTeamNames(teamNames);
		m.setGameData(gameData);
		
		Random rand = new Random();
		int randomTurn = rand.nextInt(serverThreads.size());
		m.setRandomTurn(randomTurn);
		sendMessageToAllClients(m);
		
	}
	//send messages to all client from server
	public void sendMessageToAllClients(Message m) {
		if (m != null) {
			synchronized(serverThreads) {
				for (ServerThread st : serverThreads) {
					st.sendMessage(m);
				}
			}
		}
	}
	//delete thread if client leaves
	public void deleteThread(ServerThread st){
		//look for the same thread in serverthreads arraylist
		for(int i=0; i<serverThreads.size(); i++){
			if(st.equals(serverThreads.get(i))){
				serverThreads.remove(i);
				//remove the team name as well
				teamNames.remove(i);
			}
		}
		//update the waiting text number; 
		WaitingMessage m = new WaitingMessage(gameData.getNumOfTeam()-serverThreads.size());
		sendMessageToAllClients(m);
	}
	//keep all the team name
	public void addTeam(String name){
		teamNames.add(name);
	}
	//check if all teams name is received
	public Boolean getAllClientNameReceived(){
		for(ServerThread s: serverThreads){
			if(s.getClientNameReceived()==false){
				return false; // some name is not received yet
			}
		}
		return true; //everyone's name is set
	}
	//close socket
	public void closeSocket(){
		for(ServerThread s: serverThreads){
			s.closeSocket();
		}
		try{
			if(ss!=null){
				ss.close();
			}
		} catch (IOException ioe) {
		}
	}

}
