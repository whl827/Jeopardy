import java.io.Serializable;
import java.util.ArrayList;

public class GameData implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Team> teams;
	ArrayList<Category> categoryList;
	private String[] categories;//topics of categories
	private String[] points;//possible point values
	private int answeredQuestion;
	private int numOfTeam;
	private String finalQuestion;	
	private String finalAnswer;
	private String categoryLabelImagePath;
	private String enabledQuestionImagePath;
	private String disabledQuestionImagePath;
	private int totalPeopleRated;
	private int totalRate;
	private String fileDestination;
	private ArrayList<String> outFileArray;
	
	public GameData(ArrayList<Team> teams, ArrayList<Category> categoryList, 
			String[] categories, String[] points, int answeredQuestion, 
			int numOfTeam, String finalQuestion, String finalAnswer,
			String categoryLabelImagePath, String enabledQuestionImagePath, 
			String disabledQuestionImagePath, int totalPeopleRated, int totalRate, String fileDestination, ArrayList<String> outFileArray){
		
		
		this.teams = teams;
		this.categoryList = categoryList;
		this.categories = categories;
		this.points = points;
		this.answeredQuestion = answeredQuestion;
		this.numOfTeam = numOfTeam;
		this.finalQuestion = finalQuestion;
		this.finalAnswer = finalAnswer;
		this.categoryLabelImagePath = categoryLabelImagePath;
		this.enabledQuestionImagePath = enabledQuestionImagePath;
		this.disabledQuestionImagePath = disabledQuestionImagePath;
		this.totalPeopleRated = totalPeopleRated;
		this.totalRate = totalRate;
		this.fileDestination = fileDestination;
		this.outFileArray = outFileArray;
	
	}
	public ArrayList<Team> getTeams() {
		return teams;
	}
	public void setTeams(ArrayList<Team> teams) {
		this.teams = teams;
	}
	public ArrayList<Category> getCategoryList() {
		return categoryList;
	}
	public void setCategoryList(ArrayList<Category> categoryList) {
		this.categoryList = categoryList;
	}
	public String[] getCategories() {
		return categories;
	}
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public String[] getPoints() {
		return points;
	}
	public void setPoints(String[] points) {
		this.points = points;
	}
	public int getAnsweredQuestion() {
		return answeredQuestion;
	}
	public void setAnsweredQuestion(int answeredQuestion) {
		this.answeredQuestion = answeredQuestion;
	}
	public int getNumOfTeam() {
		return numOfTeam;
	}
	public void setNumOfTeam(int numOfTeam) {
		this.numOfTeam = numOfTeam;
	}
	public String getFinalQuestion() {
		return finalQuestion;
	}
	public void setFinalQuestion(String finalQuestion) {
		this.finalQuestion = finalQuestion;
	}
	public String getFinalAnswer() {
		return finalAnswer;
	}
	public void setFinalAnswer(String finalAnswer) {
		this.finalAnswer = finalAnswer;
	}
	public String getCategoryLabelImagePath() {
		return categoryLabelImagePath;
	}
	public void setCategoryLabelImagePath(String categoryLabelImagePath) {
		this.categoryLabelImagePath = categoryLabelImagePath;
	}
	public String getEnabledQuestionImagePath() {
		return enabledQuestionImagePath;
	}
	public void setEnabledQuestionImagePath(String enabledQuestionImagePath) {
		this.enabledQuestionImagePath = enabledQuestionImagePath;
	}
	public String getDisabledQuestionImagePath() {
		return disabledQuestionImagePath;
	}
	public void setDisabledQuestionImagePath(String disabledQuestionImagePath) {
		this.disabledQuestionImagePath = disabledQuestionImagePath;
	}
	public int getTotalPeopleRated() {
		return totalPeopleRated;
	}
	public void setTotalPeopleRated(int totalPeopleRated) {
		this.totalPeopleRated = totalPeopleRated;
	}
	public int getTotalRate() {
		return totalRate;
	}
	public void setTotalRate(int totalRate) {
		this.totalRate = totalRate;
	}
	public String getFileDestination() {
		return fileDestination;
	}
	public void setFileDestination(String fileDestination) {
		this.fileDestination = fileDestination;
	}
	public ArrayList<String> getOutFileArray() {
		return outFileArray;
	}
	public void setOutFileArray(ArrayList<String> outFileArray) {
		this.outFileArray = outFileArray;
	}
}
