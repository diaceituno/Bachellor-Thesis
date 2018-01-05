package core;

import java.util.ArrayList;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.SimpleBindRequest;

public class LDAPControl {

	private String ldapUname;
	private String ldapPwd;
	private String ldapHost;
	private int ldapPort;
	private LDAPConnection ldapCon;
	
	public LDAPControl() {
		LDAPConnectionOptions opts = new LDAPConnectionOptions();
		opts.setConnectTimeoutMillis(5000);
		ldapCon = new LDAPConnection();
		ldapCon.setConnectionOptions(opts);  
	}
	
	public void updateVals(String uname, String pw) {
		
		Configuration config = MainController.getConfiguration();
		ldapUname = uname;
		ldapPwd = pw;
		ldapHost = config.getLDAPServer();
		ldapPort = config.getLDAPPort();
	}
	
	public boolean connect() {
		try {
			ldapCon.connect(ldapHost, ldapPort);
			return ldapCon.isConnected();
		} catch (LDAPException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean bind() {
		
		SimpleBindRequest bR = new SimpleBindRequest(ldapUname + "@miqr.local", ldapPwd);
		try {
			ldapCon.bind(bR);
			return true;
		} catch (LDAPException e) {
			
			e.printStackTrace();
		}
		return false;
	}
	
	public void close() {
		ldapCon.close();
	}
	
	public String[] searchInAD(String path, String attribute, String value, String returnAttribute) {

		
		Filter filter = Filter.createEqualityFilter(attribute, value);
		SearchRequest searchRequest = new SearchRequest(path,SearchScope.ONE, filter);
		SearchResult searchResult;
		ArrayList<String> returnList = new ArrayList<String>();
		try {
			searchResult = ldapCon.search(searchRequest);
			for(SearchResultEntry entry : searchResult.getSearchEntries()) {
				returnList.add(entry.getAttributeValue(returnAttribute));
			}
			return returnList.toArray(new String[returnList.size()]);
		} catch (LDAPSearchException e) {
			e.printStackTrace();
		}
		return null;
	}
	public void searchUsersInOU(String ouPath) {
		
		Filter filter = Filter.createEqualityFilter("objectCategory", "organizationalUnit");
		SearchRequest searchRequest = new SearchRequest("dc=miqr,dc=local",SearchScope.ONE, filter);
		SearchResult searchResult;
		
		try {
			searchResult = ldapCon.search(searchRequest);
			for(SearchResultEntry entry : searchResult.getSearchEntries()) {
				for(Attribute attr : entry.getAttributes()) {
					System.out.println(attr.getName() + " : " + entry.getAttributeValue(attr.getName()));	
				}
				System.out.println("-------------------------------------------------------------");
			}
		} catch (LDAPSearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
