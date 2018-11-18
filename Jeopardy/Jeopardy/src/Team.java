import java.io.Serializable;

public class Team implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private int point;
	private int lastBet;
	private boolean finalCorrect;
	
	//constructor
	public Team(){
		point = 0;
		lastBet = 0;
		finalCorrect = false;
	}
	//constructor with name
	public Team(String name){
		this();
		this.name = name;
		
	}
	
	//set name
	public void setName(String name){
		this.name = name;
	}
	//return name
	public String getName(){
		return name;
	}

	//when you are right
	public void correct(int num){
		point += num;
	}
	//when you are wrong
	public void wrong(int num){
		point -= num;
	}
	//get point
	public int getPoint(){
		return point;
	}
	
	public void setBet(int bet){
		lastBet = bet;
	}
	public int getBet(){
		return lastBet;
	}
	
	//final question right or wrong?
	public void setFinalCorrect(){
		finalCorrect = true;
	}
	public boolean getfinalCorrect(){
		return finalCorrect;
	}
	public void resetValues(){
		point = 0;
		lastBet = 0;
		finalCorrect = false;
	}
}
