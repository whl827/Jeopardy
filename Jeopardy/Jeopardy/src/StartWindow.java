

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;


public class StartWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<Team> teams;
	ArrayList<Category> categoryList;
	private String[] categories;//topics of categories
	private String[] points;//possible point values
	private int answeredQuestion;
	int numOfTeam;
	String[] teamName;
	private String finalQuestion;
	private String finalAnswer;
	
	private int previousTeamNum;
	
	//a3
	private String categoryLabelImagePath;
	private String enabledQuestionImagePath;
	private String disabledQuestionImagePath;
	
	private int totalPeopleRated;
	private int totalRate;
	private ArrayList<String> outFileArray;
	private String fileDestination;
	private int averageRate;
	JLabel averageRateText;
	
	
	private JFileChooser fc;
	private JCheckBox quickPlay;
	private JButton fileButton, start, clear, exit, logout;
	private JLabel title1, title2, fileText, numTeamText, fileName,
				   team1NameText, team2NameText, team3NameText, team4NameText;
	private JTextField team1NameBox, team2NameBox, team3NameBox, team4NameBox;
	private JSlider slider;
	private JPanel team1,team2,team3,team4;
	
	private final int MAX_TEAM =4;
	
	
	//a4
	private JRadioButton notNetworkedButton, hostGameButton, joinGameButton;
	private ButtonGroup radioGroup;
	
	//for host game
	private JTextField portTextBox;
	//join
	private JTextField ipAddressTextBox;
	boolean notNetworkedSelected, hostGameSelected, joinGameSelected;
	private String username;
	
	private JTextField waitingUpdateText;
	
	private GameData gameData;
	
	private HostServer host;
	private PlayerServer client;
	
	
	public void clearData(){
		teams.clear();
		categoryList.clear();
		for(int i=0; i<categories.length; i++){
			categories[i] = "";
		}
		for(int i=0; i<points.length; i++){
			points[i] = "";
		}
		slider.setValue(0);
		fileName.setText("");
		averageRateText.setText("");
		quickPlay.setSelected(false);
		start.setEnabled(false);
		answeredQuestion = 0;
		numOfTeam = 0;
		finalQuestion = null;
		finalAnswer = null;
		previousTeamNum = 0;
		fc = new JFileChooser();
		team1NameBox.setText("");
		team2NameBox.setText("");
		team3NameBox.setText("");
		team4NameBox.setText("");
		for(int i=0; i<MAX_TEAM; i++){
			teamName[i] = null;
		}
		FileNameExtensionFilter filter = new FileNameExtensionFilter("txt", "txt");
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		
		//a4
		notNetworkedButton.setSelected(true);
		
		notNetworkedSelected = true;
		hostGameSelected = false;
		joinGameSelected=  false;
		start.setText("Start Jeopardy");
		portTextBox.setText("port");
		ipAddressTextBox.setText("IP Address");
		portTextBox.setForeground(Color.gray);
		ipAddressTextBox.setForeground(Color.gray);
		waitingUpdateText.setText("");;
		buttonsWhileWaiting(true);

	}
	
	public StartWindow(String username) {
		super("Jeopardy Menu");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//a4
		notNetworkedSelected = true;
		hostGameSelected = false;
		joinGameSelected=  false;
		this.username = username;

		
		//a3
		categoryLabelImagePath = null;
		enabledQuestionImagePath = null;
		disabledQuestionImagePath = null;
		
		initializeComponents();
		createGUI();
		addEvents();
		previousTeamNum = 0;
		//Ask the user when quitting by clicking X
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent evt)
            {
                Object[] options = {"Yes", "No"};

                int answer = JOptionPane.showOptionDialog(StartWindow.this, "Are you sure you want to quit? ","Close Window", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                             null, options,options[1]);
                if(answer == JOptionPane.YES_OPTION)
                {
                    System.exit(0); 
                }
            }
		});

		teams = new ArrayList<Team>();
		categoryList = new ArrayList<Category>();
		categories = null;	//topics of categories
		points = null;		//possible point values
		answeredQuestion = 0;
		numOfTeam = 0;
		teamName = new String[MAX_TEAM];
		for(int i=0; i<MAX_TEAM; i++){
			teamName[i] = null;
		}
		team1NameBox.setText("");
		team2NameBox.setText("");
		team3NameBox.setText("");
		team4NameBox.setText("");
	}
	
	private void initializeComponents() {
		
		//a4	
		notNetworkedButton = new JRadioButton("Not Networked");
		hostGameButton = new JRadioButton("Host Game");
		joinGameButton = new JRadioButton("Join Game");
		radioGroup = new ButtonGroup();
		radioGroup.add(notNetworkedButton);
		radioGroup.add(hostGameButton);
		radioGroup.add(joinGameButton);
		notNetworkedButton.setBackground(new Color(153,204,255));
		hostGameButton.setBackground(new Color(153,204,255));
		joinGameButton.setBackground(new Color(153,204,255));
		notNetworkedButton.setSelected(true);
		//host game
		portTextBox = new JTextField("port");
		//join
		ipAddressTextBox = new JTextField("IP Address");
		portTextBox.setForeground(Color.gray);
		ipAddressTextBox.setForeground(Color.gray);
		
		portTextBox.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		ipAddressTextBox.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		
		waitingUpdateText = new JTextField();
		waitingUpdateText.setEnabled(false);
		waitingUpdateText.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		waitingUpdateText.setForeground(Color.WHITE);
		waitingUpdateText.setBorder(null);
		
		
		fc = new JFileChooser("user.dir");
		quickPlay = new JCheckBox("Quick Play");
		slider = new JSlider(1,4);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt");
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		
		team1 = new JPanel(); team2 = new JPanel();
		team3 = new JPanel(); team4 = new JPanel();
		
		fileButton = new JButton("Choose File");
		fileButton.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		
		start = new JButton("Start Jeopardy");
		start.setFont(new Font("Times New Roman", Font.PLAIN, 29));
		clear = new JButton("Clear Choices");
		clear.setFont(new Font("Times New Roman", Font.PLAIN, 29));
		exit = new JButton("Exit");
		exit.setFont(new Font("Times New Roman", Font.PLAIN, 29));
		logout = new JButton("Logout");
		logout.setFont(new Font("Times New Roman", Font.PLAIN, 29));
		
		
		
		title1 = new JLabel("Welcome to Jeopardy!");
		title1.setFont(new Font("Times New Roman", Font.PLAIN, 40));
		title2 = new JLabel("Choose whether you are joining or hosting , "
							+"a game or playing not-networked.");
		title2.setFont(new Font("Times New Roman", Font.PLAIN, 23));
		
		fileText = new JLabel("Please choose a game file.");
		fileText.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		fileText.setForeground(Color.WHITE);
		
		numTeamText = new JLabel("Please choose the number of teams that will be playing on the slider below.");
		numTeamText.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		numTeamText.setForeground(Color.WHITE);
		
		fileName = new JLabel("");
		fileName.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		fileName.setForeground(Color.WHITE);
		//a3
		averageRateText = new JLabel("");
		averageRateText.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		averageRateText.setForeground(Color.WHITE);
		
		team1NameText = new JLabel("Please name Team 1");
		team1NameText.setFont(new Font("Times New Roman", Font.PLAIN, 35));
		team1NameText.setAlignmentX(CENTER_ALIGNMENT);
		team1NameText.setForeground(Color.WHITE);
		
		team1NameBox = new JTextField(10);
		team1NameBox.setFont(new Font("Times New Roman", Font.PLAIN, 35));
		
		team2NameText = new JLabel("Please name Team 2");
		team2NameText.setFont(new Font("Times New Roman", Font.PLAIN, 35));
		team2NameText.setAlignmentX(CENTER_ALIGNMENT);
		team2NameText.setForeground(Color.WHITE);
		
		team2NameBox = new JTextField(10);
		team2NameBox.setFont(new Font("Times New ROman", Font.PLAIN, 35));
		
		team3NameText = new JLabel("Please name Team 3");
		team3NameText.setFont(new Font("Times New Roman", Font.PLAIN, 35));
		team3NameText.setAlignmentX(CENTER_ALIGNMENT);
		team3NameText.setForeground(Color.WHITE);
		
		team3NameBox = new JTextField(10);
		team3NameBox.setFont(new Font("Times New ROman", Font.PLAIN, 35));
		
		team4NameText = new JLabel("Please name Team 4");
		team4NameText.setFont(new Font("Times New Roman", Font.PLAIN, 35));
		team4NameText.setAlignmentX(CENTER_ALIGNMENT);
		team4NameText.setForeground(Color.WHITE);
		
		team4NameBox = new JTextField(10);
		team4NameBox.setFont(new Font("Times New ROman", Font.PLAIN, 35));
	
	}

	private void createGUI() {

		setSize(800, 825);
		//setSize(500,525); => you can use this to test the reduced size!
		setLocationRelativeTo(null);
		
		JPanel mainTopPanel = new JPanel();
		mainTopPanel.setLayout(new BoxLayout(mainTopPanel, BoxLayout.Y_AXIS));
		
		JPanel topPanel = new JPanel();
		JPanel topPanel2 = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		topPanel2.setLayout(new BoxLayout(topPanel2, BoxLayout.X_AXIS));
		
		topPanel2.add(title1);
		topPanel2.add(Box.createRigidArea(new Dimension(20,0)));
		topPanel2.add(quickPlay);
		quickPlay.setHorizontalTextPosition(SwingConstants.LEFT);
		
		quickPlay.setBackground(new Color(153,204,255));
		topPanel2.setBackground(new Color(153,204,255));
		topPanel.setBackground(new Color(153,204,255));

		topPanel.add(topPanel2);
		topPanel.add(Box.createRigidArea(new Dimension(0,30)));
		topPanel.add(title2);
		
		//a4
		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.X_AXIS));
		radioPanel.add(notNetworkedButton);
		radioPanel.add(hostGameButton);
		radioPanel.add(joinGameButton);

		topPanel.add(radioPanel);
		
		title1.setAlignmentX(CENTER_ALIGNMENT);
		title2.setAlignmentX(CENTER_ALIGNMENT);

		JPanel filePanel = new JPanel();
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
		fileButton.setBackground(Color.darkGray);
		fileButton.setForeground(Color.WHITE);
		
		filePanel.add(fileText);
		filePanel.add(Box.createRigidArea(new Dimension(10,50)));
		filePanel.add(fileButton);
		filePanel.add(Box.createRigidArea(new Dimension(10,50)));
		filePanel.add(fileName);
		//a3
		filePanel.add(Box.createRigidArea(new Dimension(20,0)));
		filePanel.add(averageRateText);
	    filePanel.setBackground(new Color(0,0,153));

		slider.setMajorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setBackground(Color.darkGray);
		slider.setForeground(Color.WHITE);
		
		JLabel j1 = new JLabel("1");
		j1.setForeground(Color.white);
		j1.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		JLabel j2 = new JLabel("2");
		j2.setForeground(Color.white);
		j2.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		JLabel j3 = new JLabel("3");
		j3.setForeground(Color.white);
		j3.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		JLabel j4 = new JLabel("4");
		j4.setForeground(Color.white);
		j4.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		
		Hashtable labels = new Hashtable();
	    labels.put(1, j1); labels.put(2, j2);
        labels.put(3, j3); labels.put(4, j4);

        slider.setValue(1);
	    slider.setLabelTable(labels);
	    slider.setPaintLabels(true);
	    
		mainTopPanel.add(topPanel);
		
		
