import java.io.Serializable;

public class Question implements Serializable {
	private String question;
	private String answer;
	private int value;
	private boolean chosen;
	
	//constructor
	public Question(){
		question = null;
		answer = null;
		value = 0;
		chosen = false;
	}
	//constructor with question, answer, and value
	public Question(String question, String answer, int value){
		this.question = question;
		this.answer = answer;
		this.value = value;
		chosen = false;
	}
	
	public void setQuestion(String question){
		this.question = question;
	}
	public void setAnswer(String answer){
		this.answer = answer;
	}
	public void setValue(int value){
		this.value = value;
	}
	
	public String getQuestion(){
		return question;
	}
	public String getAnswer(){
		return answer;
	}
	public int getValue(){
		return value;
	}
	
	public void setChosen(boolean chosen){
		this.chosen = chosen;
	}
	public boolean getChosen(){
		return chosen;
	}
}
