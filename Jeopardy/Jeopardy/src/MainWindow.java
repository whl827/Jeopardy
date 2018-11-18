

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1;
	//private variables (data)
	private ArrayList<Team> teams;
	private ArrayList<Category> categoryList;   	//topics of categories, possible point values
	private String[] categories, points;
	private int answeredQuestion, numOfTeam, turn;
	private int currentPoint, catIndex, index;  	//current category, question number
	private boolean quickplay;
	private String finalQuestion, finalAnswer;
	private StringBuilder announcement;
	private final int max_Question = 25;
	private final int MAX_TEAM = 4;
	private int currentPlayer;

	//a3
	private String categoryLabelImagePath;
	private String enabledQuestionImagePath;
	private String disabledQuestionImagePath;

	private int totalPeopleRated;
	private int totalRate;

	private String fileDestination;
	private ArrayList<String> outFileArray;

	private ImageIcon catImage;
	private ImageIcon enabledImage;
	private ImageIcon disabledImage;

	//UI

	//Main Panels
	private CardLayout cl;
	JPanel boardPanel, gamePanel, finalPanel;
	//Warning
	private JLabel warning;

	//MENU
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem1, menuItem2, menuItem3, menuItem4;
	private boolean secondChance;
	//Title
	private JPanel titleBox;
	private JLabel title;
	//chart
	private ArrayList<JLabel> categoryOnBoard;
	private ArrayList<JButton> scoreBoard;
	//score Board
	private ArrayList<JLabel> teamName;
	private ArrayList<JLabel> teamScore;
	private JPanel gridScore;
	//bottom panel (announcement)
	private JLabel progress, welcome, teamTurn;
	private JTextArea textArea;
	private JTextField emptyLeftW, emptyLeftE, emptyLeftS;

	//DISPLAYQUESTION
	private JLabel team, cat, value;
	private JTextArea questionBox;
	//question Box
	private JTextField answerBox;
	private JButton submitButton;
	private JButton pass;
	private JPanel questionPanel, mainQuestionPanel;
	private JPanel gridChart, leftPanel;
	JPanel rightPanel;

	//Final Jeopardy Variables 
	private JLabel[] finalNameLabels;
	private JLabel[] finalBetLabels;
	private JSlider[] finalSliders; 
	private JButton[] finalButtons;
	private boolean[] finalEligible;
	private JTextArea fjPreview;
	private JTextField[] fjAnswerBox; 
	private JButton[] fjSubmitButton; 
	//final Jeopardy panels
	private JPanel mainBottomPanel, betAndButtons, sliders , finalNames , finalTopPanel ;
	private JPanel bets,buttons,fjPanel;

	StartWindow sw; //reference to startwindow

	//a4
	private GameData gameData;
	private PlayerServer player;

	private boolean networked;

	private ArrayList<String> waitingTeamNames;

	//Window for final jeopardy network
	private JPanel finalPanelNetwork;
	private JLabel finalTitleNetwork, finalUserNameNetwork, 
	finalAmountLabelNetwork, waitingAnswerNetwork;
	private JSlider finalSliderNetwork;
	private JButton finalSetButtonNetwork, finalSubmitAnswerButtonNetwork;
	private JTextField finalAnswerBoxNetwork, finalQuestionNetwork;
	private JTextArea finalWarningLabelNetwork;
	private int startCheckingWinner = 0;
	//private int everyoneRatings = 0;

	ArrayList<Team> finalTeams;
	private int finalAnsweredTeams;
	String myTeamFinalAnswer;

	//a5
	private JLabel turnWaitingLabel; //next to title Jeopardy
	private int turnWaitingCounter;

	private JLabel announcementLabel; //right side of user name during question
	private int announcementCounter;
	

	private JLabel buzzInTimer;	//place to put clock image when buzzing
	private int buzzInTimerCounter;

	private JLabel fishTimer;

	private JLabel gameProgressTeamName; //next to title game progress

	private ImageIcon[] timerImages;
	private ImageIcon[] fishImages;
	
	final private String timerImageName1 = "images/clock/frame_";
	final private String timerImageName2 = "_delay-0.06s.jpg";
	final private String fishImageName1 = "images/fish/frame_";
	final private String fishImageName2 = "_delay-0.1s.jpg";
	
	private JPanel topPanelSub;
	private JPanel progressPanel;
	
	
	
	//timer for each player
	private ArrayList<JLabel> teamTimers;
	
	private int clockImageIndex = 0 ; //for main 15 second (waiting to choose)
	private int fishImageIndex = 0;
	
	private Timer waitToChooseTimer = new Timer();
	private Timer waitToAnswerTimer = new Timer();
	private Timer waitToBuzzInTimer = new Timer();

	private Executor executor = Executors.newCachedThreadPool();
	

	public MainWindow(){
		super("Jeopardy!!");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	//a4
	public MainWindow(GameData gameData, PlayerServer player, int randomTurn){
		this(gameData, randomTurn);
		this.player = player;

		//a5
		turnWaitingLabel = new JLabel(":"+turnWaitingCounter);
		announcementLabel.setText(":"+announcementCounter);
		
		turnWaitingLabel.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		turnWaitingLabel.setBackground(new Color(153,204,255)); turnWaitingLabel.setOpaque(true);
		
		gameProgressTeamName = new JLabel(player.getTeamName());
		gameProgressTeamName.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		
		titleBox.add(turnWaitingLabel); //add timer next to title
		
		progressPanel.add(gameProgressTeamName); //add team name next to "Game Progress"
		progressPanel.add(Box.createRigidArea(new Dimension(30,0)));
		
		//place to store clock and fish images
		timerImages = new ImageIcon[142];
		fishImages = new ImageIcon[8];
		
		//add clock images
		for(int i=0; i<timerImages.length; i++){
			BufferedImage image = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
			try {
				String imagePath = timerImageName1 + String.valueOf(i) + timerImageName2;
				image = ImageIO.read(new File(imagePath));
				timerImages[i] = new ImageIcon(image.getScaledInstance(50, 50, Image.SCALE_DEFAULT));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//add fish images
		for(int i=0; i<fishImages.length; i++){
			BufferedImage image = new BufferedImage(50,50,BufferedImage.TYPE_INT_ARGB);
			try {
				String imagePath = fishImageName1 + String.valueOf(i) + fishImageName2;
				image = ImageIO.read(new File(imagePath));
				fishImages[i] = new ImageIcon(image.getScaledInstance(80, 80, Image.SCALE_DEFAULT));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//////////////////////////7


		networked = true;
		pass.setText("Buzz In!");
		//after everything is created
		this.player.disableQuestions(turn);

		//if you are host, have restart button, if not remove it
		if(player.getTeamTurn()!=0){
			menu.remove(menuItem1);
		}

		//create a window for network game final jeopardy
		finalUserNameNetwork = new JLabel(player.getTeamName() + "'s bet");
		finalAnswerBoxNetwork = new JTextField(player.getTeamName()+"'s answer");

		//the main panel

		finalPanelNetwork.setLayout(new GridLayout(6, 1, 5, 5));
		finalPanelNetwork.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		//title

		finalTitleNetwork.setHorizontalAlignment(SwingConstants.CENTER);
		finalTitleNetwork.setFont(new Font("Times New Roman", Font.PLAIN, 50));
		finalTitleNetwork.setBackground(new Color(0,0,153)); finalTitleNetwork.setOpaque(true);
		finalTitleNetwork.setForeground(Color.WHITE);

		//slider layout

		finalUserNameNetwork.setForeground(Color.WHITE);


		finalSliderNetwork.setForeground(Color.WHITE);
		finalSliderNetwork.setBackground(Color.darkGray);
		finalSliderNetwork.setPaintTicks(true);
		finalSliderNetwork.setPaintLabels(true);
		finalSliderNetwork.setFont(new Font("Times New Roman", Font.PLAIN, 20));


		finalAmountLabelNetwork.setForeground(Color.WHITE);


		finalSetButtonNetwork.setForeground(Color.BLACK);
		finalSetButtonNetwork.setBackground(Color.WHITE);



		waitingAnswerNetwork.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		waitingAnswerNetwork.setHorizontalAlignment(SwingConstants.CENTER);
		waitingAnswerNetwork.setForeground(Color.WHITE);
		waitingAnswerNetwork.setEnabled(false);



		JPanel sliderLayout = new JPanel();
		sliderLayout.setLayout(new BoxLayout(sliderLayout, BoxLayout.X_AXIS));
		sliderLayout.add(finalUserNameNetwork);
		sliderLayout.add(Box.createRigidArea(new Dimension(20,0)));
		sliderLayout.add(finalSliderNetwork);
		sliderLayout.add(Box.createRigidArea(new Dimension(20,0)));
		sliderLayout.add(finalAmountLabelNetwork);
		sliderLayout.add(Box.createRigidArea(new Dimension(20,0)));
		sliderLayout.add(finalSetButtonNetwork);
		sliderLayout.setBorder(new EmptyBorder(0,20,0,20));
		sliderLayout.setBackground(Color.darkGray);

		//warning

		finalWarningLabelNetwork.setForeground(Color.WHITE);
		finalWarningLabelNetwork.setBackground(Color.darkGray);
		finalWarningLabelNetwork.setBorder(new EmptyBorder(0,100,0,0));
		finalWarningLabelNetwork.setFont(new Font("Times New Roman", Font.PLAIN, 25));

		//question

		finalQuestionNetwork.setHorizontalAlignment(SwingConstants.CENTER);
		finalQuestionNetwork.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		finalQuestionNetwork.setBackground(new Color(153,204,255));
		finalQuestionNetwork.setForeground(Color.BLACK); finalQuestionNetwork.setOpaque(true);

		//answer box

		finalAnswerBoxNetwork.setForeground(Color.darkGray);
		finalAnswerBoxNetwork.setFont(new Font("Times New Roman", Font.PLAIN, 20));

		finalSubmitAnswerButtonNetwork.setForeground(Color.BLACK);
		finalSubmitAnswerButtonNetwork.setBackground(Color.WHITE);
		finalSubmitAnswerButtonNetwork.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		finalSubmitAnswerButtonNetwork.setMargin(new Insets(15, 0, 15, 0));


		JPanel answerLayout = new JPanel();
		answerLayout.setLayout(new BoxLayout(answerLayout, BoxLayout.X_AXIS));
		answerLayout.add(finalAnswerBoxNetwork);
		answerLayout.add(Box.createRigidArea(new Dimension(10,0)));
		answerLayout.add(finalSubmitAnswerButtonNetwork);	
		answerLayout.setBorder(new EmptyBorder(10,100,75,100));
		answerLayout.setBackground(Color.darkGray);

		finalPanelNetwork.add(finalTitleNetwork);
		finalPanelNetwork.add(sliderLayout);
		finalPanelNetwork.add(finalWarningLabelNetwork);
		finalPanelNetwork.add(finalQuestionNetwork);
		finalPanelNetwork.add(answerLayout);
		finalPanelNetwork.add(waitingAnswerNetwork);
		finalPanelNetwork.setBackground(Color.darkGray);
		leftPanel.add(finalPanelNetwork, "4");
		finalPanelNetwork.setBackground(Color.darkGray);
		
		
		//a5
		//first time, waiting for current player to choose the question
		waitToChoose();

	}
	//a5
	
	public void waitToChoose(){
		waitToChooseTimer = new Timer();
		turnWaitingCounter=15; clockImageIndex = 0;
		waitToChooseTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				executor.execute(new Runnable() {
					public void run() {
						turnWaitingCounter--;
						if(turnWaitingCounter==0){
							//when the player does not choose question in 15 seconds
							//update game progress
							announcement.append(teams.get(currentPlayer).getName() + " did not choose a question\n");
							textArea.setText(announcement.toString());
							//erase the current player's clock and start new
							clockImageIndex = 0;
							teamTimers.get(currentPlayer).setIcon(null);
							turnWaitingCounter=15;//reset counter
							turnWaitingLabel.setText(":"+turnWaitingCounter);
							turn = (turn+1)%numOfTeam; //next person gets a turn
							currentPlayer = turn;
	
							//if you are currentplayer, disable question, if not 
							if(turn==player.getTeamTurn()){
								enabledUnansweredQuestions();
							}else{
								disableAllQuestions();
							}
							//update name next to game progress and announcement of next player
							//gameProgressTeamName.setText(teams.get(currentPlayer).getName());
							announcement.append("It is now " + teams.get(currentPlayer).getName() + " 's turn to play\n");
							textArea.setText(announcement.toString()); 
						}
						else{
							turnWaitingLabel.setText(":"+turnWaitingCounter);
						}
					}
				});
			}
		}, 1000,1000);
		//for iterating through the clock images
		waitToChooseTimer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				executor.execute(new Runnable() {

					public void run() {
						if(clockImageIndex>=timerImages.length){
							clockImageIndex = 0;
						}
						teamTimers.get(turn).setIcon(timerImages[clockImageIndex]);
						++clockImageIndex;
					}
				});
			}
		}, 0, 105);
	}
	//clear when done choosing
	public void clearWaitToChooseTimer(){
		turnWaitingCounter=15;
		turnWaitingLabel.setText(":"+turnWaitingCounter);
		waitToChooseTimer.cancel();
		clockImageIndex = 0;
		teamTimers.get(currentPlayer).setIcon(null);
	}
	//waiting for player to answer
	public void waitToAnswer(){
		
		if(currentPlayer==player.getTeamTurn()){
			warning.setText("answer within 20 seconds!");
			submitButton.setEnabled(true);
			answerBox.setEnabled(true);
		} else{
			//if next
			warning.setText("waiting for " + teams.get(currentPlayer).getName() 
										   + " to answer");
			submitButton.setEnabled(false);
			answerBox.setEnabled(false);
		}
		
		waitToAnswerTimer = new Timer();
		//for display seconds left next to team name
		waitToAnswerTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				executor.execute(new Runnable() {
					public void run() {
						announcementCounter--;
						if(announcementCounter<=0){
							announcementCounter=20;
							//disable answerbox/buttons
							submitButton.setEnabled(false);
							answerBox.setEnabled(false);
							//add the current team to ineligible teams to buzz in
							//waitingTeamNames.add(teams.get(currentPlayer).getName());
							teamTimers.get(currentPlayer).setIcon(null); //clear its clock
							
							if(currentPlayer==player.getTeamTurn()){
								updateCurrentPlayerWrongAnswer(teams.get(currentPlayer).getName(), categoryList.get(catIndex).getValue(index));
							}else{
								updateWhenWrong(teams.get(currentPlayer).getName(), 
												categoryList.get(catIndex).getAnswer(index), 
												categoryList.get(catIndex).getValue(index),
												player.getTeamName(),
												player.getTeamTurn());
							}
								//When no one answers, buzz in time
								if(waitToAnswerTimer!=null){
									clearWaitToAnswerTimer();
								}
						}
						else{
							//update the label
							announcementLabel.setText(":"+announcementCounter);
						}
					}
				});
			}
		}, 1000,1000);
		//for displaying clock on current team
		waitToAnswerTimer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				executor.execute(new Runnable() {
					public void run() {
						if(clockImageIndex>=timerImages.length){
							clockImageIndex = 0;
						}
						teamTimers.get(currentPlayer).setIcon(timerImages[clockImageIndex]);
						++clockImageIndex;
						if(fishImageIndex>=fishImages.length){
							fishImageIndex = 0;
						}
						else{
							if(player.getTeamTurn()!=currentPlayer){
								fishTimer.setIcon(fishImages[fishImageIndex]);
								++fishImageIndex;
							}
						}	
					}
				});
			}
		}, 0, 140);
	}
	public void clearWaitToAnswerTimer(){
		announcementCounter=20;
		announcementLabel.setText(":"+announcementCounter);
		waitToAnswerTimer.cancel(); //cancel timer
		teamTimers.get(currentPlayer).setIcon(null); //remove the clock
		clockImageIndex = 0;
		fishTimer.setIcon(null);
	}
	public void waitToBuzzIn(){
		
		//When no one answers, buzz in time
		if(waitToAnswerTimer!=null){
			clearWaitToAnswerTimer();
		}
		waitToBuzzInTimer = new Timer();
		buzzInTimerCounter = 20; clockImageIndex = 0;
		announcementLabel.setText(":"+buzzInTimerCounter);
		waitToBuzzInTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				executor.execute(new Runnable() {	
					public void run() {
						buzzInTimerCounter--;
						if(buzzInTimerCounter<=0){	
							//no one buzzed in, go to next question
							buzzInTimerCounter=20;
							submitButton.setEnabled(false);
							answerBox.setEnabled(false);
							//waitingTeamNames.add(teams.get(currentPlayer).getName());
							//back to main board
							announcement.append("no one buzzed in\n"
									+ "The correct answer was " + categoryList.get(catIndex).getAnswer(index)+"\n");
							textArea.setText(announcement.toString());

							clearWaitToBuzzInTimer();
//							cl.show(leftPanel,"1");
//							waitingTeamNames.clear();
							team.setText(null);
//							pass.setVisible(false);
							
							turn = (turn+1)%numOfTeam;
							currentPlayer = turn;
							if(turn==player.getTeamTurn()){
								enabledUnansweredQuestions();
							}else{
								disableAllQuestions();
							}
							//end of question
							endOfQuestionNetwork();
						}
						else{
							//update the label
							announcementLabel.setText(":"+buzzInTimerCounter);
						}
					}
				});
			}
		}, 1000, 1000);
		waitToBuzzInTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				executor.execute(new Runnable() {
					public void run() {
						if(clockImageIndex>=timerImages.length){
							clockImageIndex = 0;
						}
						else{
							buzzInTimer.setIcon(timerImages[clockImageIndex]);
							++clockImageIndex;
						}

					}
				});
			}
		}, 0, 140);
	}
	public void clearWaitToBuzzInTimer(){
		if(waitToBuzzInTimer != null){
			waitToBuzzInTimer.cancel();
			buzzInTimer.setIcon(null);
			clockImageIndex = 0;
			buzzInTimerCounter = 20;
		}
	}
	////////////////////////
	
	
	
	public MainWindow(GameData gameData, int randomTurn) {
		super("Jeopardy!!");

		//a5
		/////////////////////////
		turnWaitingCounter = 15;
		announcementCounter = 20;
		buzzInTimerCounter = 20;
//		turnWaitingLabel = new JLabel();
		announcementLabel = new JLabel();
//		gameProgressTeamName = new JLabel();
		buzzInTimer = new JLabel();
		
		
		
	
		
		
		
		

		//////////////////////////

		//a4
		networked = false;
		waitingTeamNames = new ArrayList<>();


		this.gameData = gameData;

		this.categoryLabelImagePath = null;
		this.enabledQuestionImagePath = null;
		this.disabledQuestionImagePath = null;

		//a4
		this.totalPeopleRated = gameData.getTotalPeopleRated();
		this.totalRate =  gameData.getTotalRate();
		this.fileDestination =  gameData.getFileDestination();
		this.outFileArray =  gameData.getOutFileArray();

		//final jeopardy new slider gui
		finalTeams = new ArrayList<>();
		finalAnsweredTeams = 0;
		finalPanelNetwork = new JPanel();
		finalTitleNetwork = new JLabel("Final Jeopardy Round");


		finalSliderNetwork = new JSlider();
		finalAmountLabelNetwork = new JLabel("$0");
		finalSetButtonNetwork = new JButton("Set Bet");
		waitingAnswerNetwork = new JLabel();
		finalWarningLabelNetwork = new JTextArea();
		finalQuestionNetwork = new JTextField("Wait for it...");
		finalQuestionNetwork.setEditable(false);
		finalSubmitAnswerButtonNetwork = new JButton("Submit Answer");


		//get Data
		this.teams = gameData.getTeams();
		this.categoryList = gameData.getCategoryList();
		this.categories = gameData.getCategories();
		this.points = gameData.getPoints();
		this.answeredQuestion = gameData.getAnsweredQuestion();
		this.numOfTeam = gameData.getNumOfTeam();
		if(answeredQuestion == 20){
			quickplay = true;
		} 
		//Random rand = new Random();
		//turn = rand.nextInt(numOfTeam);
		turn = randomTurn;


		currentPlayer = turn;
		this.finalQuestion = gameData.getFinalQuestion();
		this.finalAnswer = gameData.getFinalAnswer();

		this.categoryLabelImagePath = gameData.getCategoryLabelImagePath();
		this.enabledQuestionImagePath = gameData.getEnabledQuestionImagePath();
		this.disabledQuestionImagePath = gameData.getDisabledQuestionImagePath();

		catImage = new ImageIcon(categoryLabelImagePath);
		enabledImage = new ImageIcon(enabledQuestionImagePath);
		disabledImage = new ImageIcon(disabledQuestionImagePath);

		announcement = new StringBuilder();
		currentPoint = 0;
		catIndex = -1;
		index = -1;
		secondChance = false;

		initializeComponents(); createGUI(); addEvents();

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent evt)
			{
				Object[] options = {"Yes", "No"};

				int answer = JOptionPane.showOptionDialog(MainWindow.this, "Are you sure you want to quit? ","Close Window", 
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,options[1]);
				if(answer == JOptionPane.YES_OPTION){ 

					if(networked){
						player.exit(player.getTeamTurn());
					}
					else{
						System.exit(0);
					}
				}
			}
		});
	}
	public void getStartWindow(StartWindow sw){
		this.sw = sw;
	}
	private void initializeComponents() {

		gridScore = new JPanel();

		rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		//a3
		rightPanel.setLayout(new GridLayout(0, 1, 0, 0));
		rightPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		rightPanel.setBackground(new Color(0,0,153));

		bets = new JPanel();
		buttons = new JPanel();
		fjPanel = new JPanel();

		team = new JLabel();
		cat = new JLabel();
		value = new JLabel();
		questionBox = new JTextArea();

		cl = new CardLayout();
		boardPanel = new JPanel();
		gamePanel = new JPanel();
		finalPanel = new JPanel();

		emptyLeftW = new JTextField(); 
		emptyLeftE = new JTextField(); 
		emptyLeftS = new JTextField();
		emptyLeftW.setEnabled(false);
		emptyLeftE.setEnabled(false);
		emptyLeftS.setEnabled(false);

		mainBottomPanel = new JPanel();
		betAndButtons = new JPanel();
		sliders = new JPanel();
		finalNames = new JPanel();
		finalTopPanel = new JPanel();

		finalNameLabels = new JLabel[numOfTeam];
		finalBetLabels = new JLabel[numOfTeam];
		finalSliders = new JSlider[numOfTeam];
		finalButtons = new JButton[numOfTeam];
		fjAnswerBox = new JTextField[numOfTeam];
		fjSubmitButton = new JButton[numOfTeam];
		finalEligible = new boolean[numOfTeam];
		for(int i=0; i<numOfTeam; i++){
			finalNameLabels[i] = (new JLabel());
			finalBetLabels[i] = (new JLabel());
			finalSliders[i] = (new JSlider());
			finalButtons[i] = (new JButton());
			fjAnswerBox[i] = (new JTextField());
			fjSubmitButton[i] = (new JButton());
			finalEligible[i] = false;
		}
		fjPreview = new JTextArea("\t\tAnd the question is...");

		leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(0, 1, 0, 0));
		leftPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		leftPanel.setBackground(new Color(0,0,153));

		warning = new JLabel();

		//question Box
		answerBox = new JTextField();
		submitButton = new JButton("Submit Answer");
		questionPanel = new JPanel();
		mainQuestionPanel = new JPanel();
		pass = new JButton("Pass");
		pass.setForeground(Color.BLACK);
		pass.setBackground(Color.WHITE);

		progress = new JLabel("Game Progress");
		progress.setFont(new Font("Times New Roman", Font.BOLD,40));
		progress.setBorder(new EmptyBorder(5,40,10,40));
		progress.setHorizontalAlignment(SwingConstants.CENTER);

		welcome = new JLabel("Welcome to Jeopardy!\n");
		teamTurn = new JLabel("The team to go first is " + teams.get(turn).getName() + "\n");

		textArea = new JTextArea();
		textArea.setBackground(new Color(153,204,255));
		textArea.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		//update score board
		teamName = new ArrayList<>();
		for(int i=0; i<numOfTeam; i++){
			JLabel team = new JLabel(teams.get(i).getName());
			team.setHorizontalAlignment(SwingConstants.CENTER);
			team.setOpaque(true);
			team.setBackground(Color.DARK_GRAY);
			team.setFont(new Font("Times New Roman", Font.PLAIN, 25));
			team.setForeground(Color.white);
			teamName.add(team);
		}
		teamScore = new ArrayList<>();
		for(int i=0; i<numOfTeam; i++){
			JLabel score = new JLabel("$" + teams.get(i).getPoint());
			score.setHorizontalAlignment(SwingConstants.CENTER);
			score.setOpaque(true);
			score.setBackground(Color.DARK_GRAY);
			score.setFont(new Font("Times New Roman", Font.PLAIN, 25));
			score.setForeground(Color.white);
			teamScore.add(score);
		}
		//a5/////////////////////
		teamTimers = new ArrayList<>();
		for(int i=0; i<numOfTeam; i++){
			teamTimers.add(new JLabel());
		}
		
		
		
		
		///////////////////////
		
		
		

		//Board
		scoreBoard = new ArrayList<JButton>();
		for(int i=0; i<categories.length; i++){
			for(int j=0; j<points.length; j++){
				JButton button = new JButton(points[i]);
				button.setBackground(Color.DARK_GRAY);
				button.setFont(new Font("Times New Roman", Font.PLAIN, 25));
				button.setForeground(Color.white);
				//a3
				button.setIcon(enabledImage);
				button.setHorizontalTextPosition(JButton.CENTER);
				button.setVerticalTextPosition(JButton.CENTER);
				scoreBoard.add(button);
			}
		}
		//Categories Names on Board
		categoryOnBoard = new ArrayList<>();
		for(int i=0; i<categories.length; i++){
			JLabel label = new JLabel(categories[i]);
			label.setOpaque(true);
			label.setForeground(Color.white);
			label.setBackground(Color.DARK_GRAY);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setFont(new Font("Times New Roman", Font.PLAIN, 30));
			label.setIcon(catImage);
			label.setHorizontalTextPosition(JLabel.CENTER);
			label.setVerticalTextPosition(JLabel.CENTER);
			categoryOnBoard.add(label);
		}
		//for menu options
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		menuItem1 = new JMenuItem("Restart This Game");
		menuItem2 = new JMenuItem("Choose New Game File");
		menuItem3 = new JMenuItem("Exit Game");
		menuItem4 = new JMenuItem("Logout");
		//title
		titleBox = new JPanel();
		title = new JLabel("Jeopardy");
		title.setFont(new Font("Times New Roman", Font.PLAIN, 35));
	}


	private void createGUI() {
		setSize(1500, 825);
		//setSize(1200,525); => you can use this to test the reduced size!
		setLocationRelativeTo(null);

		finalTopPanel.setLayout(new BorderLayout());
		finalTopPanel.setBackground(new Color(0,0,153));

		finalNames.setLayout(new BoxLayout(finalNames, BoxLayout.Y_AXIS));
		finalNames.setBackground(Color.darkGray);

		sliders.setLayout(new BoxLayout(sliders, BoxLayout.Y_AXIS));
		sliders.setBackground(Color.darkGray);

		mainBottomPanel.setLayout(new BoxLayout(mainBottomPanel, BoxLayout.Y_AXIS));
		mainBottomPanel.setBackground(Color.darkGray);

		fjPreview.setBackground(new Color(153,204,255));
		fjPreview.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		fjPreview.setLineWrap(true);
		fjPreview.setWrapStyleWord(true);
		fjPreview.setAlignmentX(CENTER_ALIGNMENT);
		fjPreview.setBorder(new EmptyBorder(10,50,10,50));


		menuBar.add(menu);
		menu.add(menuItem1);
		menu.add(menuItem2);
		menu.add(menuItem3);
		menu.add(menuItem4);
		setJMenuBar(menuBar);


		//a5 add timer next to timer
		titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.X_AXIS));
		titleBox.add(Box.createRigidArea(new Dimension(450,0)));
		titleBox.add(title);
		////////////////////////

		titleBox.setBackground(new Color(153,204,255));

		gridChart = new JPanel();
		gridChart.setLayout(new GridLayout(6, 5, 3, 3));
		gridChart.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		gridChart.setBackground(new Color(0,0,153));

		for(int i=0; i<categoryOnBoard.size(); i++){
			gridChart.add(categoryOnBoard.get(i));
		}
		for(int i=0; i<max_Question; i++){
			gridChart.add(scoreBoard.get(i));
		}
		leftPanel.setLayout(cl);
		boardPanel.setLayout(new BorderLayout());
		boardPanel.add(titleBox, BorderLayout.NORTH);
		boardPanel.add(gridChart, BorderLayout.CENTER);
		leftPanel.add(boardPanel, "1");
		leftPanel.add(gamePanel, "2");
		leftPanel.add(finalPanel, "3");
		cl.show(leftPanel, "1");
		add(leftPanel,BorderLayout.CENTER);

		gridScore.setLayout(new GridLayout(numOfTeam, 2, 3, 3));
		gridScore.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		for(int i=0; i<numOfTeam; i++){
			teamName.get(i).setBorder(new EmptyBorder(20,30,20,30));
			teamScore.get(i).setBorder(new EmptyBorder(20,30,20,30));
			
			teamTimers.get(i).setBorder(new EmptyBorder(20,30,20,30)); // a5
			
			gridScore.add(teamName.get(i));
			gridScore.add(teamTimers.get(i)); //a5
			gridScore.add(teamScore.get(i));
		}

		gridScore.setBackground(Color.DARK_GRAY);
		rightPanel.add(gridScore);

		JPanel bottomPanel = new JPanel();

		bottomPanel.setLayout(new BorderLayout());


		//a5
		progressPanel = new JPanel();
		progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.X_AXIS));
		progressPanel.add(progress);
		bottomPanel.add(progressPanel, BorderLayout.NORTH);
		//bottomPanel.add(progress, BorderLayout.NORTH);

		announcement.append(welcome.getText()+teamTurn.getText());
		textArea.setText(announcement.toString());
		bottomPanel.add(textArea, BorderLayout.CENTER);
		bottomPanel.setBackground(new Color(153,204,255));

		rightPanel.add(bottomPanel);
		add(rightPanel, BorderLayout.EAST);

		textArea.setEditable ( false ); // set textArea non-editable
		JScrollPane scroll = new JScrollPane ( textArea );
		scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
		bottomPanel.add ( scroll );

		//THIS IS FOR DISPLYQUESTION FUNCTION GUI

		///start displaying question
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());

		topPanelSub = new JPanel();
		topPanelSub.setLayout(new BoxLayout(topPanelSub, BoxLayout.X_AXIS));

		team.setHorizontalAlignment(SwingConstants.CENTER);
		cat.setHorizontalAlignment(SwingConstants.CENTER);
		value.setHorizontalAlignment(SwingConstants.CENTER);
		team.setBorder(new EmptyBorder(10,50,10,50)); //a5 changed value 500 to 50
		cat.setBorder(new EmptyBorder(10,40,10,40));
		value.setBorder(new EmptyBorder(10,50,10,500));
		team.setForeground(Color.white);
		cat.setForeground(Color.white); 
		value.setForeground(Color.white); 
		team.setBackground(new Color(0,0,153)); 
		cat.setBackground(new Color(0,0,153)); 
		value.setBackground(new Color(0,0,153));
		team.setFont(new Font("Times New Roman", Font.PLAIN, 50));
		cat.setFont(new Font("Times New Roman", Font.PLAIN, 50));
		value.setFont(new Font("Times New Roman", Font.PLAIN, 50));

		announcementLabel.setBorder(new EmptyBorder(10,400,10,50));
		announcementLabel.setBackground(new Color(0,0,153)); 
		announcementLabel.setForeground(Color.white); 
		announcementLabel.setFont(new Font("Times New Roman", Font.PLAIN, 50));
		
		//a5
		topPanelSub.add(announcementLabel);
		topPanelSub.add(buzzInTimer);
		///////
		
		topPanelSub.add(team);
		topPanelSub.add(cat);
		topPanelSub.add(value);
		topPanelSub.setBackground(new Color(0,0,153));

		//Create Warning sign
		warning.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		warning.setBackground(Color.DARK_GRAY); warning.setOpaque(true);
		warning.setForeground(Color.WHITE);
		warning.setHorizontalAlignment(SwingConstants.CENTER);
		warning.setBorder(new EmptyBorder(30,60,30,60));
		
		//a5
		JPanel warningPanel = new JPanel();
		warningPanel.setLayout(new BoxLayout(warningPanel, BoxLayout.X_AXIS));
		warningPanel.add(Box.createRigidArea(new Dimension(400,100)));
		fishTimer = new JLabel();
		warningPanel.add(Box.createRigidArea(new Dimension(10,0)));
		fishTimer.setBorder(new EmptyBorder(30,100,30,0));
		warningPanel.add(fishTimer);
		warningPanel.add(warning);
		warningPanel.setBackground(Color.darkGray);
		
		topPanel.add(topPanelSub, BorderLayout.NORTH);
		topPanel.add(warningPanel, BorderLayout.SOUTH);
		//topPanel.add(warning, BorderLayout.SOUTH);

		questionPanel.add(topPanel, BorderLayout.NORTH);
		questionPanel.setBackground(Color.darkGray);

		//question box, submit button, answer box
		mainQuestionPanel.setLayout(new BoxLayout(mainQuestionPanel, BoxLayout.Y_AXIS));
		JPanel bottomQuestionPanel= new JPanel();
		bottomQuestionPanel.setLayout(new BoxLayout(bottomQuestionPanel, BoxLayout.X_AXIS));

		questionBox.setEditable(false);
		questionBox.setBackground(new Color(153,204,255));
		questionBox.setLineWrap(true);
		questionBox.setWrapStyleWord(true);
		questionBox.setFont(new Font("Times New Roman", Font.PLAIN, 35));

		mainQuestionPanel.add(questionBox);

		answerBox.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		answerBox.setForeground(Color.gray);

		submitButton.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		submitButton.setBorder(new EmptyBorder(30,10,30,10));
		submitButton.setBackground(Color.WHITE);
		submitButton.setForeground(Color.BLACK);

		bottomQuestionPanel.setBorder(new EmptyBorder(10,0,0,0));
		bottomQuestionPanel.setBackground(Color.darkGray);

		bottomQuestionPanel.add(answerBox);
		bottomQuestionPanel.add(submitButton);
		mainQuestionPanel.add(bottomQuestionPanel);

		//a3
		pass.setVisible(false);
		pass.setFont(new Font("Times New Roman", Font.BOLD, 25));
		mainQuestionPanel.add(Box.createRigidArea(new Dimension(0,10)));
		mainQuestionPanel.add(pass);
		mainQuestionPanel.setBackground(Color.darkGray);

		bottomQuestionPanel.setPreferredSize(new Dimension(1500,75));
		mainQuestionPanel.setMaximumSize( mainQuestionPanel.getPreferredSize() );
		bottomQuestionPanel.setMaximumSize( bottomQuestionPanel.getPreferredSize() );	

		emptyLeftW.setBackground(Color.darkGray);
		emptyLeftE.setBackground(Color.darkGray);
		emptyLeftS.setBackground(Color.darkGray);

		emptyLeftW.setBorder(new EmptyBorder(30,30,20,30));
		emptyLeftE.setBorder(new EmptyBorder(30,30,20,30));
		emptyLeftS.setBorder(new EmptyBorder(0,30,0,30));

		gamePanel.setLayout(new BorderLayout());
		gamePanel.add(emptyLeftW, BorderLayout.WEST);
		gamePanel.add(emptyLeftE, BorderLayout.EAST);
		gamePanel.add(emptyLeftS, BorderLayout.SOUTH);
		gamePanel.add(questionPanel, BorderLayout.NORTH);
		gamePanel.add(mainQuestionPanel, BorderLayout.CENTER);

		//THIS IS FOR FINAL DISPALY QUESTION

		JLabel title = new JLabel("Final Jeopardy Round");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBorder(new EmptyBorder(5,0,5,0));
		title.setForeground(Color.WHITE); 
		title.setFont(new Font("Times New Roman", Font.PLAIN, 40)); 
		finalTopPanel.add(title, BorderLayout.NORTH);

		bets.setLayout(new BoxLayout(bets, BoxLayout.Y_AXIS));
		bets.setBackground(Color.darkGray);

		buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
		buttons.setBackground(Color.darkGray);

		//a3 try gridlayout
		bets.setLayout(new GridLayout(0, 1, 0, 0));
		bets.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		buttons.setLayout(new GridLayout(0, 1, 0, 0));
		buttons.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		betAndButtons.setLayout(new GridLayout(1, 2, 0, 0));
		betAndButtons.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		finalNames.setLayout(new GridLayout(0, 1, 0, 0));
		finalNames.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		sliders.setLayout(new GridLayout(0, 1, 0, 0));
		sliders.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		sliders.setAlignmentX(SwingConstants.CENTER);
		sliders.setAlignmentY(SwingConstants.CENTER);

		betAndButtons.add(bets);
		betAndButtons.add(buttons);
		betAndButtons.setBackground(Color.darkGray);

		fjPanel.setLayout(new GridLayout(2, 2, 5, 5));
		fjPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		fjPanel.setBorder(new EmptyBorder(5,0,5,0));
		fjPanel.setBackground(Color.darkGray);

		mainBottomPanel.add(fjPreview);
		mainBottomPanel.add(fjPanel);


		sliders.setBorder(new EmptyBorder(10,0,0,0));

		finalPanel.setLayout(new BorderLayout());
		finalPanel.add(mainBottomPanel, BorderLayout.SOUTH);
		finalPanel.add(finalTopPanel, BorderLayout.NORTH);
		finalPanel.add(betAndButtons, BorderLayout.EAST);
		finalPanel.add(finalNames, BorderLayout.WEST);
		finalPanel.add(sliders, BorderLayout.CENTER);

	}
	//give next team a chance
	private void nextTeamAnswer(){
		pass.setVisible(true);
		pass.setEnabled(true);
		secondChance = false;
		String currentTeamName =  teams.get(currentPlayer).getName();
		team.setText(currentTeamName);
		answerBox.setText("");
		String progressText = ("It's " + currentTeamName + "'s turn to try to answer the question.\n");
		warning.setText(progressText);
		announcement.append(progressText);
		textArea.setText(announcement.toString());
	}

	private void endOfQuestion(){

		pass.setVisible(false);
		answerBox.setText(null);
		warning.setText(null);
		answeredQuestion++;
		secondChance = false;

		turn = currentPlayer;

		if(answeredQuestion==25){

			boolean allnegative = true;
			for(int i=0; i<teams.size(); i++){
				if(teams.get(i).getPoint()>0){
					allnegative = false;
					break;
				}
			}
			if(allnegative){

				announcement.append("The game has ended. There were no Winners\n");
				textArea.setText(announcement.toString());
				RatingWindow rw = new RatingWindow("Sad!", "There were no Winners!", String.valueOf(totalRate/totalPeopleRated), 
						totalRate, totalPeopleRated, outFileArray, fileDestination);
				//don't restart but disabled all button
				rw.setLocationRelativeTo(MainWindow.this);
				rw.setVisible(true);

				for(int i=0; i<scoreBoard.size(); i++){
					scoreBoard.get(i).setEnabled(false);
					scoreBoard.get(i).setIcon(disabledImage);
					scoreBoard.get(i).setDisabledIcon(disabledImage);
				}
				cl.show(leftPanel, "1");
				return;
			}
			announcement.append("Welcome to Final Jeopardy!\n");
			textArea.setText(announcement.toString());
			displayFinalQuestion();
		}
		else{
			announcement.append("Now it's " + teams.get(turn).getName() + "'s turn!\n" );
			textArea.setText(announcement.toString());
			cl.show(leftPanel, "1");
		}
	}


	private void addEvents() {

		//a4 final jeopardy action listener

		//change amount label as slider changes
		//enable set bet button unless it's 0
		finalSliderNetwork.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				finalAmountLabelNetwork.setText("$"+finalSliderNetwork.getValue());
				if(finalSliderNetwork.getValue()==0){
					finalSetButtonNetwork.setEnabled(false);
				}
				else{
					finalSetButtonNetwork.setEnabled(true);
				}
			}
		});
		//when set button is clicked
		finalSetButtonNetwork.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				//disable button and sliders
				finalSetButtonNetwork.setEnabled(false);
				finalSliderNetwork.setEnabled(false);
				teams.get(player.getTeamTurn()).setBet(finalSliderNetwork.getValue());

				//need to notify this team finished betting
				int value = finalSliderNetwork.getValue();
				updateBetWaitingStatus(player.getTeamName());
				player.updateOtherTeamsBet(player.getTeamTurn(), value );
				player.updateBetWaitingStatus(player.getTeamTurn(), player.getTeamName());

				if(allFinishedBetting()){
					//ask other to update waiting status
					//player.updateOtherTeamsBet(player.getTeamTurn(), value );
					//enable final question 
					answerFinalQuestion();
					//ask other player to answer final Question
					player.answerFinalQuestion(player.getTeamTurn());
				}

			}
			private boolean allFinishedBetting(){

				//if at least one team has not bet yet, return false
				for(int i=0; i<finalTeams.size(); i++){
					if(finalTeams.get(i).getBet()==0){	
						return false;
					}
				}
				return true;
			}
		});

		//when you submit final question
		finalSubmitAnswerButtonNetwork.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				finalSubmitAnswerButtonNetwork.setEnabled(false);
				finalAnswerBoxNetwork.setEnabled(false);
				waitingAnswerNetwork.setText("");
				//store the answer
				myTeamFinalAnswer = finalAnswerBoxNetwork.getText();
				//add to the finished team
				++finalAnsweredTeams;
				//notify player to copy this arraylist
				player.keepTrackFinalAnsweredTeam(finalAnsweredTeams);
				//if every is done betting
				if(finalAnsweredTeams==finalTeams.size()){
					//notify everyone to check the answer
					player.checkFinalAnswer();
				}
			}
		});



		//if answerbox is writing, disable pass
		answerBox.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				disablePass();
			}
			public void removeUpdate(DocumentEvent e) {
				disablePass();
			}
			public void insertUpdate(DocumentEvent e) {
				disablePass();
			}
			public void disablePass(){
				if(!answerBox.getText().equals("")){
					pass.setEnabled(false);
				}
			}
		});

		//if user press pass, just go next person, or if last person, then start a new question
		pass.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){


				if(networked){
					//for this current player
					currentPlayer = player.getTeamTurn(); 
					warning.setText("answer within 20 seconds!");
					pass.setVisible(false);
					team.setText(player.getTeamName());
					announcement.append(player.getTeamName() + " buzzed in to answer\n" );
					textArea.setText(announcement.toString());
					answerBox.setEnabled(true);
					submitButton.setEnabled(true);
					clearWaitToBuzzInTimer();
					waitToAnswer(); //a5
					//for other players
					player.updateAfterBuzzIn(player.getTeamName(), player.getTeamTurn());

				}
				//for non network problem
				else{
					//update game progress
					announcement.append(teams.get(currentPlayer).getName() + " has passed\n" );
					textArea.setText(announcement.toString());
					currentPlayer = (currentPlayer+1)%numOfTeam;
					//if this is last team who's using pass, next team get to choose
					if(currentPlayer == turn){
						turn = (turn+1)%numOfTeam;
						currentPlayer = turn;

						//after grading a3
						String answer = categoryList.get(catIndex).getAnswer(index);
						announcement.append("The correct answer is: " + answer + "\n");
						textArea.setText(announcement.toString());

						endOfQuestion();
					}
					else{
						team.setText(teams.get(currentPlayer).getName());
						warning.setText("It's " + teams.get(currentPlayer).getName() + "'s turn to try to answer the question.\n");
						announcement.append("It's " + teams.get(currentPlayer).getName() + "'s turn to try to answer the question.\n");
						textArea.setText(announcement.toString());
					}
				}
			}
		});
		//restart
		menuItem1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(networked){
					Random rand = new Random();
					int newTurn = rand.nextInt(numOfTeam);
					player.restartGame(newTurn);
				}
				else{
					restart();
				}
				return;
			}
		});
		//new File
		menuItem2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(networked){
					player.chooseNewFile(player.getTeamTurn());
				}
				else{	
					restart();
					sw.clearData();
					sw.setVisible(true);
					setVisible(false);
				}
				return;
			}
		});
		//Exit Game
		menuItem3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(networked){
					player.exit(player.getTeamTurn());

				}
				else{
					System.exit(0);
				}
			}
		});
		//Logout
		menuItem4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){

				if(networked){
					player.logout(player.getTeamTurn());
				}
				else{	
					LoginWindow lw = new LoginWindow();
					lw.setVisible(true);
					setVisible(false);
				}
			}
		});

		//question chosen
		ActionListener actionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) { 

				
				
				
				//get which button is clicked
				int action = Integer.parseInt(e.getActionCommand());
				submitButton.setEnabled(true);
				answerBox.setEnabled(true);
				displayQuestion(action);
				if(networked){
					player.displayQuestion(action, turn);
				}
			}
		};
		//setActionCommand for buttons
		for(int i=0; i<scoreBoard.size(); i++){
			scoreBoard.get(i).addActionListener(actionListener);
			scoreBoard.get(i).setActionCommand(String.valueOf(i));
		}

		//when submit button is clicked, check for answer
		submitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){

				boolean questionEnd = false;
				String currentTeam = teams.get(currentPlayer).getName();
				//check the answer
				String userAns = answerBox.getText().trim();
				//String userAns = qw.getAnswer();
				String answer = categoryList.get(catIndex).getAnswer(index);	

				String[] split = userAns.split(" ");
				if(userAns.isEmpty()){
					return;
				}
				int value = categoryList.get(catIndex).getValue(index);

				if(networked){

					answerBox.setText("");
					//check if answer if is in a correct format
					String user_answer = correctAnswerFormat(userAns);
					if(user_answer!=null){ //if answer is correct format
						//compare the answer
						if(answer.equalsIgnoreCase(user_answer)){
							updateWhenCorrect(currentTeam, user_answer, value);
							player.updateWhenCorrect(currentTeam, user_answer, value);
						} //if it's wrong
						else{
							updateCurrentPlayerWrongAnswer(currentTeam, value);
							player.updateWhenWrong(currentTeam, answer, value);
						}
					}
					//it's wrong answer
					else{
						if(secondChance){

							updateCurrentPlayerWrongAnswer(currentTeam, value);
							player.updateWhenWrong(currentTeam, answer, value);	
						}
						else{
							//for current players
							warning.setVisible(true);
							warning.setText("Invalid format of your answer. Remember to pose your answer as a question.");
							announcement.append(currentTeam + " had a badly formatted answer.\nThey will get a second chance to answer\n");
							textArea.setText(announcement.toString());
							secondChance = true;
							//for other players
							player.firstChanceWarningUpdate(currentTeam);
						}
					}
				}

				//this is for nonentworked
				else{	
					while(questionEnd == false){
						String[] answerSplit = userAns.split(" ");
						//if user answer without question form (who, what, when, where)
						if (!(answerSplit[0].equalsIgnoreCase("who")  || answerSplit[0].equalsIgnoreCase("where")||
								answerSplit[0].equalsIgnoreCase("what") || answerSplit[0].equalsIgnoreCase("when"))){
							//wrong if wrong format on 2nd time
							if(secondChance){
								teams.get(currentPlayer).wrong(value);
								teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
								//next team
								currentPlayer = (currentPlayer+1)%numOfTeam;
								//print out the answer and updated scores //make announcement
								if(currentPlayer != turn){
									announcement.append(currentTeam + ", you got the answer wrong!\n$" + value + " will be deducted from your score\n");
									textArea.setText(announcement.toString());
									nextTeamAnswer();
									return;
								}
								//IF NO ONE ANSWERS CORRECTLY,
								turn = (turn+1)%numOfTeam;
								currentPlayer = turn;
								announcement.append(currentTeam + ", you got the answer wrong!\nThe correct answer is: " + answer
										+ "\n$" + value + " will be deducted from your score\n");
								textArea.setText(announcement.toString());
								//update the team score on board
								teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());	
								questionEnd = true;
								continue;
							}
							secondChance = true;
							pass.setEnabled(false);
							//give a second chance to answer
							warning.setText("Invalid format of your answer. Remember to pose your answer as a question.");
							warning.setVisible(true);
							announcement.append(currentTeam + " had a badly formatted answer.\nThey will get a second chance to answer\n");
							textArea.setText(announcement.toString());
							break;
						}
						else{//when first word is one of who/what/where/when
							//if the format is correct
							if(answerSplit[1].equalsIgnoreCase("is") || answerSplit[1].equalsIgnoreCase("are")){
								//store the real answer (without question) to temp
								String temp = "";
								for(int i=2; i<answerSplit.length; i++){
									temp = temp + " " + answerSplit[i];
								}
								//when you only enter what is ____ or where is ____ etc
								if(temp.isEmpty()){
									teams.get(currentPlayer).wrong(value);
									teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());

									currentPlayer = (currentPlayer+1)%numOfTeam;
									//IF PLAYER ANSWERS THE QUESTION CORRECTLY,
									if(currentPlayer != turn){
										announcement.append(currentTeam + ", you got the answer wrong!\n$" + value + " will be deducted from your score\n");
										textArea.setText(announcement.toString());
										nextTeamAnswer();
										return;
									}	
									//if no one answer correctly, waiting for piazza
									turn = (turn+1)%numOfTeam;
									currentPlayer = turn;
									announcement.append(currentTeam + ", you got the answer wrong!\nThe correct answer is: " + answer
											+ "\n$" + value + " will be deducted from your score\n");
									textArea.setText(announcement.toString());
									//update the team score on board
									teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
									questionEnd = true;
									continue;
								}
								temp = temp.substring(1);
								///correct answer
								if(answer.equalsIgnoreCase(temp)){
									teams.get(currentPlayer).correct(value);
									announcement.append(currentTeam + ", you got the answer right!\n$" + value + " will be added to your score\n");
									textArea.setText(announcement.toString());
									//update the team score on board
									teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
									questionEnd = true;
								}//wrong answer
								else{
									teams.get(currentPlayer).wrong(value);
									teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());

									currentPlayer = (currentPlayer+1)%numOfTeam;
									//IF PLAYER ANSWERS THE QUESTION CORRECTLY,
									if(currentPlayer != turn){
										announcement.append(currentTeam + ", you got the answer wrong!\n$" + value + " will be deducted from your score\n");
										textArea.setText(announcement.toString());
										nextTeamAnswer();
										return;
									}
									//if no one answers correctly, waiting for piazza
									turn = (turn+1)%numOfTeam;
									currentPlayer = turn;
									announcement.append(currentTeam + ", you got the answer wrong!\nThe correct answer is: " + answer
											+ "\n$" + value + " will be deducted from your score\n");
									textArea.setText(announcement.toString());
									//update the team score on board
									teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
									questionEnd = true;
									continue;
								}
							}
							//when second word is not either "is" or "are"
							else{
								//if second chance was already used
								if(secondChance){
									teams.get(currentPlayer).wrong(value);
									teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
									currentPlayer = (currentPlayer+1)%numOfTeam;

									//IF PLAYER ANSWERS THE QUESTION CORRECTLY,
									if(currentPlayer != turn){
										announcement.append(currentTeam + ", you got the answer wrong!\n$" + value + " will be deducted from your score\n");
										textArea.setText(announcement.toString());
										nextTeamAnswer();
										return;
									}

									//no one answers correctly, waiting for piazza
									turn = (turn+1)%numOfTeam;
									currentPlayer = turn;
									announcement.append(currentTeam + ", you got the answer wrong!\nThe correct answer is: " + answer
											+ "\n$" + value + " will be deducted from your score\n");
									textArea.setText(announcement.toString());
									//update the team score on board
									teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());

									questionEnd = true;
									continue;
								}

								secondChance = true;
								pass.setEnabled(false);
								//give a second chance to answer
								warning.setText("Invalid format of your answer. Remember to pose your answer as a question.");
								warning.setVisible(true);
								announcement.append(currentTeam + " had a badly formatted answer.\nThey will get a second chance to answer\n");
								textArea.setText(announcement.toString());
								break;
							}
						}
					}
					if(questionEnd){ 
						endOfQuestion();
					}
					answerBox.setText("");
				}
			}
		});
	}
	//listener for sliders
	private class sliderListener implements ChangeListener{

		@Override
		public void stateChanged(ChangeEvent e) {
			// TODO Auto-generated method stub
			for(int i=0; i<finalSliders.length; i++){
				if(e.getSource()==finalSliders[i]){
					updateData(i);
				}
			}
		}
		private void updateData(int index){
			int value = finalSliders[index].getValue();
			if(value == 0){
				finalButtons[index].setEnabled(false);
			}
			else{
				finalButtons[index].setEnabled(true);
			}
			finalBetLabels[index].setText("$"+Integer.toString(value));
		}
	}
	//set bet buttons
	private class finalButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			for(int i=0; i<finalButtons.length; i++){
				if(e.getSource()==finalButtons[i]){
					updateData(i);
				}
			}
			checkFinishedBet();
		}
		private void updateData(int index){
			finalButtons[index].setEnabled(false);
			finalSliders[index].setEnabled(false);
			int bet = finalSliders[index].getValue();
			teams.get(index).setBet(bet);
			announcement.append(teams.get(index).getName() + " bets " + finalBetLabels[index].getText() + "\n");
			textArea.setText(announcement.toString());
		}
		private void checkFinishedBet(){
			boolean finishedBet = true;
			for(int i=0; i<finalButtons.length; i++){
				if (finalEligible[i] == true){
					if(finalSliders[i].getValue() == 0){
						finishedBet = false;
						break;
					}
					if(finalButtons[i].isEnabled()==true){
						finishedBet = false;
						break;
					}
				}
			}
			if(finishedBet){
				for(int i=0; i<finalButtons.length; i++){
					if (finalEligible[i] == true){
						fjSubmitButton[i].setEnabled(true);
						fjAnswerBox[i].setEditable(true);
					}
				}
				announcement.append("Here is the Final Jeopardy question:\n" + finalQuestion+"\n");
				textArea.setText(announcement.toString());
				fjPreview.setText(finalQuestion);
			}
		}
	}
	//submit answer button
	private class submitButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			for(int i=0; i<fjSubmitButton.length; i++){
				if(e.getSource() == fjSubmitButton[i]){
					updateData(i);
				}
			}
			checkEndGame();
		}
		//check the answer and  update team's status
		public void updateData(int index){
			fjAnswerBox[index].setEditable(false);
			fjSubmitButton[index].setEnabled(false);
			String ans = fjAnswerBox[index].getText();
			String[] answerSplit = ans.split(" ");
			//wrong answer: what is __, or empty, or one word
			if(answerSplit.length<=2){
				return;
			}
			for(int a=0; a<answerSplit.length; a++){
				answerSplit[a] = answerSplit[a].trim();
			}
			//who, what, when, where
			if ((answerSplit[0].equalsIgnoreCase("who")  || answerSplit[0].equalsIgnoreCase("where")||
					answerSplit[0].equalsIgnoreCase("what") || answerSplit[0].equalsIgnoreCase("when"))){
				//if the format is correct
				if(answerSplit[1].equalsIgnoreCase("is") || answerSplit[1].equalsIgnoreCase("are")){
					//store the real answer (without question (what is)) to temp
					String temp = "";
					for(int j=2; j<answerSplit.length; j++){
						temp = temp + " " + answerSplit[j];
					}
					temp = temp.substring(1);
					///correct answer
					if(finalAnswer.equalsIgnoreCase(temp)){
						teams.get(index).setFinalCorrect();
					}
				}
			}
		}
		//check if game ends
		public void checkEndGame(){
			boolean endGame = true;
			//when it's all finished
			for(int i=0; i<fjSubmitButton.length; i++){
				if(finalEligible[i]==true){
					if(fjSubmitButton[i].isEnabled()==true){
						endGame =false;
						break;
					}
				}
			}
			//if ends, printout  result
			if(endGame){
				fjPreview.setText("\t\tAnswer:  " + finalAnswer);
				for(int i=0; i<teams.size(); i++){
					if(teams.get(i).getBet()>0){
						if(teams.get(i).getfinalCorrect()){
							//correct
							teams.get(i).correct(teams.get(i).getBet());
							announcement.append(teams.get(i).getName() + " got the final question right!\n");
							textArea.setText(announcement.toString());
							teamScore.get(i).setText("$" + String.valueOf(teams.get(i).getPoint()));
						}
						else{
							//wrong
							teams.get(i).wrong(teams.get(i).getBet());
							announcement.append(teams.get(i).getName() + " got the final question wrong!\n");
							textArea.setText(announcement.toString());
							teamScore.get(i).setText("$" + String.valueOf(teams.get(i).getPoint()));
						}
					}
				}
				//if everyone is zero or below after Final Jeopardy
				boolean allZero = true;
				for(int i=0; i<teams.size(); i++){
					//if at least one team has positive value
					if(teams.get(i).getPoint() > 0){
						allZero = false;
						break;
					}
				}
				//there is no winner after FJ
				if(allZero){

					announcement.append("The game has ended. There were no Winners\n");
					textArea.setText(announcement.toString());
					RatingWindow rw = new RatingWindow("Sad!", "There were no Winners!", String.valueOf(totalRate/totalPeopleRated), 
							totalRate, totalPeopleRated, outFileArray, fileDestination);
					//don't restart but disabled all button
					rw.setLocationRelativeTo(MainWindow.this);
					rw.setVisible(true);
					for(int i=0; i<scoreBoard.size(); i++){
						scoreBoard.get(i).setEnabled(false);
						scoreBoard.get(i).setIcon(disabledImage);
						scoreBoard.get(i).setDisabledIcon(disabledImage);
					}
					cl.show(leftPanel, "1");
					return;
				}
				//Find the winner 
				ArrayList<Team> winner = new ArrayList<Team>();
				int max = 0;
				for(int i=0; i<teams.size(); i++){
					if(teams.get(i).getPoint() >= max){
						max = teams.get(i).getPoint();
					}
				}
				//store winners
				for(int i=0; i<teams.size(); i++){
					if(teams.get(i).getPoint() == max){
						winner.add(teams.get(i));
					}
				}
				//print out winners on progress box
				StringBuilder winners = new StringBuilder();
				for(int i=0; i<winner.size(); i++){
					winners.append(winner.get(i).getName() + " ");
				}
				//a3 some team wins!
				announcement.append("The game has ended. Winners are " + winners.toString());
				textArea.setText(announcement.toString());
				RatingWindow rw = new RatingWindow("Good!", "Winners are " + winners.toString(), String.valueOf(totalRate/totalPeopleRated), 
						totalRate, totalPeopleRated, outFileArray, fileDestination);
				//don't restart but disabled all button
				rw.setLocationRelativeTo(MainWindow.this);
				rw.setVisible(true);
				for(int i=0; i<scoreBoard.size(); i++){
					scoreBoard.get(i).setEnabled(false);
					scoreBoard.get(i).setIcon(disabledImage);
					scoreBoard.get(i).setDisabledIcon(disabledImage);
				}

				cl.show(leftPanel, "1");
				return;
			}
		}
	}
	private class answerBoxListener implements FocusListener{

		@Override
		public void focusGained(FocusEvent e) {
			for(int i=0; i<fjAnswerBox.length; i++){
				if(fjAnswerBox[i].isFocusOwner()){
					updateGained(i);
				}
			}
		}
		@Override
		public void focusLost(FocusEvent e) {
			for(int i=0; i<fjAnswerBox.length; i++){
				updateLost(i);
			}
		}
		private void updateGained(int index){
			if(fjAnswerBox[index].isEditable()){
				if(fjAnswerBox[index].getText().trim().equals(teams.get(index).getName() + ", enter your answer.")){
					fjAnswerBox[index].setText("");
				}
			}
		}
		private void updateLost(int index){
			if(fjAnswerBox[index].isEditable()){
				if(fjAnswerBox[index].getText().isEmpty()){
					fjAnswerBox[index].setText(teams.get(index).getName() + ", enter your answer.");
				}
			}
		}
	}
	public void displayFinalQuestion(){

		for(int i=0; i<teams.size(); i++){
			if(teams.get(i).getPoint() > 0){
				finalEligible[i] = true;
			}
		}
		for(int i=0; i<teams.size(); i++){


			if(finalEligible[i] == true){

				JLabel label = new JLabel(teams.get(i).getName() +"'s bet:");
				finalNameLabels[i] = label;
				label.setFont(new Font("Times New Roman", Font.PLAIN, 20)); 
				label.setForeground(Color.WHITE);
				label.setBorder(new EmptyBorder(15,10,15,10));
				finalNames.add(label);
				finalNames.add(Box.createRigidArea(new Dimension(0,50)));

				JSlider sl = new JSlider(JSlider.HORIZONTAL, 0, teams.get(i).getPoint(), 0);
				finalSliders[i] = sl;
				sl.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				sl.setBackground(Color.darkGray);
				sl.setForeground(Color.WHITE);
				sl.setPaintTicks(true);
				sl.setPaintLabels(true);
				if(teams.get(i).getPoint()<10){
					sl.setMajorTickSpacing(1);
				}
				else{
					sl.setMajorTickSpacing(teams.get(i).getPoint()/10);
				}
				sliders.add(sl);
				sl.setBorder(new EmptyBorder(20,20,30,20));

				JLabel betLabel = new JLabel("$0");
				finalBetLabels[i] = betLabel;
				betLabel.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				betLabel.setBackground(Color.darkGray);
				betLabel.setForeground(Color.WHITE);
				betLabel.setBorder(new EmptyBorder(15,10,15,20));
				bets.add(betLabel);
				bets.add(Box.createRigidArea(new Dimension(0,50)));

				JButton fButton = new JButton("Set Bet");
				finalButtons[i] = fButton;
				fButton.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				fButton.setForeground(Color.BLACK);
				fButton.setEnabled(false);
				fButton.setBorder(new EmptyBorder(15,10,15,20));
				buttons.add(fButton);
				buttons.add(Box.createRigidArea(new Dimension(0,50)));
			}
		}
		for(int i=0; i<teams.size(); i++){

			if(finalEligible[i] == true){
				JTextField fjBox = new JTextField(teams.get(i).getName() + ", enter your answer.");
				fjAnswerBox[i] = fjBox;
				fjBox.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				fjBox.setForeground(Color.gray);

				fjBox.setPreferredSize(new Dimension(200,30));
				fjBox.setEditable(false);

				JButton fjBut = new JButton("Submit Answer");

				fjSubmitButton[i] = fjBut;
				fjBut.setFont(new Font("Times New Roman", Font.PLAIN, 20));
				fjBut.setBorder(new EmptyBorder(5,5,5,5));
				fjBut.setEnabled(false);

				JPanel submitPanel = new JPanel();
				submitPanel.add(fjAnswerBox[i]);
				submitPanel.add(Box.createRigidArea(new Dimension(5,0)));
				submitPanel.add(fjSubmitButton[i]);
				submitPanel.setBackground(Color.darkGray);

				fjPanel.add(submitPanel);
			}
		} 
		sliderListener sl = new sliderListener();
		finalButtonListener bl = new finalButtonListener();
		submitButtonListener sbl = new submitButtonListener();
		answerBoxListener abl = new answerBoxListener();

		for(int i=0; i<teams.size(); i++){
			if(finalEligible[i] == true){
				finalSliders[i].addChangeListener(sl);
				finalButtons[i].addActionListener(bl);
				fjSubmitButton[i].addActionListener(sbl);
				fjAnswerBox[i].addFocusListener(abl);
			}
		} 
		cl.show(leftPanel, "3");
	}
	//public void displayQuestion(int index, int catIndex, String category, String name){ 
	public void displayQuestion(int action){ 
		
		//a5 clear clock when current player chooses a question
		if(networked){
			clearWaitToChooseTimer();
			waitToAnswer();
		}
		//disable button
		scoreBoard.get(action).setEnabled(false);
		//a3
		scoreBoard.get(action).setIcon(disabledImage);
		scoreBoard.get(action).setDisabledIcon(disabledImage);
		scoreBoard.get(action).setHorizontalTextPosition(JButton.CENTER);
		scoreBoard.get(action).setVerticalTextPosition(JButton.CENTER);
		//update announcement
		catIndex = action%5;
		String name = teams.get(turn).getName();
		String str = " chose the question in ";
		String str2 = " worth $";
		String category = categories[catIndex];
		currentPoint = Integer.parseInt(scoreBoard.get(action).getText());
		announcement.append(name + str + category + str2 + Integer.toString(currentPoint) + "\n");
		textArea.setText(announcement.toString());
		//display Question
		index = categoryList.get(action%5).getQuestionNum(currentPoint);
		String question = categoryList.get(catIndex).getQuestion(index);	 
		//team.setText(name);
		cat.setText(category);
		value.setText("$" + String.valueOf(currentPoint));	 
		questionBox.setText(question);
		cl.show(leftPanel, "2");
	}
	public void restart(){

		//determine if quick play or not
		if(quickplay){
			answeredQuestion = 20;
		}
		else{
			answeredQuestion = 0;
		}
		//reset every data
		for(int i=0; i<categoryList.size(); i++){
			categoryList.get(i).resetValues();
		}
		//reset team's status except name
		for(int i=0; i<teams.size(); i++){
			teams.get(i).resetValues();
		}
		//make all button visible
		for(int i=0; i<scoreBoard.size(); i++){
			scoreBoard.get(i).setEnabled(true);
			//a3 change color of the button back to blue
			scoreBoard.get(i).setIcon(enabledImage);
		}
		for(int i=0; i<teamScore.size(); i++){
			teamScore.get(i).setText("$"+teams.get(i).getPoint());
		}
		Random rand = new Random();
		turn = rand.nextInt(numOfTeam);
		currentPlayer = turn;
		teamTurn.setText("The team to go first is " + teams.get(turn).getName() + "\n");
		announcement = new StringBuilder();
		announcement.append(welcome.getText() + teamTurn.getText());
		textArea.setText(announcement.toString());
		currentPoint = 0;
		catIndex = -1;
		index = -1;
		secondChance = false;
		warning.setVisible(false);

		//CLEAR FINAL JEOPARDY
		fjPreview.setText("\t\tAnd the question is...");
		for(int i=0; i<numOfTeam; i++){
			finalEligible[i] = false;
		}
		finalNames.removeAll();
		sliders.removeAll();
		bets.removeAll();
		buttons.removeAll();
		fjPanel.removeAll();

		cl.show(leftPanel, "1");
	}


	//a4
	//method that disables all the questions if not team's turn
	public void disableAllQuestions(){
		//make all button visible
		for(int i=0; i<scoreBoard.size(); i++){
			scoreBoard.get(i).setEnabled(false);
		}
	}
	public void enabledUnansweredQuestions(){
		for(int i=0; i<scoreBoard.size(); i++){
			//if the question is not yet answered
			if(scoreBoard.get(i).getIcon().equals(enabledImage)){
				scoreBoard.get(i).setEnabled(true);
			}
		}
	}
	public GameData getGameData(){
		return gameData;
	}
	public void setTurn(int turn){
		this.turn = turn; 
	}
	//disabled submit answer box and submit answer button for non playing teams
	public void setSubmitAnswer(Boolean bool){
		submitButton.setEnabled(bool);
		answerBox.setEnabled(bool);
	}
	//set warning text
	public void setWarningText(String str){
		warning.setText(str);
	}
	public void displayTeamName(int currentTeam){
		String name = teams.get(currentTeam).getName();
		team.setText(name);
	}

	//enable buzz in for other players
	public void displayBuzzIn(String currentTeam, int value){	
		//this is what other teams should do
		//enable other teams to buzz in
		pass.setVisible(true);
		pass.setEnabled(true);
		//update game progress and warning
		announcement.append("Another team can buzz in within the next 20 seconds "
									+ "to answer the question!\n"); //a5
		textArea.setText(announcement.toString());
		warning.setText("buzz in to answer!");
	}

	//display buzz in for waiting players
	public void displayBuzzInForWaitingPlayers(String currentTeam, int value){	
		//this is what other teams should do
		//enable other teams to buzz in
		pass.setVisible(false);
		//update game progress and warning
		announcement.append("Another team can buzz in within the next 20 seconds "
									+ "to answer the question!\n");
		textArea.setText(announcement.toString());
		warning.setText(currentTeam + " got the answer wrong! " +  "Waiting for another team to buzz in");
	}
	//if correct, return a answer in string, if false, returns null
	public String correctAnswerFormat(String answer){

		String[] answerSplit = answer.split(" ");
		//if len is less than 3, can't be a right format
		if(answerSplit.length<3){
			return null;
		}
		//if user answer without question form (who, what, when, where)
		if (!(answerSplit[0].equalsIgnoreCase("who")  || answerSplit[0].equalsIgnoreCase("where")||
				answerSplit[0].equalsIgnoreCase("what") || answerSplit[0].equalsIgnoreCase("when"))){	
			return null;
		}
		//if user answer without question form (is, are)
		if(!(answerSplit[1].equalsIgnoreCase("is") || answerSplit[1].equalsIgnoreCase("are"))){
			return null;
		}
		String userAnswer = "";
		for(int i=2; i<answerSplit.length; i++){
			userAnswer = userAnswer + " " + answerSplit[i];
		}
		//if someone put "what is (blank)"
		if(userAnswer.isEmpty()){
			return null;
		}
		userAnswer = userAnswer.substring(1);
		//return the correct answer
		return userAnswer.trim();
	}
	//update when user answer get the answer right
	public void updateWhenCorrect(String currentTeam, String user_answer, int value){

		teams.get(currentPlayer).correct(value);
		announcement.append(currentTeam + ", got the answer right!\n$" + value + " will be added to your score\n");
		textArea.setText(announcement.toString());
		//update the team score on board
		teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
		//questionEnd = true;
		turn = currentPlayer;
		endOfQuestionNetwork();
	}

	public void updateWhenWrong(String currentTeam, String answer, int value, String eligibleBuzzInTeam, int teamTurn){

		//update game progress / scores
		teams.get(currentPlayer).wrong(value);
		teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
		announcement.append(currentTeam + ", got the answer wrong!\n$" + value + " will be deducted from your score\n");
		textArea.setText(announcement.toString());

		//check how mnay team tried to answer
		waitingTeamNames.add(currentTeam);
		//if no one answered it correctly
		if(waitingTeamNames.size()==numOfTeam){
			turn = (turn+1)%numOfTeam;
			currentPlayer = turn;
			//display the correct answer //a5
			announcement.append("The correct answer is: " + categoryList.get(catIndex).getAnswer(index)+"\n");
			textArea.setText(announcement.toString());
			//disable/enable button based on who's turn
			if(turn==player.getTeamTurn()){
				enabledUnansweredQuestions();
				answerBox.setEnabled(true);
				submitButton.setEnabled(true);
			}else{
				disableAllQuestions();
			}
			endOfQuestionNetwork();
		}
		// if some teams still have chance to buzee in
		else{
			//check if a team is eligible to buzz in
			Boolean eligible = true;
			for(int i=0; i<waitingTeamNames.size(); i++){
				if(eligibleBuzzInTeam.equals(waitingTeamNames.get(i))){
					eligible = false;
					break;
				}
			}
			if(eligible){
				displayBuzzIn(currentTeam, value);
			} else{
				displayBuzzInForWaitingPlayers(currentTeam, value);
			}
			//a5
			clearWaitToAnswerTimer();
			waitToBuzzIn();
			
		}
	}
	//when first chance is used, update other players game progress
	public void firstChanceWarningUpdate(String currentTeam){
		announcement.append(currentTeam + " had a badly formatted answer.\nThey will get a second chance to answer\n");
		textArea.setText(announcement.toString());
	}

	public void updateCurrentPlayerWrongAnswer(String currentTeam, int value){
		teams.get(currentPlayer).wrong(value);
		teamScore.get(currentPlayer).setText("$"+teams.get(currentPlayer).getPoint());
		announcement.append(currentTeam + ", got the answer wrong!\n$" + value + " will be deducted from your score\n");
		textArea.setText(announcement.toString());


		team.setText(""); //remove label after your chance is gone
		answerBox.setEnabled(false);
		submitButton.setEnabled(false);

		waitingTeamNames.add(currentTeam);
		//if you are the first team, your buzzing waiting period label is different
		if(waitingTeamNames.get(0).equals(currentTeam)){
			warning.setText("You got the answer wrong! Waiting for other teams to buzz in.");
		}
		else{
			warning.setText("You cannot buzz in again. Waiting for another team to buzz in.");
		}
		if(waitingTeamNames.size()==numOfTeam){
			turn = (turn+1)%numOfTeam;
			currentPlayer = turn;
			//print out the correct //a5
			announcement.append("The correct answer is: " + categoryList.get(catIndex).getAnswer(index)+"\n");
			textArea.setText(announcement.toString());
			
			
			//disable/enable button based on who's turn
			if(turn==player.getTeamTurn()){
				enabledUnansweredQuestions();
				answerBox.setEnabled(true);
				submitButton.setEnabled(true);
			}else{
				disableAllQuestions();
			}
			endOfQuestionNetwork();
		}
		else{
			announcement.append("Another team can buzz in within the next 20 seconds "
									+ "to answer the question!\n");
			textArea.setText(announcement.toString());
			
			//a5
			clearWaitToAnswerTimer();
			waitToBuzzIn();
		}
	}
	//update after someone buzz in 
	public void updateAfterBuzzIn(String currentPlayerName, int currentPlayerTurn){

		currentPlayer = currentPlayerTurn;
		String message = currentPlayerName + " buzzed in to answer.";
		warning.setText(message);
		pass.setVisible(false);
		announcement.append(message+"\n");
		textArea.setText(announcement.toString());
		//a5
		clearWaitToBuzzInTimer();
		waitToAnswer();

	}
	//whenever question is finished
	private void endOfQuestionNetwork(){

		waitingTeamNames.clear();
		pass.setVisible(false);
		answerBox.setText(null);
		warning.setText(null);
		answeredQuestion++;
		secondChance = false;
		turn = currentPlayer;
		
		//a5
		for(JLabel l:teamTimers){
			l.setIcon(null);
		}

		//if it's not end, then go back to questions
		if(answeredQuestion!=25){
			clearWaitToAnswerTimer();
			waitToChoose(); //a5
			announcement.append("Now it's " + teams.get(turn).getName() + "'s turn!\n" );
			textArea.setText(announcement.toString());
			cl.show(leftPanel, "1");
		} //when it is end
		else{
			//a5
			clearWaitToAnswerTimer();
			clearWaitToBuzzInTimer();
			
			//add the final teams
			for(int i=0; i<teams.size(); i++){
				if(teams.get(i).getPoint()>0){
					finalTeams.add(teams.get(i));
				}
			}
			//if no one gets it right
			if(finalTeams.size()==0){
				announcement.append("The game has ended. There were no Winners\n");
				textArea.setText(announcement.toString());
				//open rating window
				RatingWindow rw = new RatingWindow("Sad! "+player.getTeamName(), "There were no Winners!", String.valueOf(totalRate/totalPeopleRated), 
						totalRate, totalPeopleRated, outFileArray, fileDestination);
				rw.setLocationRelativeTo(MainWindow.this);

				rw.setMainWindow(MainWindow.this);
				rw.setNetwork(true); //indicating this is a network game
				rw.setTeamName(player.getTeamName()); // pass name when you create start window
				rw.setPlayer(player); //need when you pass the rating to every client / close socket
				if(player.getTeamTurn()==0){ //indicate you are a host
					rw.setHost(true);
				}
				rw.setVisible(true);

				//disableAllQuestions();
				//cl.show(leftPanel, "1");
				//return;
			}// if at least one is eligible
			else{
				if(teams.get(player.getTeamTurn()).getPoint()>0){
					displayFinalQuestionNetwork(player.getTeamName());
				}
				else{
					//open rating window
					RatingWindow rw = new RatingWindow("Sad! "+player.getTeamName(), "You have been disqualified", String.valueOf(totalRate/totalPeopleRated), 
							totalRate, totalPeopleRated, outFileArray, fileDestination);
					rw.setLocationRelativeTo(MainWindow.this);

					rw.setMainWindow(MainWindow.this);
					rw.setNetwork(true); //indicating this is a network game
					rw.setTeamName(player.getTeamName()); // pass name when you create start window
					rw.setPlayer(player); //need when you pass the rating to every client / close socket
					if(player.getTeamTurn()==0){ //indicate you are a host
						rw.setHost(true);
					}
					rw.setVisible(true);
				}
			}
		}		
	}

	public void displayFinalQuestionNetwork(String teamName){

		int thisTeam = player.getTeamTurn();
		//update slider maximum
		finalSliderNetwork.setMaximum(teams.get(thisTeam).getPoint());
		//set up slider mark based on points
		if(teams.get(thisTeam).getPoint()<10){
			finalSliderNetwork.setMajorTickSpacing(1);
		}
		else{
			finalSliderNetwork.setMajorTickSpacing(teams.get(thisTeam).getPoint()/10);
		}
		finalSliderNetwork.setValue(0);
		//update game progress
		announcement.append("Welcome to Final Jeopardy!\n");
		textArea.setText(announcement.toString());

		//waiting for which players to bet?
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<finalTeams.size(); i++){
			if(!finalTeams.get(i).getName().equals(teamName)){
				sb.append("Waiting for " + finalTeams.get(i).getName() + " to set their bet...\n");
			}
		}
		finalWarningLabelNetwork.setText(sb.toString());

		finalSetButtonNetwork.setEnabled(false);
		finalSubmitAnswerButtonNetwork.setEnabled(false);
		finalAnswerBoxNetwork.setEnabled(false);

		cl.show(leftPanel, "4");
	}

	public void updateOtherTeamsBet(int currentTeam, int otherTeamBet){
		teams.get(currentTeam).setBet(otherTeamBet);
	}

	public void updateBetWaitingStatus(String teamName){
		//update the warning
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<finalTeams.size(); i++){
			if(!finalTeams.get(i).getName().equals(teamName)){
				//if team still hasn't set their bet
				if(finalTeams.get(i).getBet()==0){
					sb.append("Waiting for " + finalTeams.get(i).getName() + " to set their bet...\n");
				}
				else{
					sb.append(finalTeams.get(i).getName() + " bet $" + finalTeams.get(i).getBet() + "\n");
				}
			}
		}
		finalWarningLabelNetwork.setText(sb.toString());
	}

	public void answerFinalQuestion(){

		//update game progress
		announcement.append("Here is the Final jeopardy question:\n"+finalQuestion+"\n");
		textArea.setText(announcement.toString());
		finalQuestionNetwork.setText(finalQuestion);
		//enable text and button
		finalAnswerBoxNetwork.setEnabled(true);
		finalAnswerBoxNetwork.setText("");
		finalAnswerBoxNetwork.setForeground(Color.BLACK);
		finalSubmitAnswerButtonNetwork.setEnabled(true);
		waitingAnswerNetwork.setText("Waiting for the rest of the players to answer...");
	}
	public void checkFinalAnswer(){



		//display final answer on question and game progress
		finalQuestionNetwork.setText(finalAnswer);
		announcement.append("Final Answer is:\n" + finalAnswer + "\n");
		textArea.setText(announcement.toString());


		//need to notify others to update the current score
		//player.updateFinalScore(player.getTeamTurn(), bet)


		int bet = teams.get(player.getTeamTurn()).getBet(); //get the bet amount
		//if it's correct format
		if(correctAnswerFormat(myTeamFinalAnswer)!=null){
			//it's right
			if(correctAnswerFormat(myTeamFinalAnswer).equals(finalAnswer)){
				teams.get(player.getTeamTurn()).setFinalCorrect(); //set boolean to true
				teams.get(player.getTeamTurn()).correct(bet); //add to the total
				//update game progress
				announcement.append(player.getTeamName() + " got the answer right!\n");
				textArea.setText(announcement.toString());
			} //it's wrong
			else{
				teams.get(player.getTeamTurn()).wrong(bet); //add to the total
				//update game progress
				announcement.append(player.getTeamName() + " got the answer wrong!\n");
				textArea.setText(announcement.toString());
			}
		} //wrong format, so wrong
		else{
			teams.get(player.getTeamTurn()).wrong(bet); //add to the total
			//update game progress
			announcement.append(player.getTeamName() + " got the answer wrong!\n");
			textArea.setText(announcement.toString());
		}


		//update the game board
		int currentPoint = teams.get(player.getTeamTurn()).getPoint();
		teamScore.get(player.getTeamTurn()).setText("$"+currentPoint);


		//send the boolean value if this team got the answer right
		//string that should update game progress of other windows, 
		//bet amount to update this team's score and the score label on top right
		boolean finalQuestionCorrect = teams.get(player.getTeamTurn()).getfinalCorrect();
		String name = player.getTeamName();
		//updateAfterTeamAnswersFinalQuestion(finalQuestionCorrect, name, bet, player.getTeamTurn());	
		player.updateAfterTeamAnswersFinalQuestion(finalQuestionCorrect, name, bet, player.getTeamTurn());
	}
	//update other team's arraylist<team> when someone answers final quesiton
	public void updateAfterTeamAnswersFinalQuestion(Boolean correct, String teamName, int teamBet, int teamTurn){
		//update game progress and team points
		if(correct){
			teams.get(teamTurn).setFinalCorrect();
			teams.get(teamTurn).correct(teamBet);
			announcement.append(teamName + " got the answer right!\n");
			textArea.setText(announcement.toString());
		}
		else{
			teams.get(teamTurn).wrong(teamBet);
			announcement.append(teamName + " got the answer wrong!\n");
			textArea.setText(announcement.toString());
		}
		//change label
		teamScore.get(teamTurn).setText("$"+teams.get(teamTurn).getPoint());

		startCheckingWinner++;
		//this is when you finish updating everyone's score for final winner
		if(startCheckingWinner==finalTeams.size()-1){
			findWinnerNetwork();
		}
	}
	public void findWinnerNetwork(){

		boolean allLose = true;
		for(int i=0; i<finalTeams.size(); i++){
			if(finalTeams.get(i).getPoint()>0){
				allLose = false;
			}
		}
		//if there is no winner
		if(allLose){
			announcement.append("The game has ended. There were no Winners\n");
			textArea.setText(announcement.toString());
			RatingWindow rw = new RatingWindow("Sad! "+player.getTeamName(), "There were no Winners!", String.valueOf(totalRate/totalPeopleRated), 
					totalRate, totalPeopleRated, outFileArray, fileDestination);
			//don't restart but disabled all button
			rw.setLocationRelativeTo(MainWindow.this);
			//		
			rw.setMainWindow(MainWindow.this);
			rw.setNetwork(true); //indicating this is a network game
			rw.setTeamName(player.getTeamName()); // pass name when you create start window
			rw.setPlayer(player); //need when you pass the rating to every client / close socket
			if(player.getTeamTurn()==0){ //indicate you are a host
				rw.setHost(true);
			}
			rw.setVisible(true);



			//disableAllQuestions();
			//cl.show(leftPanel, "1");
		} //if there is a winner, find it
		else{
			//Find the winner 
			ArrayList<Team> winner = new ArrayList<Team>();
			//find max number
			int max = 0;
			for(int i=0; i<finalTeams.size(); i++){
				if(finalTeams.get(i).getPoint() >= max){
					max = finalTeams.get(i).getPoint();
				}
			}
			//store winners with max number
			for(int i=0; i<finalTeams.size(); i++){
				if(finalTeams.get(i).getPoint() == max){
					winner.add(finalTeams.get(i));
				}
			}
			//print out winners on progress box
			StringBuilder winners = new StringBuilder();
			for(int i=0; i<winner.size(); i++){
				winners.append(winner.get(i).getName() + " ");
			}
			//a3 some team wins!
			announcement.append("The game has ended. Winners are " + winners.toString());
			textArea.setText(announcement.toString());
			RatingWindow rw = new RatingWindow("Good job! "+player.getTeamName(), "Winners are " + winners.toString(), String.valueOf(totalRate/totalPeopleRated), 
					totalRate, totalPeopleRated, outFileArray, fileDestination);
			//don't restart but disabled all button
			rw.setLocationRelativeTo(MainWindow.this);

			rw.setMainWindow(MainWindow.this);
			rw.setNetwork(true); //indicating this is a network game
			rw.setTeamName(player.getTeamName()); // pass name when you create start window
			rw.setPlayer(player); //need when you pass the rating to every client / close socket
			if(player.getTeamTurn()==0){ //indicate you are a host
				rw.setHost(true);
			}
			rw.setVisible(true);

		}
	}

	public void restartGameNetwork(int newTurn){
		//need to figure out how to deal with random team number 
		//determine if quick play or not
		if(quickplay){
			answeredQuestion = 20;
		}
		else{
			answeredQuestion = 0;
		}
		//reset every data
		for(int i=0; i<categoryList.size(); i++){
			categoryList.get(i).resetValues();
		}
		//reset team's status except name
		for(int i=0; i<teams.size(); i++){
			teams.get(i).resetValues();
		}


		enabledImage = new ImageIcon(enabledQuestionImagePath);
		enabledImage.getImage().flush();
		//make all button visible
		for(int i=0; i<scoreBoard.size(); i++){
			//scoreBoard.get(i).setEnabled(true);
			//scoreBoard.get(i).setDisabledIcon(enabledImage);
			//a3 change color of the button back to blue
			scoreBoard.get(i).setIcon(enabledImage);
			scoreBoard.get(i).setDisabledIcon(enabledImage);

		}
		for(int i=0; i<teamScore.size(); i++){
			teamScore.get(i).setText("$"+teams.get(i).getPoint());
		}

		//Random rand = new Random();
		//turn = rand.nextInt(numOfTeam);
		turn = newTurn;
		currentPlayer = turn;
		teamTurn.setText("The team to go first is " + teams.get(turn).getName() + "\n");
		announcement = new StringBuilder();
		announcement.append(welcome.getText() + teamTurn.getText());
		textArea.setText(announcement.toString());
		currentPoint = 0;
		catIndex = -1;
		index = -1;
		secondChance = false;
		
		//a5
		warning.setText(null);

		//CLEAR FINAL JEOPARDY
		fjPreview.setText("\t\tAnd the question is...");
		for(int i=0; i<numOfTeam; i++){
			finalEligible[i] = false;
		}
		finalNames.removeAll();
		sliders.removeAll();
		bets.removeAll();
		buttons.removeAll();
		fjPanel.removeAll();

		//a4 new remove

		team.setText("");
		waitingTeamNames.clear();

		if(player.getTeamTurn()==turn){
			this.enabledUnansweredQuestions();
		}else{
			this.disableAllQuestions();
		}
		finalAnswerBoxNetwork.setText(player.getTeamName()+"'s answer");

		//a4
		//final jeopardy new slider gui
		finalTeams.clear();
		finalAnsweredTeams = 0;
		finalSliderNetwork.setEnabled(true);
		finalAmountLabelNetwork.setText("$0");
		finalSetButtonNetwork.setText("Set Bet");
		finalSetButtonNetwork.setEnabled(true);
		waitingAnswerNetwork.setText("");
		finalWarningLabelNetwork.setText("");
		finalQuestionNetwork.setText("Wait for it...");	
		startCheckingWinner = 0;
		
		//a5
		for(JLabel l:teamTimers){
			l.setIcon(null);
		}
		clearWaitToChooseTimer();
		clearWaitToAnswerTimer();
		clearWaitToBuzzInTimer();
		waitToChoose();
		
		
		cl.show(leftPanel, "1");
	}
	public void goToStartWindowWhenSomeoneExit(){


		Object[] options = {"OK"};
		int input = JOptionPane.showOptionDialog(this, "One of the players has exited the game", player.getTeamName()+"'s Announcement", 
				JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		if(input == JOptionPane.OK_OPTION){
			dispose();
			//if you are host, need to close serversocket
			if(player.getTeamTurn()==0){
				player.closeHostServer();
			}
			StartWindow sw = new StartWindow(player.getTeamName());
			sw.setVisible(true);
		}
	}

	public int getFinalAnswerTeams(){
		return finalAnsweredTeams;
	}
	public void setFinalAnswerTeams(int num){
		finalAnsweredTeams = num;
	}
	public ArrayList<Team> getTeams(){
		return teams;
	}
}