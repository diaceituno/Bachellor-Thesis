package viewControl;

import core.MainController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class HubControl {
	
	private Stage stage;
	private Scene scene;
	
	@FXML
	ComboBox<String> branchBox;
	@FXML
	Button configBtn, editorBtn, formBtn, groupBtn;

	public HubControl(Stage primStage) {
		
		//Loading Scene
		AnchorPane pane = (AnchorPane) MainController.getIOControl().loadPane("hubView.fxml", this);
		scene = new Scene(pane);
		
		//Setting up Stage
		this.stage = primStage;
		stage.setResizable(false);
		stage.setTitle("MIQR FMS");
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> {
			System.exit(0);
		});
		
		//Setting up Branches
		MainController.getDBControl().connect();
		String[] branches = MainController.getDBControl().queryBranches();
		MainController.getDBControl().close();
		
		branchBox.setItems(FXCollections.observableArrayList(branches));
		branchBox.getSelectionModel().selectFirst();
		MainController.updateBranch(branchBox.getSelectionModel().getSelectedItem());
		
		branchBox.valueProperty().addListener((ob,ov,nv)->{
			MainController.updateBranch(branchBox.getSelectionModel().getSelectedItem());
		});
		
		/*Setting up Tooltips*/
		Tooltip.install(configBtn, new Tooltip("LDAP und MYSQL Konfigurationen"));
	}
	
	public void show() {
		stage.show();
	}
	
	@FXML
	private void configAction() {
		MainController.configScene();
	}
	
	@FXML
	private void editorAction() {
		MainController.editorScene();
	}
	
	@FXML
	private void formAction() {
		System.out.println("form");
	}
	
	@FXML
	private void groupAction() {
		MainController.groupScene();
	}
	
	
}
