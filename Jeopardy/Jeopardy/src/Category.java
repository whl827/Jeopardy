

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Category implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String catName;
	private ArrayList<Question> questions;
	private Question q1;
	private Question q2;
	private Question q3;
	private Question q4;
	private Question q5;
	private int maxQuestion;
	
	public Category(){
		questions = new ArrayList<Question>();
		q1 = new Question();
		q2 = new Question();
		q3 = new Question();
		q4 = new Question();
		q5 = new Question();
		questions.add(q1);
		questions.add(q2);
		questions.add(q3);
		questions.add(q4);
		questions.add(q5);
		maxQuestion = 0;
	}
	
	//constructor
	public Category(String catName){
		this();
		this.catName = catName;
		
	
	}
	//set name
	public void setName(String catName){
		this.catName = catName;
	}
	//return name
	public String getName(){
		return catName;
	}
	
	//return number of question
	public int numOfQuestions(){
		return questions.size();
	}
	
	public boolean addQuestion(int value, String question, String answer){
		//max number of questions
		if(maxQuestion == 5){
			JOptionPane optionPane = new JOptionPane("This Category has reached its limit. It cannot contain more than 5 questions" 
													   + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
			JDialog invalidFileError = new JDialog();
			invalidFileError = optionPane.createDialog("Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		//check for multiple value 
		for(int i=0; i<questions.size(); i++){
			if(questions.get(i).getValue() == value){	
				JOptionPane optionPane = new JOptionPane("Multiple Value Point in same category" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
				JDialog invalidFileError = new JDialog();
				invalidFileError = optionPane.createDialog("Invalid File");
				invalidFileError.setVisible(true);
				return false;
			}
		}
		questions.get(maxQuestion).setValue(value);
		questions.get(maxQuestion).setQuestion(question);
		questions.get(maxQuestion).setAnswer(answer);
		maxQuestion++;
		return true;
	}
		
	//return question
	public String getQuestion(int index){
		return questions.get(index).getQuestion();
	}//return answer
	public String getAnswer(int index){
		return questions.get(index).getAnswer();
	}//return point value
	public int getValue(int index){
		return questions.get(index).getValue();
	}
	
	//set question to chosen
	public void setChosen(int index){
		questions.get(index).setChosen(true);
	}
	//check if question has been chosen
	public boolean getChosen(int index){
		return questions.get(index).getChosen();

	}
	//get question number
	public int getQuestionNum(int value){
		for(int i=0; i<questions.size(); i++){
			//question with same value
			if(questions.get(i).getValue() == value){
				return i;
			}
		}
		return -1;
	}
	//when replay, reset chosen to all false
	public void resetValues(){
		//reset chosen to false
		for(int i=0; i<questions.size(); i++){
			questions.get(i).setChosen(false);
		}
		maxQuestion = 0; //reset maxquestion
	}
}
