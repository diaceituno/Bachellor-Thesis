package viewControl;

import core.Configuration;
import core.MainController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ConfigurationControl {

	private Stage stage;
	private Scene scene;
	
	/*Control Booleans*/
	boolean ldapSFill;
	boolean ldapPFill;
	boolean mySQLSFill;
	boolean mySQLPFill;
	boolean mySQLUFill;
	boolean enableSave;
	
	/*Bindings*/
	@FXML
	Button anwendenBtn;
	@FXML
	TextField ldapServer;
	@FXML
	TextField ldapPort;
	@FXML
	TextField mySQLServer;
	@FXML
	TextField mySQLPort;
	@FXML
	TextField mySQLUser;
	@FXML
	TextField mySQLPass;

	public ConfigurationControl() {
		
		/*Setting up Scene*/
		AnchorPane pane = (AnchorPane) MainController.getIOControl().loadPane("configView.fxml", this);
		scene = new Scene(pane);
		scene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				anwendenAction();
			}
		});
		
		/*Setting up Stage*/
		stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("Konfiguration");
		stage.setScene(scene);
		
		/*Setting up TextField filters*/
		ldapPort.setOnKeyTyped(e -> {
			if(!e.getCharacter().matches("[0-9]")) {
				e.consume();
			}
		});
		
		mySQLPort.setOnKeyTyped(e -> {
			if(!e.getCharacter().matches("[0-9]")) {
				e.consume(); 
			}
		});
		
		/*Setting up ChangeListeners*/
		ldapServer.textProperty().addListener((ob,ov,nv) ->{
			ldapSFill = !nv.trim().isEmpty();
			enable();
		});
		
		ldapPort.textProperty().addListener((ob,ov,nv)->{
			ldapPFill = !nv.trim().isEmpty();
			enable();
		});
		
		mySQLServer.textProperty().addListener((ob,ov,nv) ->{
			mySQLSFill = !nv.trim().isEmpty();
			enable();
		});
		
		mySQLPort.textProperty().addListener((ob,ov,nv)->{
			mySQLPFill = !nv.trim().isEmpty();
			enable();
		});
		
		mySQLUser.textProperty().addListener((ob,ov,nv)->{
			mySQLUFill = !nv.trim().isEmpty();
			enable();
		});
		
		Configuration config = MainController.getConfiguration();
		if(config.isFilled()) {
			ldapServer.setText(config.getLDAPServer());
			ldapPort.setText(String.valueOf(config.getLDAPPort()));
			mySQLServer.setText(config.getMySQLServer());
			mySQLPort.setText(String.valueOf(config.getMySQLPort()));
			mySQLUser.setText(config.getMySQLUser());
			mySQLPass.setText(config.getMySQLPassword());
		}
	}
	
	public void show() {
		stage.showAndWait();
	}
	
	@FXML
	public void anwendenAction() {
		
		if(enableSave) {

			//Saving Configuration to MainControllers Configuration Instance
			Configuration config = MainController.getConfiguration();
			config.setLDAPServer(ldapServer.getText());
			config.setLDAPPort(Integer.parseInt(ldapPort.getText()));
			config.setMySQLServer(mySQLServer.getText());
			config.setMySQLPort(Integer.parseInt(mySQLPort.getText()));
			config.setMySQLUser(mySQLUser.getText());
			config.setMySQLPw(mySQLPass.getText());
			
			//Saving Configuration to Conf file
			String configurationString = "LDAPServer=" + ldapServer.getText() + ";" + 
					 "LDAPPort=" + ldapPort.getText() + ";" + 
					 "MySQLServer=" + mySQLServer.getText() + ";" + 
					 "MySQLPort=" + mySQLPort.getText() + ";" + 
					 "MySQLUser=" + mySQLUser.getText() + ";" + 
					 "MySQLPass=" + mySQLPass.getText();

			MainController.getIOControl().saveEncrypted("conf", configurationString);
			MainController.getDBControl().genURL();
			/*Alerting Success*/
			if(!MainController.getLoginStatus()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText(null);
				alert.setContentText("Danke! Bite melden sie sich an");
				alert.showAndWait();
			}
			stage.close();
		}
	}

	private void enable() {
		enableSave = ldapSFill && ldapPFill && mySQLSFill && mySQLPFill && mySQLUFill;
		if(enableSave) {
			anwendenBtn.setDisable(false);
		}else {
			anwendenBtn.setDisable(true);
		}
	}
}
