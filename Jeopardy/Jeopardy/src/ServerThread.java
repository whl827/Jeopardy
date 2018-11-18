import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread{
	
	private Socket s;
	private HostServer cs;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	//for adding name to server
	boolean clientNameReceived;
	
	public ServerThread(Socket s, HostServer cs) {
		this.s = s;
		this.cs = cs;
		clientNameReceived = false;
		
		try {	
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
			
		} catch (IOException ioe) {

		} 
	}
	public void run() {
		try {
			while (true) {
				Message m = (Message) ois.readObject();
					
				if(m instanceof WaitingMessage){
					WaitingMessage wm = (WaitingMessage) m;
					if(wm.getCommand()!=null){
						if(wm.getCommand().equals("client logs out")){
							removeClient();
						}
					}
					//if client first comes in, add the to arraylist
					if(!wm.getTeamName().isEmpty()){
						cs.addTeam(wm.getTeamName());
						clientNameReceived = true;
					}
					cs.sendMessageToAllClients(wm);
				}
				else if(m instanceof GameMessage){
					GameMessage gm = (GameMessage) m;
					cs.sendMessageToAllClients(gm);
				} else if(m instanceof FinalMessage){
					FinalMessage fm = (FinalMessage) m;
					cs.sendMessageToAllClients(fm);
				} else if(m instanceof MenuMessage){
					MenuMessage mm = (MenuMessage) m;
					//close host server
					if(mm.getCommand()!=null){
						if(mm.getCommand().equals("close host server")){
							cs.closeSocket();
						}
					}
					cs.sendMessageToAllClients(mm);
				}
				oos.flush();
			}
		} catch (IOException ioe) {
			//this is when someone disconnects in the middle of waiting period
			removeClient();
			

		} catch (ClassNotFoundException cnfe) {

		}
	}
	public void sendMessage(Message m) {
		try {
			oos.writeObject(m);;
			oos.flush();
		} catch (IOException ioe) {
			try {
				if(s!=null){
					s.close();
				}
			} catch (IOException e) {

			}
		}
	}
	public Socket getSocket(){
		return s;
	}
	
	public Boolean getClientNameReceived(){
		return clientNameReceived;
	}
	public void closeSocket(){
		try{
			if (oos != null) {
				oos.close();
			}
			if (ois != null) {
				ois.close();
			}
			if (s!= null) {
				s.close();
			}
		} catch (IOException ioe) {
		}
	}
	public void removeClient(){
		try { if(s!=null){ s.close();}} //close the socket that is getting disconnected
		catch (IOException e) { 
			
		}
		cs.deleteThread(this); //tell server to delete the thread and update the text
	}

}
