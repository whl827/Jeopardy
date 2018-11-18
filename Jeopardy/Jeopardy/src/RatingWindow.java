

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RatingWindow extends JFrame{

	private static final long serialVersionUID = 2L;
	
	private int totalPeopleRated;
	private int totalRate;
	
	private	ArrayList<String> outFileArray;
	private String fileDestination;
	
	private JLabel firstText, secondText, thirdText, forthText;
	private JSlider ratingSlider;
	private JLabel sliderText;
	private JButton ratingOkayButton;

	private String title, title2, currentRating;
	
	
	//a4
	private boolean network;
	private int totalRatingsNetwork; //total number of all rating from all players during network;
	private boolean host;
	private PlayerServer player;
	private String teamName;
	private MainWindow mw;
	
	public RatingWindow(String title, String title2, String currentRating, 
						int totalRate, int totalPeopleRated, ArrayList<String> outFileArray, String fileDestination){
		super("Rating Window");
		this.title = title;
		this.title2 = title2;
		if(totalPeopleRated == -1 || totalRate == -1){
			this.currentRating = "0";
		}
		else{
			this.currentRating = currentRating;
		}
		this.totalPeopleRated = totalPeopleRated;
		this.totalRate = totalRate;
		this.outFileArray = outFileArray;
		this.fileDestination = fileDestination;
		
		network = false;
		totalRatingsNetwork = 0;
		host = false;
		player = null;

		
		initializeGUI();
		createGUI();
		addEvents();
	
	}
	private void initializeGUI() {
		
		firstText = new JLabel(title);
		secondText = new JLabel(title2);
		thirdText = new JLabel("Please rate this game file one a scale from 1 to 5");
		
		
		if(totalPeopleRated == -1 || totalRate == -1){
			forthText = new JLabel("no rating");
		}
		else{
			forthText = new JLabel("current average rating: " + currentRating + "/5");
		}
		
		ratingOkayButton = new JButton("Okay");
		ratingSlider = new JSlider(1,5);
		ratingSlider.setPaintTicks(true);
		ratingSlider.setPaintLabels(true);
	    sliderText = new JLabel("3");
	    
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	private void createGUI() {
		setSize(600,600);
		//setSize(300,300); => you can use this to test the reduced size!
		setLocationRelativeTo(null);
		firstText.setBackground(new Color(153,204,255)); firstText.setOpaque(true);
		secondText.setBackground(Color.darkGray); secondText.setOpaque(true);
		secondText.setForeground(Color.WHITE);
		thirdText.setBackground(new Color(153,204,255)); thirdText.setOpaque(true);
		ratingSlider.setBackground(new Color(153,204,255));
		sliderText.setBackground(Color.darkGray); sliderText.setOpaque(true);
		sliderText.setForeground(Color.WHITE);
		forthText.setBackground(new Color(153,204,255)); forthText.setOpaque(true);
		ratingOkayButton.setBackground(Color.darkGray); ratingOkayButton.setOpaque(true);
		ratingOkayButton.setForeground(Color.WHITE);
		
		firstText.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		secondText.setFont(new Font("Times New Roman", Font.PLAIN, 35));
		thirdText.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		forthText.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		sliderText.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		ratingSlider.setFont(new Font("Times New Roman", Font.BOLD, 17));
		ratingOkayButton.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		
		ratingSlider.setMajorTickSpacing(1);
		sliderText.setBorder(new EmptyBorder(25,20,25,20));
		ratingOkayButton.setBorder(new EmptyBorder(20,10,20,10));
		ratingOkayButton.setEnabled(false);
		
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
		sliderPanel.setBackground(new Color(153,204,255));
		sliderPanel.add(ratingSlider);
		sliderPanel.add(Box.createRigidArea(new Dimension(30,0)));
		sliderPanel.add(sliderText);
		
		firstText.setHorizontalAlignment(SwingConstants.CENTER);
		secondText.setHorizontalAlignment(SwingConstants.CENTER);
		forthText.setHorizontalAlignment(SwingConstants.CENTER);
		thirdText.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 1, 3, 3));
		mainPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		mainPanel.setBackground(Color.BLUE);
		
		mainPanel.add(firstText);
		mainPanel.add(secondText);
		mainPanel.add(thirdText);
		mainPanel.add(sliderPanel);
		mainPanel.add(forthText);
		
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(1, 3, 0, 0));
		
		
		JLabel emptyLabel = new JLabel();
		JLabel emptyLabel2 = new JLabel();
		emptyLabel.setBackground(new Color(153,204,255)); emptyLabel.setOpaque(true);
		emptyLabel2.setBackground(new Color(153,204,255)); emptyLabel2.setOpaque(true);
		
		buttons.add(emptyLabel);
		buttons.add(ratingOkayButton);
		buttons.add(emptyLabel2);
		
		mainPanel.add(buttons);
		
		mainPanel.setBackground(new Color(153,204,255));
		add(mainPanel, BorderLayout.CENTER);
		

	}
	private void addEvents() {
		//overw
		ratingOkayButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if(network){
					//write to file, set visible to false and open start window
					//host only write file
					writeToFile(); // write to file
					player.closeSocket(); // close this team's socket
					dispose();
					mw.dispose();
					StartWindow sw = new StartWindow(teamName);
					sw.setVisible(true);
				}
				else{
					writeToFile();
					//close the window
					setVisible(false);
				}
			}
		});
		//change text as slider change
		ratingSlider.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				int currentUserRate = ratingSlider.getValue();
				sliderText.setText(String.valueOf(currentUserRate));
				ratingOkayButton.setEnabled(true);
			}
		});	
	}
	public boolean isNetwork() {
		return network;
	}
	public void setNetwork(boolean network) {
		this.network = network;
	}
	public boolean isHost() {
		return host;
	}
	public void setHost(boolean host) {
		this.host = host;
	}
	public PlayerServer getPlayer() {
		return player;
	}
	public void setPlayer(PlayerServer player) {
		this.player = player;
	}
	public void writeToFile(){
		//first time rating
		if(currentRating.equals("0")){
			totalRate = ratingSlider.getValue();
			totalPeopleRated = 1;
		}
		//already have previous ratings
		else{
			totalRate = totalRate + ratingSlider.getValue();
			totalPeopleRated++;
		}
		//start overwriting the file
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			File file = new File(fileDestination);
			if(!file.exists()){
				file.createNewFile();
			}
			fw = new FileWriter(file.getAbsolutePath());
			bw = new BufferedWriter(fw);
			
			for(int i=0; i<outFileArray.size(); i++){
				bw.write(outFileArray.get(i));
				bw.write("\n");
				
			}
			bw.write(String.valueOf(totalPeopleRated));
			bw.write("\n");
			bw.write(String.valueOf(totalRate));
			bw.flush();

		} catch(IOException e){
			
		}
		finally{
			try{
				if(fw!=null){
					fw.close();
				}
				if(bw!=null){
					bw.close();
				}
			} catch(IOException e){
		
			}
		}
	}
	public String getTeamName() {
		return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public int getTotalRatingsNetwork(){
		return totalRatingsNetwork;
	}
	public void setTotalRatingsNetwork(int x){
		totalRatingsNetwork = x;
	}
	public MainWindow getMainWindow(){
		return mw;
	}
	public void setMainWindow(MainWindow mw){
		this.mw = mw;
	}

}