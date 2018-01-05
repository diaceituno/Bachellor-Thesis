package viewControl;

import core.LDAPControl;
import core.MainController;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginControl{

	private Stage stage;
	private Scene scene;
	
	@FXML
	private Button loginBtn;
	@FXML
	private TextField usrField;
	@FXML
	private PasswordField pwdField;
	
	public LoginControl(){
			
		/*Setting up Scene*/
		AnchorPane pane = (AnchorPane) MainController.getIOControl().loadPane("loginView.fxml", this);
		scene = new Scene(pane);
		scene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ENTER) {
				loginAction();
			}
			if(e.getCode() == KeyCode.CONTROL) {
				loginSuccess();
			}
		});
		
		/*Setting up stage*/
		stage = new Stage();
		stage.setTitle("Anmeldung");
		stage.setResizable(false);
		stage.setScene(scene);
		
		/*Setting changeListenes for TextField*/
		usrField.textProperty().addListener((ob,ov,nv) -> {
			if(!nv.trim().isEmpty()) {
				loginBtn.setDisable(false);
			}else {
				loginBtn.setDisable(true);
			}
		});
	}
	
	@FXML 
	private void loginAction() {
		
		LDAPControl ldapControl = MainController.getLDAPControl();
		ldapControl.updateVals(usrField.getText(), pwdField.getText());
		
		if(ldapControl.connect()) {
			
			if(ldapControl.bind()) {
				loginSuccess();
			}else {
				loginFail();
			}
		}else {
			connectionFailure();
		}
	}
	
	private void loginFail() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.initOwner(stage);
		alert.setTitle("Anmeldung fehlgeschlagen");
		alert.setHeaderText(null);
		alert.setContentText("Anmeldung fehlgeschlagen!\nBenutzername oder Passwort falsch");
		alert.showAndWait();
	}
	
	private void loginSuccess() {
		MainController.setLoginStatus(true);
		stage.close();
	}
	
	public void show() {
		stage.showAndWait();
	}
	
	public void connectionFailure() {
		
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(null);
		alert.setTitle("Fehler");
		alert.setContentText("Verbindung mit der AD konnte nicht aufgebaut werden");
		alert.showAndWait();
		MainController.failure(null, true);
	}

}
 