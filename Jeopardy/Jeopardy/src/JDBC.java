import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBC {
	
	private Connection conn = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement statement = null;
	
	private String username, password;
	boolean buttonPressed; //false for log in , true for create,
	
	final boolean LOGIN = false;
	final boolean CREATE = true;
	
	boolean validUser;
	boolean newUser;
	
	public JDBC(){
		validUser = false;
		newUser = false;
	}
	//consturctors with parameters
	public void runJDBC(String userName, String passWord, boolean buttonPressed){
		this.username = userName; 
		this.password = passWord;
		this.buttonPressed = buttonPressed;
		
		//connect to sql database
		try{
			//connect
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/Users?user=root&password=root&useSSL=false");

			//when login is pressed
			if(buttonPressed == LOGIN){
				if(validUser()){
					validUser = true;
				}
				else{
					validUser=  false;
				}
			} //when create accoutn is pressed
			else if(buttonPressed == CREATE){
				if(userExist()){
					newUser = false;
				}
				else{
					newUser = true;
					writeToDataBase();
				}
			}
		} catch(SQLException sqle){
			System.out.println("sqle: " + sqle.getMessage());
		} catch(ClassNotFoundException cnfe){
			System.out.println("cnfe:" + cnfe.getMessage());
		} finally{
			try{
				if(statement!=null){ statement.close(); }
				if(rs != null){ rs.close();  }
				if(st != null){ st.close();  }
				if(conn!=null){ conn.close();}
			} catch(SQLException sqle){
				System.out.println("sqle: "  + sqle.getMessage());
			}
		}
	}
	public void writeToDataBase() throws SQLException{
		//write a new user
		statement = (PreparedStatement) conn.prepareStatement("INSERT INTO UserInfo(user_name,pass_word) VALUES(?,?)");
		statement.setString(1, username);
		statement.setString(2, password);
		statement.executeUpdate();
	}
	public boolean userExist() throws SQLException{
		st = conn.createStatement();
		rs = st.executeQuery("SELECT user_name FROM UserInfo WHERE user_name='" + username + "';");
		if(!rs.next()){ 	//user does not exist
			return false;
		}
		else{ 				//user exists
			return true;
		}
	}
	public boolean validUser() throws SQLException{
		st = conn.createStatement();
		rs = st.executeQuery("SELECT user_name, pass_word FROM UserInfo WHERE user_name='" + username + "' AND pass_word='" + password + "';");
	
		if(!rs.next()){ 	//invalid user
			return false;
		}
		else{ 				//valid user
			return true;
		}
	}
	//methods to access from login window
	public boolean isValidUser(){
		return validUser;
	}
	public boolean isNewUser(){
		return newUser;
	}
}