//a4
	    
	    JPanel portAndIpBox = new JPanel();
	    
	    portAndIpBox.setLayout(new BoxLayout(portAndIpBox, BoxLayout.X_AXIS));
	    
	    portAndIpBox.add(Box.createRigidArea(new Dimension(50,0)));
	    portAndIpBox.add(portTextBox);
	    portAndIpBox.add(Box.createRigidArea(new Dimension(20,0)));
	    portAndIpBox.add(ipAddressTextBox);
	    portAndIpBox.add(Box.createRigidArea(new Dimension(50,0)));
	    portAndIpBox.setBackground(new Color(0,0,153));
	    mainTopPanel.add(portAndIpBox);
	    portTextBox.setVisible(false);
	    ipAddressTextBox.setVisible(false);
		
		mainTopPanel.add(filePanel);
	    numTeamText.setAlignmentX(CENTER_ALIGNMENT);
	    mainTopPanel.add(numTeamText);
	    mainTopPanel.add(slider);
	 
	    mainTopPanel.setBackground(new Color(0,0,153));
		add(mainTopPanel, BorderLayout.NORTH);
		
		//TEAM NAME TEXTBOX
		JPanel teamMain = new JPanel();
		teamMain.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		team1.setLayout(new BoxLayout(team1, BoxLayout.Y_AXIS));
		team2.setLayout(new BoxLayout(team2, BoxLayout.Y_AXIS));
		team3.setLayout(new BoxLayout(team3, BoxLayout.Y_AXIS));
		team4.setLayout(new BoxLayout(team4, BoxLayout.Y_AXIS));

		team1NameText.setBackground(Color.darkGray); team1NameText.setOpaque(true);
		team2NameText.setBackground(Color.darkGray); team2NameText.setOpaque(true);
		team3NameText.setBackground(Color.darkGray); team3NameText.setOpaque(true);
		team4NameText.setBackground(Color.darkGray); team4NameText.setOpaque(true);

		team1NameBox.setBackground(new Color(153,204,255)); team2NameBox.setBackground(new Color(153,204,255));
		team3NameBox.setBackground(new Color(153,204,255)); team4NameBox.setBackground(new Color(153,204,255));
		
		team1.add(team1NameText); team2.add(team2NameText); team3.add(team3NameText); team4.add(team4NameText);
		team1.add(team1NameBox); team2.add(team2NameBox); team3.add(team3NameBox); team4.add(team4NameBox);
		
		//a3 resize
		team1.setMinimumSize(new Dimension(225,75));
		team2.setMinimumSize(new Dimension(225,75));
		team3.setMinimumSize(new Dimension(225,75));
		team4.setMinimumSize(new Dimension(225,75));
		
		gbc.insets = new Insets(12,15,12,15);
		gbc.gridx = 0; gbc.gridy = 0; teamMain.add(team1, gbc);
		gbc.gridx = 0; gbc.gridy = 1; teamMain.add(team2, gbc);
		gbc.gridx = 1; gbc.gridy = 0; teamMain.add(team3, gbc);
		gbc.gridx = 1; gbc.gridy = 1; teamMain.add(team4, gbc);
		
		teamMain.setMinimumSize(new Dimension(100, 100));
		
		teamMain.setBackground(new Color(0,0,153));
		add(teamMain, BorderLayout.CENTER);
		
		team1.setVisible(true); team2.setVisible(false); team3.setVisible(false); team4.setVisible(false);
		
		//3 buttons
		start.setBackground(Color.darkGray); clear.setBackground(Color.darkGray); 
		start.setForeground(Color.white); 	 clear.setForeground(Color.white); 
		exit.setBackground(Color.darkGray);  logout.setBackground(Color.darkGray);
		exit.setForeground(Color.white);     logout.setForeground(Color.white);
		start.setEnabled(false);
		//resize
		start.setMargin(new Insets(5, 0, 5, 0));
		exit.setMargin(new Insets(5, 0, 5, 0));
		logout.setMargin(new Insets(5, 0, 5, 0));
		clear.setMargin(new Insets(5, 0, 5, 0));
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridBagLayout());
		
		gbc.insets = new Insets(0,0,5,0);
	
		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; buttons.add(start, gbc);
		gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1; buttons.add(clear, gbc);
		gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 2; buttons.add(logout, gbc);
		gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1; buttons.add(exit, gbc);
		
		clear.setPreferredSize(start.getPreferredSize()); exit.setPreferredSize(start.getPreferredSize());
		buttons.setBackground(new Color(0,0,153));
		
		buttons.setMinimumSize(new Dimension(500, 30));
		
		
		
		//a4
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.add(waitingUpdateText);
		bottomPanel.add(Box.createRigidArea(new Dimension(0,50)));
		bottomPanel.add(buttons);
		bottomPanel.setBackground(new Color(0,0,153));
		waitingUpdateText.setBackground(new Color(0,0,153));
		waitingUpdateText.setHorizontalAlignment(SwingConstants.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	
	}
	private void addEvents() {	
		
		//a4
		ActionListener actionListener = new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				 int action = Integer.parseInt(e.getActionCommand());
				 switch(action){
					 case 0: //notNetworkedButton
						 
						 
						 team1NameText.setText("Please name Team 1");
						 team1NameBox.setText("");
						 start.setText("Start Jeopardy");
						 
						 notNetworkedSelected = true; hostGameSelected = false; joinGameSelected = false;
						 
						 slider.setVisible(true); slider.setMinimum(1); slider.setValue(1);
						 portTextBox.setVisible(false); ipAddressTextBox.setVisible(false);

						 team2.setVisible(false);team3.setVisible(false); team4.setVisible(false);
						 
						 quickPlay.setVisible(true);
						 
						 fileText.setVisible(true);
							fileButton.setVisible(true);
							fileName.setVisible(true);
							numTeamText.setVisible(true);
							slider.setVisible(true);
							averageRateText.setVisible(true);

						 break;
					 case 1: //hostGameButton
						 team1NameText.setText("Please choose a team name");
						 team1NameBox.setText(username);
						 start.setText("Start Game");
						 
						 notNetworkedSelected = false; hostGameSelected = true; joinGameSelected = false;
						 
						 slider.setVisible(true); slider.setMinimum(2);  slider.setValue(2);
						 portTextBox.setVisible(true); ipAddressTextBox.setVisible(false);
						 portTextBox.setText("port");
						 portTextBox.setForeground(Color.gray);
						 
						 team2.setVisible(false); team3.setVisible(false); team4.setVisible(false);
						 
						 quickPlay.setVisible(true);
						 
						fileText.setVisible(true);
						fileButton.setVisible(true);
						fileName.setVisible(true);
						numTeamText.setVisible(true);
						slider.setVisible(true);
						averageRateText.setVisible(true);
						
						 
						 break;
					 case 2: //joinGameButton
						 team1NameText.setText("Please choose a team name");
						 team1NameBox.setText(username);
						 start.setText("Join Game");
						 
						 notNetworkedSelected = false; hostGameSelected = false; joinGameSelected = true;
						 
						 slider.setVisible(false);
						 portTextBox.setVisible(true); ipAddressTextBox.setVisible(true);
						 portTextBox.setText("port"); ipAddressTextBox.setText("IP Address");	
						 portTextBox.setForeground(Color.gray); ipAddressTextBox.setForeground(Color.gray);	

						 team2.setVisible(false); team3.setVisible(false); team4.setVisible(false);
						 
						 
						 quickPlay.setVisible(false);
						 
						fileText.setVisible(false);
						fileButton.setVisible(false);
						fileName.setVisible(false);
						numTeamText.setVisible(false);
						slider.setVisible(false);
						averageRateText.setVisible(false);
						 
						 break;
				 }
			}
		};
		notNetworkedButton.addActionListener(actionListener);
		notNetworkedButton.setActionCommand(String.valueOf(0));
		hostGameButton.addActionListener(actionListener);
		hostGameButton.setActionCommand(String.valueOf(1));
		joinGameButton.addActionListener(actionListener);
		joinGameButton.setActionCommand(String.valueOf(2));
		
		
		
		logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				LoginWindow lw = new LoginWindow();
				lw.setVisible(true);
				dispose();
				//setVisible(false);
				if(hostGameSelected && !clear.isEnabled()){
					host.closeSocket();
				}
				if(joinGameSelected && !clear.isEnabled()){
					String clientName = team1NameBox.getText();
					client.clientLogsOut(clientName);
				}
				
			}
		});
		
		
		//set quick play function
		quickPlay.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e){
				if(quickPlay.isSelected()){
					answeredQuestion = 20;
				}
				else{
					answeredQuestion = 0;
				}
			}
		});
		//open "choose file" when button is clicked
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {		
				boolean validFile = false;
				while(!validFile){
					try{
						//show error message or store the valid file name
						int result = fc.showOpenDialog(StartWindow.this);
						if(fc.getSelectedFile()==null || result == fc.CANCEL_OPTION){
							validFile = true;
						}
						else{
							File f = fc.getSelectedFile();
							categoryList.clear();
							FileChecker fileChecker = new FileChecker();
							fileChecker.clearData();
							validFile = fileChecker.checkFile(f);
							if(validFile){
								categoryList = fileChecker.getCategoryList();
								categories = fileChecker.getCategories();
								points = fileChecker.getPoints();
								fileName.setText(fc.getName(f));
								finalQuestion = fileChecker.getFinalQ();
								finalAnswer = fileChecker.getFinalA();
								
								//a3 images
								categoryLabelImagePath = fileChecker.getCategoryLabelImagePath();
								enabledQuestionImagePath = fileChecker.getEnabledQuestionImagePath();
								disabledQuestionImagePath = fileChecker.getDisabledQuestionImagePath();
								//a3 get file destination
								fileDestination = fc.getSelectedFile().getAbsolutePath();
								totalPeopleRated = fileChecker.getTotalPeopleRated();
								totalRate = fileChecker.getTotalRate();
								outFileArray = fileChecker.getArrayList();
								if(totalPeopleRated == -1 || totalRate == -1){
									averageRateText.setText("no rating");
									averageRate = 0;
								}
								else{
									averageRate = totalRate/totalPeopleRated;
									averageRateText.setText("average rating: " + averageRate + "/5");
								}
							}
						}
					} catch(FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				if(notNetworkedSelected){
					updateStartButton();
				}else if(hostGameSelected){
					updateStartButtonHost();
				}else if(joinGameSelected){
					updateStartButtonJoin();
				}
			}
		});
		//show number of boxes depending on number of team
		slider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e){
				int teamNum = slider.getValue();
				
				if(notNetworkedSelected){ //a4
					switch(teamNum){
						case 1:
							team1.setVisible(true); team2.setVisible(false); team3.setVisible(false); team4.setVisible(false);
							if(teamNum<previousTeamNum){
								team2NameBox.setText("");
							}
							break;
						case 2:
							team1.setVisible(true); team2.setVisible(true); team3.setVisible(false); team4.setVisible(false);
							if(teamNum<previousTeamNum){
								team3NameBox.setText("");
							}
							break;
						case 3:
							team1.setVisible(true);team2.setVisible(true); team3.setVisible(true); team4.setVisible(false);
							if(teamNum<previousTeamNum){
								team4NameBox.setText("");
							}
							break;
						case 4:
							team1.setVisible(true); team2.setVisible(true); team3.setVisible(true); team4.setVisible(true);
							break;
					}
					previousTeamNum = teamNum;
					updateStartButton();
				}
			}
		});
		
		//a4
		portTextBox.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				update();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	update();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	update();
	        }
	        public void setTeamName(){
	        	teamName[0] = team1NameBox.getText().trim();
	        }	
	        private void update(){
				if(hostGameSelected){
					updateStartButtonHost(); setTeamName();
				}
				else if(joinGameSelected == true){
					updateStartButtonJoin(); setTeamName();
				}
	        }
		});
		ipAddressTextBox.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				update();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	update();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	update();
	        }
	        public void setTeamName(){
	        	teamName[0] = team1NameBox.getText().trim();
	        }	
	        private void update(){
				if(hostGameSelected){
					updateStartButtonHost(); setTeamName();
				}
				else if(joinGameSelected == true){
					updateStartButtonJoin(); setTeamName();
				}
	        }
		});
		ghostTextListener gtl = new ghostTextListener("port", portTextBox);
		ghostTextListener gtl2 = new ghostTextListener("IP Address", ipAddressTextBox);
		portTextBox.addFocusListener(gtl);
		ipAddressTextBox.addFocusListener(gtl2);
		
		team1NameBox.getDocument().addDocumentListener(new DocumentListener() {
	        public void changedUpdate(DocumentEvent e) {
	        	update();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	update();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	update();
	        }
	        public void setTeamName(){
	        	teamName[0] = team1NameBox.getText().trim();
	        }
	        private void update(){
	        	if(notNetworkedSelected){
	        		updateStartButton();
	        	}else if(hostGameSelected){
	        		updateStartButtonHost();
	        	}else if(joinGameSelected){
	        		updateStartButtonJoin();
	        	}
	        	setTeamName();
	        }
	        
	    });
		team2NameBox.getDocument().addDocumentListener(new DocumentListener() {
	        public void changedUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void setTeamName(){
	        	teamName[1] = team2NameBox.getText().trim();
	        }
	    });
		team3NameBox.getDocument().addDocumentListener(new DocumentListener() {
	        public void changedUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void setTeamName(){
	        	teamName[2] = team3NameBox.getText().trim();
	        }
	    });
		team4NameBox.getDocument().addDocumentListener(new DocumentListener() {
	        public void changedUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	updateStartButton(); setTeamName();
	        }
	        public void setTeamName(){
	        	teamName[3] = team4NameBox.getText().trim();
	        }
	    });
		//open when start button clicks
		start.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				
				
				numOfTeam =	slider.getValue();
				//store teams
				ArrayList<String> finalTeamName = new ArrayList<>();
				for(int i=0; i<teamName.length; i++){
					if(teamName[i]==null || teamName[i].equals("")){
						continue;
					}
					finalTeamName.add(teamName[i]);
				}
				//add team with name
				for(int i=0; i<finalTeamName.size(); i++){
					Team team = new Team(finalTeamName.get(i));
					teams.add(team);
				}

				//teams and numofteam needs to be udpated it after everyone joins the game
				gameData = new GameData(teams, categoryList, categories, points, 
												   answeredQuestion, numOfTeam, finalQuestion, finalAnswer,
												   categoryLabelImagePath, enabledQuestionImagePath, 
												   disabledQuestionImagePath, totalPeopleRated, totalRate, fileDestination, outFileArray);
				
				if(notNetworkedSelected){
					//set all the data
					
					Random rand = new Random();
					int num = rand.nextInt(numOfTeam);
					
					MainWindow mw = new MainWindow(gameData, num);
					mw.getStartWindow(StartWindow.this);
					mw.setVisible(true);
					setVisible(false);
				}
				//a4
				else if (hostGameSelected){
					//disable buttons while waiting
					buttonsWhileWaiting(false);
					//get he port number
					int port = Integer.parseInt(portTextBox.getText().toString());

					 host = new HostServer(port, gameData);
					try {
						client = new PlayerServer(InetAddress.getLocalHost().getHostAddress(), port, StartWindow.this);
					} catch (UnknownHostException e) {
			
					}
				}
				else if(joinGameSelected){
					
					buttonsWhileWaiting(false);
					int port = Integer.parseInt(portTextBox.getText().toString());
					String ip = ipAddressTextBox.getText().toString();
					client = new PlayerServer(ip, port, StartWindow.this);

				}
			}
		});
		//add clear function
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				//clear names
				team1NameBox.setText("");
				team2NameBox.setText("");
				team3NameBox.setText("");
				team4NameBox.setText("");
				for(int i=0; i<teams.size(); i++){
					teams.get(i).setName("");
				}
				//set slider = 1
				slider.setValue(1);
				fileName.setText("");
				averageRateText.setText("");
				quickPlay.setSelected(false);
				
				//a4
				notNetworkedButton.setSelected(true);
				notNetworkedSelected = true;
				hostGameSelected = false;
				joinGameSelected=  false;
				start.setText("Start Jeopardy");
				portTextBox.setText("port");
				ipAddressTextBox.setText("IP Address");
				portTextBox.setForeground(Color.gray);
				ipAddressTextBox.setForeground(Color.gray);
				
				waitingUpdateText.setText("");
			}
		});
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});
	}
	//enable start button when u can
	private void updateStartButton(){
		//valid file not yet
		if(fileName.getText().toString().isEmpty()){
			return;
		}
		if(slider.getValue()==1){
				if(!team1NameBox.getText().trim().isEmpty()){
					start.setEnabled(true);
					return;
				}
		}
		else if(slider.getValue()==2){
			if(!team1NameBox.getText().trim().isEmpty() && !team2NameBox.getText().trim().isEmpty() ){
				start.setEnabled(true);
				return;
			}
		}
		else if(slider.getValue()==3){
			if(!team1NameBox.getText().trim().isEmpty() && !team2NameBox.getText().trim().isEmpty()
										 				&& !team3NameBox.getText().trim().isEmpty()){
				start.setEnabled(true);
				return;
			}			
		}
		else if(slider.getValue()==4){
			if(!team1NameBox.getText().trim().isEmpty() && !team2NameBox.getText().trim().isEmpty()
	 													&& !team3NameBox.getText().trim().isEmpty() 
	 													&& !team4NameBox.getText().trim().isEmpty()){
				start.setEnabled(true);
				return;
			}
		}
		start.setEnabled(false);
	}
	//when radio butto is host game
	private void updateStartButtonHost(){
		
		if(fileName.getText().toString().isEmpty()){
			start.setEnabled(false);
			return;
		}
		if(portTextBox.getText().toString().isEmpty() || portTextBox.getText().toString().equals("port")){
			start.setEnabled(false);
			return;
		}
		if(team1NameBox.getText().toString().isEmpty()){
			start.setEnabled(false);
			return;
		}
		start.setEnabled(true);
	}
	//when radio button is join game
	private void  updateStartButtonJoin(){
		
		if(portTextBox.getText().toString().isEmpty() || portTextBox.getText().toString().equals("port")){
			start.setEnabled(false);
			return;
		}
		if(team1NameBox.getText().toString().isEmpty() ){
			start.setEnabled(false);
			return;
		}
		if(ipAddressTextBox.getText().toString().isEmpty() || ipAddressTextBox.getText().toString().equals("IP Address")){
			start.setEnabled(false);
			return;
		}
		start.setEnabled(true);
	}
	
	private class ghostTextListener implements FocusListener{

		String str = null;
		JTextField textF;
		
		public ghostTextListener(String str, JTextField textF){
			this.str = str;
			this.textF = textF; 
		} //make ghosttext disappear
		public void focusGained(FocusEvent e) {
			if(textF.getText().equalsIgnoreCase(str)){
				textF.setText("");
				textF.setForeground(Color.BLACK);
			}
		} //reset ghosttext
		public void focusLost(FocusEvent e) {
			if(textF.getText().isEmpty()){
				textF.setForeground(Color.gray);
				textF.setText(str);
			}
		}
	}
	
	//a4
	public void buttonsWhileWaiting(boolean bool){
		team1NameBox.setEditable(bool);
		slider.setEnabled(bool);
		portTextBox.setEditable(bool);
		ipAddressTextBox.setEditable(bool);
		fileButton.setEnabled(bool);
		start.setEnabled(bool);
		clear.setEnabled(bool);
		notNetworkedButton.setEnabled(bool);
		hostGameButton.setEnabled(bool);
		joinGameButton.setEnabled(bool);
		quickPlay.setEnabled(bool);
	}
	public void setWaitingText(String str){
		waitingUpdateText.setText(str);
	}
	public GameData getGameData(){
		return gameData;
	}
	
	
}
