

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class FileChecker {
	
	final int MAX_CAT = 5;
	final int MAX_POINTVALUE = 5;
	private ArrayList<Category> categoryList;
	private String[] categories;
	private String[] points;
	private String fq;
	private String fa;
	
	private String categoryLabelImagePath;
	private String enabledQuestionImagePath;
	private String disabledQuestionImagePath;
	
	private int totalPeopleRated;
	private int totalRate;
	private ArrayList<String> dataList;
	
	
	public ArrayList<String> getArrayList(){
		return dataList;
	}
	
	public int getTotalPeopleRated() {
		return totalPeopleRated;
	}
	public int getTotalRate() {
		return totalRate;
	}
	
	public String getCategoryLabelImagePath() {
		return categoryLabelImagePath;
	}
	public String getEnabledQuestionImagePath() {
		return enabledQuestionImagePath;
	}
	public String getDisabledQuestionImagePath() {
		return disabledQuestionImagePath;
	}
	public String getFinalQ(){
		return fq;
	}
	public String getFinalA(){
		return fa;
	}
	
	public FileChecker(){
		categoryList = new ArrayList<>();
		categories = null;
		points = null;
		fq = null;
		fa = null;
		//a3
		categoryLabelImagePath = null;
		enabledQuestionImagePath = null;
		disabledQuestionImagePath = null;
		totalPeopleRated = 0;
		totalRate = 0;
	}
	
	public void clearData(){
		categoryList.clear();
		categories = new String[0];
		points = new String[0];
		fq = null;
		fa = null;
	}
	
	public ArrayList<Category> getCategoryList(){
		return categoryList;
	}
	public String[] getCategories(){
		return categories;
	}
	public String[] getPoints(){
		return points;
	}
	public boolean checkFile(File text) throws FileNotFoundException{
		
		
		Scanner scan = new Scanner(text);

		dataList = new ArrayList<>();
		JDialog invalidFileError = new JDialog();
		
		if(!text.exists()){
			JOptionPane optionPane = new JOptionPane("File does not exist", JOptionPane.ERROR_MESSAGE, 
																			JOptionPane.DEFAULT_OPTION);
			invalidFileError = optionPane.createDialog(invalidFileError, "Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		//if file is empty
		if(text.length()==0){
			JOptionPane optionPane = new JOptionPane("File is Empty", JOptionPane.ERROR_MESSAGE, 
					JOptionPane.DEFAULT_OPTION);
			invalidFileError = optionPane.createDialog(invalidFileError, "Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		//Scanner scan = new Scanner(text);
		//break down first line
		String firstLine = scan.nextLine();
		//String firstLine = tempData.get(0);
		firstLine = firstLine.trim();
		categories = firstLine.split("::");
		//removing all the white spaces
		for(int i=0; i<categories.length;i ++){
			categories[i] = categories[i].trim().toLowerCase();
		}
		
		//when more or less categories appear
		//a3 includes an image in first line
		if(categories.length != 6){
			JOptionPane optionPane = new JOptionPane("It does not contain 5 categories!!!" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
			invalidFileError = optionPane.createDialog(invalidFileError, "Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		else{//look for duplicate category
			//store the image and recreate categories array
			categoryLabelImagePath = categories[categories.length-1];
			String[] temp = categories;
			categories = new String[MAX_CAT];
			for(int i=0; i<MAX_CAT; i++){
				categories[i] = temp[i];
			}
			
			for(int i=0; i<categories.length-1; i++){
				for(int j=i; j<categories.length-1; j++){
					if(categories[i].equalsIgnoreCase(categories[j+1])){
						JOptionPane optionPane = new JOptionPane("There is a duplciate category: " + categories[i].toLowerCase() + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
						invalidFileError = optionPane.createDialog("Invalid File");
						invalidFileError.setVisible(true);
						return false;
					}
				}
			}
		}
		//done with the first line, go to next line for values;
		String secondLine = scan.nextLine();
		//String secondLine = tempData.get(1);
		secondLine = secondLine.trim();
		points = secondLine.split("::");
		for(int i=0; i<points.length; i++){
			points[i] = points[i].trim();
		}

		//a3 include two images in second line
		if(points.length != 7){
			JOptionPane optionPane = new JOptionPane("It does not contain 5 point values" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
			invalidFileError = optionPane.createDialog("Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		else{
			//store two images path and recreate points array
			enabledQuestionImagePath = points[points.length-2];
			disabledQuestionImagePath = points[points.length-1];
			String[] temp = points;
			points = new String[MAX_POINTVALUE];
			for(int i=0; i<MAX_POINTVALUE; i++){
				points[i] = temp[i];
			}

			//look for duplicate category
			for(int i=0; i<points.length-1; i++){
				for(int j=i; j<points.length-1; j++){
					if( (Integer.parseInt(points[i])) == (Integer.parseInt(points[j+1])) ){
						JOptionPane optionPane = new JOptionPane("There is a duplicate point value: " + points[i].toLowerCase() + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
						invalidFileError = optionPane.createDialog("Invalid File");
						invalidFileError.setVisible(true);
						return false;
					}
				}
			}
		}
		//a3 work
		ArrayList<String> tempData = new ArrayList<>();
		//just read each line and store it to arrayList
		while(scan.hasNextLine()){
			tempData.add(scan.nextLine());
		}
		//get the num of people and total rate	
		try{
			totalPeopleRated = Integer.parseInt(tempData.get(tempData.size()-2));
			totalRate = Integer.parseInt(tempData.get(tempData.size()-1));
		} catch (IllegalArgumentException e){
			JOptionPane optionPane = new JOptionPane("Wrong Format (Rating)" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
			invalidFileError = optionPane.createDialog("Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		
		//remove last to lines
		tempData.remove(tempData.get(tempData.size()-1));
		tempData.remove(tempData.get(tempData.size()-1));

		boolean firstQ = true;
		//store data in a correct format
		//while(scan.hasNextLine()){
			
		//a3
		for(int i=0; i<tempData.size(); i++){
			//get the next line
			//String temp = scan.nextLine();
			
			String temp = tempData.get(i);
			temp = temp.trim();
			//detect blank line
			if(temp.isEmpty()){
				continue;
			}
			String firstCh = null;
			//new line symbol ::
			if(temp.length()>=2){
				firstCh = temp.substring(0,2);
			}
			//only have one character in a line (continuation)
			else{
				firstCh = temp.substring(0,1);
			}
			//if it's a new question, add the string
			if(firstCh.equals("::")){
				//take out "::" in front and add
				temp = temp.substring(2);
				dataList.add(temp);
			}
			//if it's a continuation
			else{
				//combine the string with multiple lines
				String newStr = dataList.get(dataList.size()-1) + " " + temp;
				dataList.set(dataList.size()-1, newStr);
			}
		}
		//close the Scanner
		scan.close();
		//too many questions stored error
		if(dataList.size() != 26){
			JOptionPane optionPane = new JOptionPane("This file does not contain 26 questions" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
			invalidFileError = optionPane.createDialog("Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		//declare variables
		String finalQuestion = null;
		String finalAnswer = null;
		
		//set to true when FJ is found, need to reset this in replay
		boolean finalQ = false;	
		
		//need to reset these value for every data list item
		boolean catExist = false;	//check if category exists
		boolean valExist = false;	//check if point value exists
		
		//make categories with name
		for(int i=0; i<categories.length; i++){
			Category cat = new Category(categories[i]);
			categoryList.add(cat);
		}
		//check format and store data
		for(int i=0; i<dataList.size(); i++){
			String[] data = dataList.get(i).split("::");
			//trim all the spaces
			for(int l=0; l<data.length; l++){
				data[l] = data[l].trim();
			}
			//If it's final question 
			if(data[0].equals("FJ")){
				//multiple final question error
				if(finalQ == true){
					JOptionPane optionPane = new JOptionPane("Cannot have multiple Final Question" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
					invalidFileError = optionPane.createDialog("Invalid File");
					invalidFileError.setVisible(true);
					return false;
				}
				//invalid input
				if(data.length != 3){
					JOptionPane optionPane = new JOptionPane("Wrong Format in Final Question" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
					invalidFileError = optionPane.createDialog("Invalid File");
					invalidFileError.setVisible(true);
					return false;
				}
				//final question has been set
				if(data.length == 3){			
					finalQuestion = data[1];
					finalAnswer = data[2];
					finalQ = true; 
					
					fq = finalQuestion;
					fa = finalAnswer;
				}
			}
			//If it's regular question
			else{
				//check if format is right
				if(data.length != 4){
					JOptionPane optionPane = new JOptionPane("Wrong Format of the question" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
					invalidFileError = optionPane.createDialog("Invalid File");
					invalidFileError.setVisible(true);
					return false;
				}
				//check if category is in one of the categories
				for(int j=0; j<categories.length; j++){					
					if((data[0].equalsIgnoreCase(categories[j]))){
						catExist = true;
						break;
					}
				}
				//category does not exist
				if(catExist == false){
					//System.err.println(data[0] + " This category does not exist");
					JOptionPane optionPane = new JOptionPane("The category does not exist" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
					invalidFileError = optionPane.createDialog("Invalid File");
					invalidFileError.setVisible(true);
					return false;
				}
				//check if point value  is in one of the value points
				for(int k=0; k<points.length; k++){
					if((Integer.parseInt(data[1])==Integer.parseInt(points[k]))){
						valExist = true;
						break;
					}
				}
				//point does not exist
				if(valExist == false){
					JOptionPane optionPane = new JOptionPane("This value point does not exist" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
					invalidFileError = optionPane.createDialog("Invalid File");
					invalidFileError.setVisible(true);
					return false;
				}
				//It passed Error Checking! Store value/questions/answer on correct category
				if(data[0].equalsIgnoreCase(categories[0])){
					if(categoryList.get(0).addQuestion(Integer.parseInt(data[1]), data[2], data[3]) == false){
						return false;
					};
				}
				else if(data[0].equalsIgnoreCase(categories[1])){
					if(categoryList.get(1).addQuestion(Integer.parseInt(data[1]), data[2], data[3]) == false){
						return false;
					};
				}
				else if(data[0].equalsIgnoreCase(categories[2])){
					if(categoryList.get(2).addQuestion(Integer.parseInt(data[1]), data[2], data[3]) == false){
						return false;
					};
				}
				else if(data[0].equalsIgnoreCase(categories[3])){
					if(categoryList.get(3).addQuestion(Integer.parseInt(data[1]), data[2], data[3]) == false){
						return false;
					};
				}
				else if(data[0].equalsIgnoreCase(categories[4])){
					if(categoryList.get(4).addQuestion(Integer.parseInt(data[1]), data[2], data[3]) == false){
						return false;
					};
				}
			}
			catExist = false;
			valExist = false;
		}
		//if final question does not exit, terminate
		if(finalAnswer == null || finalQuestion == null){
			JOptionPane optionPane = new JOptionPane("Final Jeopardy quesiton does not exist" + "\nPlease choose another file", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION);
			invalidFileError = optionPane.createDialog("Invalid File");
			invalidFileError.setVisible(true);
			return false;
		}
		//re-store the first two lines to update the file later
		
		ArrayList<String> tempList = dataList;
		dataList = new ArrayList<String>();
		for(int i=0; i<tempList.size(); i++){
			dataList.add("::"+tempList.get(i));
		}
		
		dataList.add(0, secondLine);
		dataList.add(0, firstLine);
		return true;
	}

}
