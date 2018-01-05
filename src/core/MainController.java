package core;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import viewControl.ConfigurationControl;
import viewControl.EditorControl;
import viewControl.GroupControl;
import viewControl.HubControl;
import viewControl.LoginControl;

public class MainController extends Application{

	private Stage mainStage;
	private static IOControl ioControl;
	private static DBControl dbControl;
	private static LDAPControl ldapControl;
	private static Configuration configuration;
	
	/*Scenes*/
	private LoginControl loginControl;
	private static HubControl hubControl; 
	private static EditorControl editorControl;
	private static ConfigurationControl configControl;
	private static GroupControl groupControl;
	
	/** Control variables **/
	private static boolean loginStatus;
	private static String branch; 
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage mainStage) throws Exception {
		this.mainStage = mainStage;
		initialize();
	}
	
	private void initialize() {
		
		//call welcome shizzle method threads and shit
		try {
			MainController.ioControl = new IOControl("Ressources/","Config/");
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
			MainController.failure("", true);
		}
		
		MainController.configuration = new Configuration();
		MainController.dbControl = new DBControl();
		if(!MainController.configuration.loadConfiguration("conf", ";")) {
			
			String message = "Es w�rde keine Konfigurationsdatei gefunden!\n" +
							 "Bitte LDAP und MySQL Konfiguration eingeben";
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.setTitle("Konfigurationsfehler");
			alert.showAndWait();
			configScene();
		}
		
		MainController.dbControl.genURL();
		ldapControl = new LDAPControl();
		loginScene();
		if(loginStatus) {
			hubScene();
		}
	}
	
	/*Scene Setter Methods*/
	private void loginScene() {
		if(loginControl == null) {
			
			loginControl = new LoginControl();
		}
		loginControl.show();
	}

	private void hubScene() {
		
		if(hubControl == null) {
			System.out.println("asdf");
			hubControl = new HubControl(mainStage);
		}
		hubControl.show();
	}
	
	public static void editorScene() {
		
		if(editorControl == null) {
			
			editorControl = new EditorControl();
		}
		editorControl.show();
	}
	
	public static void configScene() {
		
		if(configControl == null) {
			
			configControl = new ConfigurationControl();
		}
		configControl.show();
	}
	
	public static void formScene() {
		System.out.println("Form bby");
	}
	
	public static void groupScene() {
		
		if(groupControl == null) {
			
			groupControl = new GroupControl();
		}
		groupControl.show();
	}
	
	/* Static Core Methods */
	public static void failure(String message, boolean exit) {
		if(!exit) { //change later
			System.exit(0);
		}
	}
	
	public static void setLoginStatus(boolean loginStatus) {
		MainController.loginStatus = loginStatus;
	}

	public static boolean getLoginStatus() {
		return MainController.loginStatus;
	}
	
	public static String getBranch() {
		return MainController.branch;
	}
	
	public static DBControl getDBControl() {
		return dbControl;
	}
	
	public static IOControl getIOControl() {
		return ioControl;
	}
	
	public static Configuration getConfiguration() {
		return configuration;
	}

	public static LDAPControl getLDAPControl() {
		return ldapControl;
	}
	
	public static void updateBranch(String branch) {
		MainController.branch = branch;
		if(groupControl != null) {
			groupControl.updateBranch();
		}
		if(editorControl != null){
			editorControl.updateBranch();
		}
	}

}
