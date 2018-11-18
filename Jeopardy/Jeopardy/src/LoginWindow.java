

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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LoginWindow extends JFrame {
	
	JTextField welcomeMessage, welcomeMessage2;
	JTextField alertLabel;
	JTextField usernameText, passwordText;
	JButton loginButton, createButton;
	
	JPanel mainLayout;
	
	final String usernameGhostText = "username";
	final String passwordGhostText = "password";
	
	final String ExistingAlertLabel = "this username already exists";
	final String NonExistingAlertLabel = "this password and username combination does not exist";
	
	ArrayList<User> userList;

	private static final long serialVersionUID = 1L;
	
	//a4
	private JDBC jdbc;
	private final boolean LOGIN = false;
	private final boolean CREATE = true;
	

	

	public LoginWindow() {
		super("Login Window");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initializeComponents();
		createGUI();
		addEvents();
		userList = new ArrayList<>();
		//a4
		jdbc = new JDBC();
		
	}
	private void initializeComponents() {
		

		
		
		welcomeMessage = new JTextField("login or create an account to play");
		welcomeMessage2 = new JTextField("Jeopardy!");
		welcomeMessage.setEditable(false);
		welcomeMessage2.setEditable(false);
		
		alertLabel = new JTextField();
		alertLabel.setEditable(false);
		
		usernameText = new JTextField(usernameGhostText);
		passwordText = new JTextField(passwordGhostText);
		
		loginButton = new JButton("Login"); 
		createButton = new JButton("Create Account");

		loginButton.setEnabled(false);
		createButton.setEnabled(false);
	
		mainLayout = new JPanel();
		mainLayout.setLayout(new GridLayout(6, 1, 5, 5));
		mainLayout.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);	
		
		mainLayout.setMinimumSize(mainLayout.getPreferredSize());
		
	}
	private void createGUI() {
		setSize(600, 600);
		//setSize(300,300); => you can use this to test the reduced size!
		setLocationRelativeTo(null);
		//remove borderline 
		welcomeMessage.setBorder(null);
		welcomeMessage2.setBorder(null);
		alertLabel.setBorder(null);
		//set font for each label
		welcomeMessage.setFont(new Font("Times New Roman", Font.BOLD, 30));
		welcomeMessage2.setFont(new Font("Times New Roman", Font.BOLD, 33));
		alertLabel.setFont(new Font("Times New Roman", Font.PLAIN, 19));
		usernameText.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		passwordText.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		loginButton.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		createButton.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		//set color for ghost text and buttons
		usernameText.setForeground(Color.gray);
		passwordText.setForeground(Color.gray);
		loginButton.setBackground(Color.darkGray);
		createButton.setBackground(Color.darkGray);
		loginButton.setForeground(Color.WHITE);
		createButton.setForeground(Color.WHITE);
		//center the texts
		welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeMessage2.setHorizontalAlignment(SwingConstants.CENTER);
		alertLabel.setHorizontalAlignment(SwingConstants.CENTER);
		//create margine for buttons and put them into a box
		loginButton.setMargin(new Insets(15, 10, 15, 10));
		createButton.setMargin(new Insets(15, 10, 15, 10));
		JPanel buttonLayout = new JPanel();
		buttonLayout.setLayout(new BoxLayout(buttonLayout, BoxLayout.X_AXIS));
		buttonLayout.add(loginButton);
		buttonLayout.add(Box.createRigidArea(new Dimension(10,0)));
		buttonLayout.add(createButton);
		//add all components to main layout
		mainLayout.add(welcomeMessage);
		mainLayout.add(welcomeMessage2);
		mainLayout.add(alertLabel);
		mainLayout.add(usernameText);
		mainLayout.add(passwordText);
		//use gridlayout to organize componenets
		JPanel mainWindow = new JPanel();
		mainWindow.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,0,0);
		gbc.gridx = 0; gbc.gridy = 0; mainWindow.add(mainLayout, gbc);
		gbc.gridx = 0; gbc.gridy = 1; mainWindow.add(buttonLayout, gbc);
		//set colors for background
		welcomeMessage.setBackground(new Color(153,204,255));
		welcomeMessage2.setBackground(new Color(153,204,255));
		alertLabel.setBackground(new Color(153,204,255));
		mainLayout.setBackground(new Color(153,204,255));
		buttonLayout.setBackground(new Color(153,204,255));
		mainWindow.setBackground(new Color(153,204,255));
		
		//resizing
		mainLayout.setMinimumSize(new Dimension(300,215));
		buttonLayout.setMinimumSize(new Dimension(300,30));
		
		//add it to the main panel
		add(mainWindow, BorderLayout.CENTER);
		
	}
	private void addEvents() {
		
		
		
		//document listener for login and create buttons
		usernameText.getDocument().addDocumentListener(new DocumentListener() {
	        public void changedUpdate(DocumentEvent e) {
	        	updateButton();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	updateButton();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	updateButton();
	        }
	        
	    });
		passwordText.getDocument().addDocumentListener(new DocumentListener() {
	        public void changedUpdate(DocumentEvent e) {
	        	updateButton();
	        }
	        public void removeUpdate(DocumentEvent e) {
	        	updateButton();
	        }
	        public void insertUpdate(DocumentEvent e) {
	        	updateButton();
	        }
	    });
		//make ghost text visible/invisible
		ghostTextListener gtl = new ghostTextListener(usernameGhostText, usernameText);
		ghostTextListener gtl2 = new ghostTextListener(passwordGhostText, passwordText);
		usernameText.addFocusListener(gtl);
		passwordText.addFocusListener(gtl2);
		//login button clicks
		createButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				
				//a4
				//check if exists and either create or error message
				String username = usernameText.getText().toString();
				String password = passwordText.getText().toString();
				jdbc.runJDBC(username, password, CREATE);
				//USER ALREADY EXISTS
				if(jdbc.isNewUser()){
					restoreDefault();
			    	StartWindow sw = new StartWindow(username);
			    	sw.setVisible(true);
			    	setVisible(false);
				}
				else{
					alertLabel.setText(ExistingAlertLabel);
				}
			}
		});
		loginButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				
				//a4
				String username = usernameText.getText().toString();
				String password = passwordText.getText().toString();
				jdbc.runJDBC(username, password, LOGIN);
				if(jdbc.isValidUser()){
			    	restoreDefault();
			    	StartWindow sw = new StartWindow(username);
			    	sw.setVisible(true);
			    	dispose();
			    	//setVisible(false);
				}
				else{
					alertLabel.setText(NonExistingAlertLabel);
				}
			}
		});
	}
	//enable buttons if username and password is correctly typed
	public void updateButton(){
		if(!usernameText.getText().equalsIgnoreCase(usernameGhostText) &&
			!passwordText.getText().equalsIgnoreCase(passwordGhostText)){
				if(!usernameText.getText().isEmpty() &&
					!passwordText.getText().isEmpty()){
						loginButton.setEnabled(true);
						createButton.setEnabled(true);
						return;
					}
			}
		loginButton.setEnabled(false);
		createButton.setEnabled(false);
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
	private void restoreDefault(){
		usernameText.setText(usernameGhostText);
		passwordText.setText(passwordGhostText);
		usernameText.setForeground(Color.gray);
		passwordText.setForeground(Color.gray);
		alertLabel.setText("");
		loginButton.setEnabled(false);
		createButton.setEnabled(false);
	}
}