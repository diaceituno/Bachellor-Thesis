package viewControl;

import java.util.Optional;

import core.DBControl;
import core.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GroupControl {

	private Stage stage;
	
	/*BRANCH*/
	/*Attributes for Groups in Branch*/
	private Scene listScene;
	private TableColumn<String,String> tColBList;	
	private FilteredList<String> fGroupList;
	
	/*FXML Bindings group in branch*/
	@FXML
	TableView<String> tViewBList;	//list of Groups in Branch
	@FXML
	TextField groupSearchField;
	
	/*GROUP*/
	/*Attributes for Forms in Group*/
	private Scene groupScene;
	private TableColumn<String,String> tColFList;
	private TableColumn<String,String> tColAFList;
	private FilteredList<String> fPollList;
	private boolean change;
	
	/*FXML Bindings form in group*/
	@FXML
	Label groupNameLabel;
	@FXML
	TableView<String> tViewPList;	// List of Forms in Group
	@FXML
	TableView<String> tViewAPList;	//List of all Forms
	@FXML
	TextField pollSearchField;
	@FXML
	Button pathButton;
	
	public GroupControl() {
		
		/*Setting up groups in Branch Scene*/
		AnchorPane pane = (AnchorPane) MainController.getIOControl().loadPane("groupView.fxml", this);
		listScene = new Scene(pane);
		pane = (AnchorPane) MainController.getIOControl().loadPane("groupFormView.fxml", this);
		groupScene = new Scene(pane);

		/*Setting up tableColumn*/
		tColBList = new TableColumn<String, String>(MainController.getBranch() + " Gruppen");
		tColBList.setCellValueFactory(cD -> new SimpleStringProperty(cD.getValue()));
		tColBList.setPrefWidth(tViewBList.getPrefWidth()-2);
		tColBList.setResizable(false);
		
		tViewBList.getColumns().add(tColBList);
		tViewBList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tViewBList.setRowFactory(tv->{
			TableRow<String> tr = new TableRow<>();
			tr.setOnMouseClicked(e -> {
				if(e.getClickCount() == 2) {
					bearbeitenAction();
				}
			});
			return tr;
		});
		
		/*Setting up Items*/
		fGroupList = new FilteredList<String>(FXCollections.observableArrayList(), p -> true);
		tViewBList.setItems(fGroupList);
		groupSearchField.textProperty().addListener((ob,ov,nv) -> {
				
				fGroupList.setPredicate( group -> {
					group = group.toLowerCase();
					if(nv.isEmpty()) {
						return true;
					}
					if(group.contains(nv.toLowerCase())) {
						return true;
					}
					return false;
				});
			});
		
		/*Setting up Forms in Group scene*/
		groupNameLabel.setAlignment(Pos.CENTER);
		tColFList = new TableColumn<String,String>("Zugeordnete Fragebogen");
		tColFList.setCellValueFactory(cD -> new SimpleStringProperty(cD.getValue()));
		tColFList.setPrefWidth(tViewPList.getPrefWidth() - 2);
		tColFList.setResizable(false);
		
		tColAFList = new TableColumn<String,String>("Alle Fragebogen");
		tColAFList.setCellValueFactory(cD -> new SimpleStringProperty(cD.getValue()));
		tColAFList.setPrefWidth(tViewAPList.getPrefWidth() -2);
		tColAFList.setResizable(false);
		
		tViewPList.getColumns().add(tColFList);
		tViewPList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tViewPList.setRowFactory(tv -> {
			TableRow<String> tr = new TableRow<String>();
			tr.setOnMouseClicked(e -> {
				if(e.getClickCount() == 2) {
					removePollAction();
				}
			});
			return tr;
		});
		tViewAPList.getColumns().add(tColAFList);
		tViewAPList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		tViewAPList.setRowFactory(tv -> {
			TableRow<String> tr = new TableRow<String>();
			tr.setOnMouseClicked(e -> {
				if(e.getClickCount() == 2) {
					addPollAction();
				}
			});
			return tr;
		});
		
		/*Setting up Items*/
		fPollList = new FilteredList<String>(FXCollections.observableArrayList(), p -> true);
		tViewAPList.setItems(fPollList);
		pollSearchField.textProperty().addListener((ob,ov,nv)->{
			fPollList.setPredicate(poll ->{
				poll =  poll.toLowerCase();
				if(nv.isEmpty()) {
					return true;
				}
				if(poll.contains(nv.toLowerCase())) {
					return true;
				}
				return false;
			});
		});
		
		/*Setting up Stage*/
		stage = new Stage();
		stage.setResizable(false);
		stage.setTitle("Gruppenverwaltung " + MainController.getBranch());
		updateBranch();
	} 
	
	@SuppressWarnings("unchecked")
	public void updateBranch() {
		
		/*resetting labels and selection models*/
		stage.setTitle("Gruppenverwaltung " + MainController.getBranch());
		tViewBList.getSelectionModel().clearSelection();
		tColBList.setText(MainController.getBranch() + " Gruppen");
		groupSearchField.setText("");
		
		/*Clearing List and getting new Branch items*/
		MainController.getDBControl().connect();
		ObservableList<String> groupList = FXCollections.observableArrayList(MainController.getDBControl().queryGroupsInBranch(MainController.getBranch())); 
		MainController.getDBControl().close();
		
		ObservableList<String> source = (ObservableList<String>) fGroupList.getSource();
		source.clear();
		source.addAll(groupList);
		stage.setScene(listScene);
		
	}
	
	public void show() {
		stage.showAndWait();
	}
	
	/*BRANCH*/
	@SuppressWarnings("unchecked")
	@FXML
	private void bearbeitenAction() {
		ObservableList<String> selected = tViewBList.getSelectionModel().getSelectedItems();
		if(selected.size() > 0) {
			
			if(selected.size() > 1) {
				pathButton.setVisible(false);
			}else {
				pathButton.setVisible(true);
			}
			
			/*Setting up Labels*/
			String nameLabel = selected.get(0);
			for(int i = 1; i < selected.size() ; i++) {
				nameLabel += ", " + selected.get(i);
			}
			
			/*Setting up items in Polls in Group Table*/
			String branch = MainController.getBranch();
			DBControl dbControl = MainController.getDBControl();
			dbControl.connect();
			
			String[] selectedArray = selected.toArray(new String[selected.size()]);
			String[] items = dbControl.queryPollsInGroups(branch, selectedArray);
			System.out.println(items.length);
			tViewPList.setItems(FXCollections.observableArrayList(items));
			
			/*Setting up items in All Polls Table*/
			ObservableList<String> pollList = FXCollections.observableArrayList(dbControl.queryPollsInBranch(branch));
			ObservableList<String> source = (ObservableList<String>) fPollList.getSource();
			source.clear();
			source.addAll(pollList);
			source.removeAll(items);
			
			dbControl.close();
			change = false;
			groupNameLabel.setText(nameLabel);
			stage.setScene(groupScene);
		}
	}
	
	@FXML
	public void erstellenAction() {
		Dialog<?> inputDialog = new TextInputDialog();
		inputDialog.initOwner(stage);
		inputDialog.setTitle("Gruppe erstellen");
		inputDialog.setHeaderText(null);
		Optional<?> result =  inputDialog.showAndWait();
		result.ifPresent(gN -> {
			String groupName = (String) gN;
			if(!groupName.isEmpty()) {
				MainController.getDBControl().connect();
				MainController.getDBControl().saveGroup(MainController.getBranch(), groupName);
				MainController.getDBControl().close();
				updateBranch();
			}
		});
	}

	@FXML
	public void fertigAction() {
		stage.close();
	}


	/*GROUP*/
	@FXML
	private void anwendenAction() {
		
		if(change) {
			ObservableList<String> groups = tViewBList.getSelectionModel().getSelectedItems();
			if(groups.size() > 0) {
				
				String branch = MainController.getBranch();
				DBControl dbControl = MainController.getDBControl();
				ObservableList<String> polls = tViewPList.getItems();
				dbControl.connect();
				
				for(String group : groups) {
					
					for(String poll : polls) {
						
						dbControl.savePollinGroup(branch, group, poll);
					}
					
				}
				dbControl.close();
			}
		}
		
		stage.setScene(listScene);
	}

	@FXML
	private void setPathAction() {
		TextInputDialog inputDialog = new TextInputDialog();
		inputDialog.setHeaderText(null);
		inputDialog.setContentText("AD Pfad Eingeben");
		inputDialog.showAndWait();
	}
	
	@FXML
	private void addPollAction() {
		
		ObservableList<String> selection = tViewAPList.getSelectionModel().getSelectedItems();
		if(selection.size() > 0) {
			
			ObservableList<String> source = tViewPList.getItems();
			for(String poll : selection) {
				if(!source.contains(poll)) {
					source.add(poll);
				}
			}
			fPollList.getSource().removeAll(selection);
		}
		change = true;
	}
	
	@SuppressWarnings("unchecked")
	@FXML 
	private void removePollAction() {
		
		String branch = MainController.getBranch();
		ObservableList<String> toRemove = tViewPList.getSelectionModel().getSelectedItems();
		ObservableList<String> groupsList = tViewBList.getSelectionModel().getSelectedItems();
		String[] groupsArray = groupsList.toArray(new String[groupsList.size()]);
		if(toRemove.size() > 0) {
			
			DBControl  dbControl = MainController.getDBControl();
			dbControl.connect();
			for(String poll : toRemove) {
				dbControl.deleteFromGroups(branch, groupsArray, poll);
			}
			dbControl.close();
		}
		((ObservableList<String>) fPollList.getSource()).addAll(toRemove);
		tViewPList.getItems().removeAll(toRemove);
		
	
	}
}
 