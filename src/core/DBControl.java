package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBControl {
	
	private String dbURL;
	private Connection connection;
	
	public DBControl() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			MainController.failure(null, true);
		}
	}
	
	/*Updated Configuration & connection methods*/
	public void genURL() {
		Configuration config = MainController.getConfiguration();
		dbURL = "jdbc:mysql://"+ config.getMySQLServer() + ":" + 
				config.getMySQLPort() +"/eval-test";
	}
	
	public boolean connect() {
		Configuration config = MainController.getConfiguration();
		try {
			connection = DriverManager.getConnection(dbURL,config.getMySQLUser(),config.getMySQLPassword());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean close() {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/*Querying Methods*/
	public String[] queryBranches(){
		
		String query = "select branchName from branches";
		Statement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	
		if(result != null) {
			
			ArrayList<String> retList = new ArrayList<String>();
			try {
				while(result.next()) {
					
					retList.add(result.getString("branchName"));
				}
				
				return retList.toArray(new String[retList.size()]);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String[] queryAllGroups() {
		return null;
	}
	
	public String[] queryGroupsInBranch(String branch) {
	
		String query = "select groupName from groups where branchName='" + branch + "'";
		Statement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(result != null) {
		
			ArrayList<String> retList = new ArrayList<String>();
			try {
				while(result.next()) {
					
					retList.add(result.getString("groupName"));
				}
				return retList.toArray(new String[retList.size()]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String[] queryPollsInBranch(String branch) {
		
		String query = "select pollName from polls where branchName='" + branch + "'";
		Statement statement = null;
		ResultSet result = null;
		try {
			statement = connection.createStatement();
			result = statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(result != null) {
			
			ArrayList<String> retList = new ArrayList<String>();
			try {
				while(result.next()) {
					retList.add(result.getString("pollName"));
				}
				
				return retList.toArray(new String[retList.size()]);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public String[] queryPollsInGroups(String branch, String[] groups) {
		
		String base = "select pollName from groupspolls where branchName='" + branch + "' ";
		Statement statement = null;
		ResultSet result =  null;
		ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
		
		for(String group : groups) {
			
			String query = base + " and groupname='" + group + "'";	
			ArrayList<String> groupList = new ArrayList<String>();
			try {
				statement = connection.createStatement();
				result = statement.executeQuery(query);
				while(result.next()) {
					
					groupList.add(result.getString("pollName"));
				}
				results.add(groupList);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ArrayList<String> returnList = results.get(0);
		for(int i=1;i<results.size();i++) {
			returnList.retainAll(results.get(i));
		}
		
		return returnList.toArray(new String[returnList.size()]);
	}

	public String queryPageinPoll(String branch, String poll, int page) {
		
		String query = "select fxml from pollpages where branch='" + branch + "' and poll='" + poll + "' and number=" + page + ";";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery(query);
			while(result.next()) {
				return result.getString("fxml");
			}
		} catch (SQLException e) {
			//  catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String[] queryPagesInPoll(String branch, String poll) {
		
		String query = "select fxml from polls where branchName='" + branch + "' and pollName='" + poll + "'";		
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			String wholeFXML = null;
			while(rs.next()) {
				wholeFXML = rs.getString("fxml");
			}
			
			String header = "<?import javafx.scene.Group?>\n"
			  + "<?import javafx.scene.control.TextArea?>"
			  + "<?import javafx.scene.shape.Line?>\n";
			
			
			String startRegex = "<page>";
			String endRegex = "</page>";
			String restFXML = wholeFXML;
			ArrayList<String> retPages = new ArrayList<String>();
			while(restFXML.contains("<page>")) {
				
				String page = header + restFXML.substring(restFXML.indexOf(startRegex) + 6, restFXML.indexOf(endRegex));
				retPages.add(page);
				restFXML = restFXML.substring(restFXML.indexOf(endRegex) + 7);
			}
			return retPages.toArray(new String[retPages.size()]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/*Saving Methods*/
	public boolean saveGroup(String branch, String group) {
		
		String query = "insert into groups values('" + branch + "','" + group + "','')";
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean savePollinGroup(String branch, String group, String poll) {
		
		String query = "insert into groupspolls values('" + branch + "','" + group + "','" + poll + "')";
		System.out.println(query);
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public boolean savePageinPoll(String branch, String poll, int number, String fxml) {
		
		String query = "insert into pollpages values('" + branch + "','" + poll + "'," + number + ",'" + fxml +"')";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean setGroupPath(String branch, String group, String path) {
		
		String query = "update groups set ldapPath='" + path + "' where branchName='" + branch + "' and groupName='" + group + "';";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean saveUsersInGroup(String branch, String group, String[] users) {
		
		String query = "replace into users values ";
		for(String user: users) {
			query+= "('" + branch + "','" + group + "','" + user + "'),";
		}
		query = query.substring(0,query.length() - 1);
		query+=";";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean savePollinBranch(String branch, String poll, String fxml) {
		
		String query = "insert into polls values('" + branch + "','" + poll + "','" + fxml + "')";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	/*Deleting Methods*/
	public boolean deleteFromGroups(String branch, String[] groups, String poll) {
		
		String query = "delete from groupspolls where poll='" + poll + "' and (groupname='";
		Statement statement =  null;
		query+=groups[0] + "'";
		for(int i=1;i<groups.length;i++) {
			query+=" or groupname='" + groups[i] + "'";
		}
		query+=")";
		System.out.println(query);
		try {
			statement = connection.createStatement();
			return statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean deletepollFromBranch(String branch, String poll) {
		
		String queryForm = "delete from polls where branchName ='" + branch + "' and pollName='" + poll + "'";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			return statement.execute(queryForm);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
