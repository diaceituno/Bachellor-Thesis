package core;

public class Configuration {

	private String ldapServer;
	private int ldapPort;
	
	private String mySQLServer;
	private int mySQLPort;
	private String mySQLUsername;
	private String mySQLPw;
	
	private boolean filled;
	
	public void setLDAPServer(String ldapServer) {
		this.ldapServer = ldapServer;
	}
	
	public void setLDAPPort(int ldapPort) {
		this.ldapPort = ldapPort;
	}
	
	public void setMySQLServer(String mySQLServer) {
		this.mySQLServer = mySQLServer;
	}
	
	public void setMySQLPort(int mySQLPort) {
		this.mySQLPort = mySQLPort;
	}
	
	public void setMySQLUser(String username) {
		this.mySQLUsername = username;
	}
	
	public void setMySQLPw(String pw) {
		this.mySQLPw = pw;
	}
	
	public String getLDAPServer() {
		return ldapServer;
	}
	
	public int getLDAPPort() {
		return ldapPort;
	}
	
	public String getMySQLServer() {
		return mySQLServer;
	}
	
	public int getMySQLPort() {
		return mySQLPort;
	}
	
	public String getMySQLUser() {
		return mySQLUsername;
	}
	
	public String getMySQLPassword() {
		return mySQLPw;
	}
	
	public boolean isFilled() {
		return filled;
	}
	
	public boolean loadConfiguration(String confFile, String regex) {
		
		String decoded = MainController.getIOControl().loadEncrypted(confFile);
		if(decoded == null) {
			filled = false;
			return false;
		}
		
		String[] keyValues = decoded.split(regex);
		for(String keyValue : keyValues) {

			String[] splitKV = keyValue.split("=");
			String key = splitKV[0];
			String value = splitKV[1];
			switch(key) {
		
				case "LDAPServer":
					setLDAPServer(value);
					break;
				case "LDAPPort":
					setLDAPPort(Integer.parseInt(value));
					break;
				case "MySQLServer":
					setMySQLServer(value);
					break;
				case "MySQLPort":
					setMySQLPort(Integer.parseInt(value));
					break;
				case "MySQLUser":
					setMySQLUser(value);
					break;
				case "MySQLPass":
					setMySQLPw(value);
					break;
			}	
		}
		filled = true;
		return true;
	}
}
