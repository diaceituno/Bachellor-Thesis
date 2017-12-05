package viewControl;





import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import core.DBControl;
import core.MainController;
import generators.FXMLGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EditorControl {
	
	private Stage stage;
	private Scene scene;
	private Stage secStage;
	private Scene lScene;
	private Scene sScene;
	private Scene tblScene;
	
	private AnchorPane page;
	private AnchorPane bgPane;
	
	//tools
	private int iCount = 0;
	private final int TXT_TOOL = 1;
	private final int IN_TOOL = 2;
	private final int TBL_TOOL = 3;
	private int tool;
	private boolean selection = false;
	
	/*Pages*/
	private ArrayList<Group> pages;
	private int currentPage;
	
	/*FXML Bindings Main*/
	@FXML
	MenuBar menuBar;
	@FXML
	Slider zoomSlider;
	@FXML
	Group zoomGroup;
	@FXML
	ToggleButton txtBtn,uiBtn,tblBtn;
	@FXML
	TextField sizeField;
	@FXML
	Label pageLbl;
	@FXML
	Group pageGroup;
	
	
	/*FXML Bindings open*/
	@FXML
	private TableView<String> tViewPolls;
	@FXML
	private TextField pollSearchField;
	private FilteredList<String> fPollList;
	
	/*FXML Bindings save*/
	@FXML
	private TableView<String> tViewSPolls;
	@FXML
	private TextField pollSSearchField;
	private FilteredList<String> fPollSList;
	
	/*FXML Bindings table*/
	@FXML
	private TextField rowsField;
	@FXML
	private TextField colsField;
	
 	public EditorControl() {
		
		//Generating Containers
		AnchorPane root = new AnchorPane();
		AnchorPane toolPane = (AnchorPane) MainController.getIOControl().loadPane("toolPane.fxml", this);
		ToggleGroup toggleGroup = new ToggleGroup();
		ScrollPane scrollPane = new ScrollPane();
		Group group = new Group();
		bgPane = new AnchorPane();
		page = new AnchorPane();
		stage = new Stage();
		secStage = new Stage();
		pages = new ArrayList<Group>();
		fPollList = new FilteredList<String>(FXCollections.observableArrayList(), p -> true);
		fPollSList = new FilteredList<String>(FXCollections.observableArrayList(), p -> true);
		
		//Adding Containers
		txtBtn.setToggleGroup(toggleGroup);
		uiBtn.setToggleGroup(toggleGroup);
		tblBtn.setToggleGroup(toggleGroup);
		bgPane.getChildren().add(page);
		group.getChildren().add(bgPane);
		scrollPane.setContent(group);
		root.getChildren().addAll(scrollPane,toolPane);
		scene = new Scene(root);
		stage.setScene(scene);
		
		//Styling Containers
		page.setStyle("-fx-background-color: #FFFFFF;");
		page.setEffect(new DropShadow());
		bgPane.setStyle("-fx-background-color: #F0F0F0;");
		stage.setTitle("Formeditor");
		secStage.initModality(Modality.WINDOW_MODAL);
		secStage.initOwner(stage);
		
		//Dimensioning
		stage.setMaximized(true);
		double pageWidth = 600;
		double pageHeight = 800;
		double minBgWidth = pageWidth + 50;
		double minBgHeight = pageHeight + 50;
		
		page.setPrefHeight(pageHeight);
		page.setPrefWidth(pageWidth);
		bgPane.setMinWidth(minBgWidth);
		bgPane.setMinHeight(minBgHeight);
		
		//Adding Listeners
		scrollPane.setLayoutY(toolPane.getPrefHeight());
		scene.widthProperty().addListener((ob,ov,nv)->{
			double sceneWidth = (double) nv;
			menuBar.setPrefWidth(sceneWidth);
			bgPane.setPrefWidth(sceneWidth);
			scrollPane.setPrefWidth(sceneWidth);
			if(sceneWidth > 600) {
				zoomGroup.setLayoutX(sceneWidth - 220);
				pageGroup.setLayoutX(sceneWidth - 350);
			}
			if(sceneWidth > minBgWidth) {
				page.setLayoutX((sceneWidth - page.getPrefWidth())/2);
			}else {
				page.setLayoutX((minBgWidth - page.getPrefWidth())/2);
			}
		});
		
		scene.heightProperty().addListener((ob,ov,nv)->{
			double scrollHeight = (double) nv - toolPane.getPrefHeight();
			bgPane.setPrefHeight(scrollHeight); 
			scrollPane.setPrefHeight(scrollHeight);
			if(scrollHeight > minBgHeight) {
				page.setLayoutY((scrollHeight - page.getHeight())/2);
			}else {
				page.setLayoutY((minBgHeight - page.getPrefHeight())/2);
			}
		});
		
		scene.focusOwnerProperty().addListener((ob,ov,nv)->{
			if(nv.getClass().equals(TextArea.class)) {
				System.out.println(nv.getId());
			}
		});
		
		scene.setOnKeyPressed(e -> {
			if(e.getCode() == KeyCode.ESCAPE) {
				if(toggleGroup.getSelectedToggle() != null) {
					toggleGroup.getSelectedToggle().setSelected(false);
				}
				selection = false;
				page.setCursor(Cursor.DEFAULT);
			}
		});
		
		scene.setOnMouseClicked(e->{
			if(selection) {
				ToggleButton selectedBtn = (ToggleButton)toggleGroup.getSelectedToggle();
				String selectedString = selectedBtn.getText();
				selectedBtn.setSelected(false);
				System.out.println(selectedString);
			}
		});
		
		toggleGroup.selectedToggleProperty().addListener((ob,ov,nv)->{
			if(nv != null) {
				selection = true;
				if(nv.equals(txtBtn)) {
					tool = TXT_TOOL;
				}else if(nv.equals(tblBtn)) {
					tool = TBL_TOOL;
					showTableView();
				}else {
					tool = IN_TOOL;
				}
				page.setCursor(Cursor.CROSSHAIR);
			}else {
				selection = false;
				page.setCursor(Cursor.DEFAULT);
			}
		});
	
		page.setOnMouseClicked(e -> {
			
			if(selection) {
				
				double x = e.getX();
				double y = e.getY();
				
				switch(tool) {
				case 1:
					pages.get(currentPage).getChildren().add(genTxt(x,y));
					break;
				case 2:
					pages.get(currentPage).getChildren().add(genIGroup(x,y));
					break;
				case 3:
					String rows = rowsField.getText();
					String cols = colsField.getText();
					if(!(rows.isEmpty() && cols.isEmpty())) {
						int col = Integer.parseInt(cols);
						int row = Integer.parseInt(rows);
						pages.get(currentPage).getChildren().add(genTblGroup(row,col,x,y));
					}
					
					break;
				}
			}
		});
		
		currentPage = 0;
		newPageAction();
		
		
		//zoom
		zoomSlider.setMin(1);
		zoomSlider.setMax(3);
		zoomSlider.setValue(1);
		zoomSlider.valueProperty().addListener((ob,ov,nv)->{
			bgPane.setScaleX((double) nv);
			bgPane.setScaleY((double) nv);
		});
	}

	@FXML
	private void zoomAction() {
		if(bgPane.getScaleX() < 3) {
			bgPane.setScaleX(bgPane.getScaleX() + 0.1);
			bgPane.setScaleY(bgPane.getScaleY() + 0.1);
			zoomSlider.setValue(bgPane.getScaleX());
		}
	}
	
	@FXML
	private void unzoomAction() {
		if(bgPane.getScaleX() > 1) {
			bgPane.setScaleX(bgPane.getScaleX() - 0.1);
			bgPane.setScaleY(bgPane.getScaleY() - 0.1);
			zoomSlider.setValue(bgPane.getScaleX());
		}
	}
	
	@FXML
	private void textAction() {
		selection = true;
		page.setCursor(Cursor.CROSSHAIR);
	}
	
	public void show() {
		
		if(!stage.isShowing()) {
			stage.showAndWait();
		}	
	}
	
	public void updateBranch() {
		
		DBControl dbControl = MainController.getDBControl();
		dbControl.connect();
		ObservableList<String> data = FXCollections.observableArrayList(dbControl.queryPollsInBranch(MainController.getBranch()));
		dbControl.close(); 
		
		if(lScene != null) {
			
			@SuppressWarnings("unchecked")
			ObservableList<String> lSource = (ObservableList<String>) fPollList.getSource();
	 
			lSource.clear();
			lSource.addAll(data);
			
			tViewPolls.getColumns().get(0).setText("Evaluationsformen " + MainController.getBranch());
		}
		if(sScene != null) {
			@SuppressWarnings("unchecked")
			ObservableList<String> sSource = (ObservableList<String>) fPollSList.getSource();
			sSource.clear();
			sSource.addAll(data);
			tViewSPolls.getColumns().get(0).setText("Evaluationsformen " + MainController.getBranch());
		}
	}
	
	private void showTableView() {
		
		if(tblScene == null) {
			
			AnchorPane pane = (AnchorPane) MainController.getIOControl().loadPane("tableView.fxml", this);
			tblScene = new Scene(pane);
			tblScene.setOnKeyPressed(e -> {
				if(e.getCode() == KeyCode.ENTER) {
					tableOK();
				}
			});
			rowsField.setOnKeyTyped(e -> {
				if(!e.getCharacter().matches("[0-9]")) {
					e.consume();
				}
			});
			colsField.setOnKeyTyped(e->{
				if(!e.getCharacter().matches("[0-9]")) {
					e.consume();
				}
			});
		}
		secStage.setScene(tblScene);
		secStage.showAndWait();
	}
	
	/*Generator Methods*/
	private Group genIGroup(double x, double y) {
		iCount++;
		Group retGroup = new Group();
		TextArea tArea = genTxt(x,y);
		TextArea iArea = genTxt(x + tArea.getPrefWidth(), y);
		tArea.setPromptText("EINGABE " + iCount);
		iArea.setText("EINGABE " + iCount);
		iArea.setEditable(false);
		retGroup.getChildren().addAll(tArea,iArea);
		retGroup.setId("i");
		return retGroup;
	}
	
	private TextArea genTxt(double locX, double locY) {
		
		TextArea area = new TextArea();
		area.setLayoutX(locX);
		area.setLayoutY(locY);
		area.setPrefWidth(100);
		area.setPrefHeight(75);
		area.setWrapText(true);
		
		addTxtHandlers(area);
			
		return area;
	}

	private void addTxtHandlers(TextArea area) {
			
		area.setOnMouseMoved(e->{
			
			double x = e.getX();
			double y = e.getY();
			double width = area.getPrefWidth() - 3;
			double height = area.getPrefHeight() - 3;
			
			if((x < 3)&&(y < 3)) {
				area.setCursor(Cursor.MOVE);
			}else if((x > width) && (y > height)){
				area.setCursor(Cursor.SE_RESIZE);
			}else {
				area.setCursor(Cursor.DEFAULT);
			}
		});
		
		area.setOnMouseDragged(e -> {
			
			if(area.getCursor() == Cursor.MOVE) {
				
				double moveMinx = e.getX() + area.getLayoutX();
				double moveMaxx = e.getX() + area.getLayoutX() + area.getPrefWidth();
				double moveMiny = e.getY() + area.getLayoutY();
				double moveMaxy = e.getY() + area.getLayoutY() + area.getPrefHeight();
				
				if((moveMinx > 0)&&(moveMaxx < page.getPrefWidth())) {
					area.setLayoutX(area.getLayoutX() + e.getX());
				}
				if((moveMiny > 0)&&(moveMaxy < page.getPrefHeight())) {
					area.setLayoutY(area.getLayoutY() + e.getY());
				}
			}
			if(area.getCursor() == Cursor.SE_RESIZE) {
				
				double resMaxX = e.getX() + area.getLayoutX();
				double resMaxY = e.getY() + area.getLayoutY();
				
				if(resMaxX < page.getPrefWidth()) {
					area.setPrefWidth(e.getX());
				}
				if(resMaxY < page.getPrefHeight()) {
					area.setPrefHeight(e.getY());
				}
			}
		
		});
	}
	
	private Group genTblGroup(int rows, int cols, double x, double y) {
		//fix dis shit boi index test before adding the listener u idiot
		
		Group retGroup = new Group();
		Group verticals = new Group();
		Group horizontals = new Group();
		Group areas = new Group();
		
		double pageWidth = page.getPrefWidth() - 30;
		double remHeight = page.getPrefHeight() - y - 15;
		
		double colWidth = pageWidth/cols;
		double rowHeight = remHeight/rows;
		if(rowHeight > 75) {
			rowHeight = 75;
		}
		
		for(int col = 0; col < cols; col++) {
			TextArea area = new TextArea();
			area.setText(String.valueOf(col));
			area.setWrapText(true);
			area.setPrefWidth(colWidth);
			area.setPrefHeight(rowHeight);
			area.setLayoutX(15 + col*colWidth);
			area.setLayoutY(y);
			areas.getChildren().add(area);
		}
		((TextArea) areas.getChildren().get(0)).setContextMenu(null);
		
		for(int row = 1; row < rows; row++) {
			TextArea area = new TextArea();
			area.setWrapText(true);
			area.setText(String.valueOf(cols + row));
			area.setPrefWidth(colWidth);
			area.setPrefHeight(rowHeight);
			area.setLayoutX(15);
			area.setLayoutY(y + row*rowHeight);
			areas.getChildren().add(area);
		}
		
		for(int col = 0; col < cols + 1; col++) {
			Line line = new Line();
			line.setStartX(15 + col*colWidth);
			line.setEndX(15 + col*colWidth);
			line.setStartY(y);
			line.setEndY(y + rowHeight*rows);
			line.setCursor(Cursor.H_RESIZE);
			verticals.getChildren().add(line);
		}
		
		for(int row = 0; row < rows + 1; row++) {
		
			Line line = new Line();
			line.setStartX(15);
			line.setEndX(page.getWidth() - 15);
			line.setStartY(y + row*rowHeight);
			line.setEndY(y + row*rowHeight);
			line.setCursor(Cursor.V_RESIZE);
			horizontals.getChildren().add(line);		
		}
		
		addTblHandlers(areas,verticals,horizontals);
	
		//cleaning
		areas.setId("a");
		verticals.setId("v");
		horizontals.setId("h");
		retGroup.getChildren().addAll(areas,verticals,horizontals);
		retGroup.setId("t");
		return retGroup;
	}
	
	public void addTblHandlers(Group areas,Group verticals, Group horizontals) {
		
		int rows = horizontals.getChildren().size() - 1;
		int cols = verticals.getChildren().size() - 1;
		
		for(int i=1;i<cols;i++) {
			TextArea area = (TextArea) areas.getChildren().get(i);
			/*Context Menu*/
			ContextMenu cMenu = new ContextMenu();
			MenuItem itemLabel = new MenuItem("Note hinzufügen");
			itemLabel.setDisable(true);
			itemLabel.setStyle("-fx-opacity: 2.0;");
			MenuItem noteLabel = new MenuItem();
			if(area.getId() == null) {
				noteLabel.setText("Keine Note hinzugefügt");
			}else {
				noteLabel.setText("Note: " + area.getId());
			}
			noteLabel.setDisable(true);
			noteLabel.setStyle("-fx-opacity: 2.0;");
			MenuItem item1 = new MenuItem("1");
			item1.setOnAction(e -> {
				area.setId("1");
				cMenu.getItems().get(1).setText("Note: 1");
			});
			MenuItem item2 = new MenuItem("2");
			item2.setOnAction(e -> {
				area.setId("2");
				cMenu.getItems().get(1).setText("Note: 2");
			});
			MenuItem item3 = new MenuItem("3");
			item3.setOnAction(e -> {
				area.setId("3");
				cMenu.getItems().get(1).setText("Note: 3");
			});
			MenuItem item4 = new MenuItem("4");
			item4.setOnAction(e->{
				area.setId("4");
				cMenu.getItems().get(1).setText("Note: 4");
			});
			MenuItem item5 = new MenuItem("5");
			item5.setOnAction(e->{
				area.setId("5");
				cMenu.getItems().get(1).setText("Note: 5");
			});
			
			cMenu.getItems().addAll(itemLabel, noteLabel,item1, item2, item3, item4, item5);
			area.setContextMenu(cMenu);
		}
		((TextArea) areas.getChildren().get(0)).setContextMenu(null);
		
		
		
		for(int i=0;i<rows + 1;i++) {
			Line line = (Line) horizontals.getChildren().get(i);
			line.setCursor(Cursor.V_RESIZE);
			if((i > 1)&&( i < rows)) {
				
				Line aboveL = (Line) horizontals.getChildren().get(i - 1);
				Line underL = (Line) horizontals.getChildren().get(i + 1);
				TextArea tAbove = (TextArea) areas.getChildren().get(cols + i - 2);
				TextArea tUnder = (TextArea) areas.getChildren().get(cols + i - 1);
				
				line.setOnMouseDragged(e -> {
					double upper = aboveL.getStartY();
					double under = underL.getStartY();
					if((e.getY() > upper + 50) &&(e.getY() < under - 50)) {
						
						line.setStartY(e.getY());
						line.setEndY(e.getY());
						
						tAbove.setPrefHeight(e.getY() - tAbove.getLayoutY());
						tUnder.setLayoutY(e.getY());
						tUnder.setPrefHeight(under - e.getY());
					}
				});
			}else {
				if(i == 0) {
					
					line.setOnMouseDragged(e -> {
						
						if((e.getY() > 0 )&&(e.getY() < page.getPrefHeight() - 15)) {
							
							double start = line.getStartY();
							double end = ((Line) verticals.getChildren().get(0)).getEndY();
							double oSpace = end - start;
							double nSpace = end - e.getY();
							if(nSpace > 50*rows) {
								for(Node node : horizontals.getChildren()) {
									
									Line cLine = (Line) node;
									double ratio = (cLine.getStartY() - start)/oSpace;
									double newY = e.getY() + ratio*nSpace;
									cLine.setStartY(newY);
									cLine.setEndY(newY);
								}
								
								for(Node node : verticals.getChildren()){
									
									((Line) node).setStartY(e.getY());
								}
								
								for(Node node : areas.getChildren()) {
									
									TextArea cArea = (TextArea) node;
									double lRatio = (cArea.getLayoutY() - start)/oSpace;
									double hRatio = cArea.getPrefHeight()/oSpace;
									double newY = e.getY() + lRatio*nSpace;
									cArea.setLayoutY(newY);
									cArea.setPrefHeight(hRatio*nSpace);
								}
							}
						}
						
					});
				}
				if(i == 1) {
					Line aboveL = (Line) horizontals.getChildren().get(0);
					Line underL = (Line) horizontals.getChildren().get(2);
					line.setOnMouseDragged(e -> {
						
						double upper = aboveL.getStartY();
						double under = underL.getStartY();
						
						if((e.getY() > upper + 50)&&(e.getY() < under - 50)) {
							line.setStartY(e.getY());
							line.setEndY(e.getY());
							for(int tA = 0; tA < cols; tA++) {
								TextArea area = (TextArea) areas.getChildren().get(tA);
								area.setPrefHeight(e.getY() - area.getLayoutY());
							}
							TextArea area = (TextArea) areas.getChildren().get(cols);
							area.setLayoutY(e.getY());
							area.setPrefHeight(under - e.getY());
						}
					});
				}
				if(i == rows) {
					line.setOnMouseDragged(e ->{
						if((e.getY() > 0 )&&(e.getY() < page.getPrefHeight() - 15)) {
							
							double start = ((Line) verticals.getChildren().get(0)).getStartY();
							double end = line.getStartY();
							double oSpace = end - start;
							double nSpace = e.getY() - start;
							if(nSpace > 50*rows) {
								for(Node node : horizontals.getChildren()) {
									
									Line cLine = (Line) node;
									double ratio = (cLine.getStartY() - start)/oSpace;
									double newY = start + ratio*nSpace;
									cLine.setStartY(newY);
									cLine.setEndY(newY);
								}
								
								for(Node node : verticals.getChildren()){
									
									((Line) node).setEndY(e.getY());
								}
								
								for(Node node : areas.getChildren()) {
									
									TextArea cArea = (TextArea) node;
									double lRatio = (cArea.getLayoutY() - start)/oSpace;
									double hRatio = cArea.getPrefHeight()/oSpace;
									double newY = start + lRatio*nSpace;
									cArea.setLayoutY(newY);
									cArea.setPrefHeight(hRatio*nSpace);
								}
							}
						}
					});
				}
			}
		}
		
		for(int i=0; i < cols + 1; i++ ) {
			Line line = (Line) verticals.getChildren().get(i);
			line.setCursor(Cursor.H_RESIZE);
			if((i > 1)&&(i<cols)) {

				TextArea leftArea = (TextArea) areas.getChildren().get(i - 1);
				TextArea rightArea = (TextArea) areas.getChildren().get(i);
				double before = ((Line) verticals.getChildren().get(i - 1)).getStartX();
				double after = ((Line) verticals.getChildren().get(i + 1)).getStartX();
				line.setOnMouseDragged(e -> {
					
					if((e.getX() > before + 50)&&(e.getX() < after - 50)) {
						
						line.setStartX(e.getX());
						line.setEndX(e.getX());
						
						leftArea.setPrefWidth(e.getX() - before);
						rightArea.setPrefWidth(after - e.getX());
						rightArea.setLayoutX(e.getX());
					}
				});
			}else {
				if(i == 0) {
					line.setOnMouseDragged(e -> {
						
						if(e.getX() > 0 ){
							
							double last = ((Line) horizontals.getChildren().get(0)).getEndX();
							double first = ((Line) horizontals.getChildren().get(0)).getStartX();
							double oSpace = last - first;
							double nSpace = last - e.getX();
							if(nSpace > 50*cols) {
								ObservableList<Node> vChildren = verticals.getChildren();
								for(Node cNode : vChildren) {
									 
									Line cLine = (Line) cNode; 
									double ratio = (cLine.getStartX() - first)/oSpace;
									cLine.setStartX(e.getX() + nSpace * ratio);
									cLine.setEndX(e.getX() + nSpace * ratio);
								}
								
								ObservableList<Node> hChildren = horizontals.getChildren();
								for(Node cNode : hChildren) {
									((Line) cNode).setStartX(e.getX());
								}
								
								ObservableList<Node> aChildren = areas.getChildren();
								for(Node cNode : aChildren) {
								
									TextArea cArea = (TextArea) cNode;
									double lRatio = (cArea.getLayoutX() - first)/oSpace;
									double wRatio = cArea.getPrefWidth()/oSpace;
									cArea.setLayoutX(e.getX() + nSpace*lRatio);
									cArea.setPrefWidth(nSpace*wRatio);
								}
							}
							
						}
					});
				}
				else if(i == 1) {
					line.setOnMouseDragged(e -> {
						
						double before = ((Line) verticals.getChildren().get(0)).getStartX();
						double after = ((Line) verticals.getChildren().get(2)).getStartX();
						if((e.getX() > before + 50) && (e.getX() < after - 50)) {
							line.setStartX(e.getX());
							line.setEndX(e.getX());
							((TextArea) areas.getChildren().get(0)).setPrefWidth(e.getX() - before);
							((TextArea) areas.getChildren().get(1)).setPrefWidth(after - e.getX());
							((TextArea) areas.getChildren().get(1)).setLayoutX(e.getX());
							
							ObservableList<Node> aChildren = areas.getChildren();
							for(int ai = cols; ai < cols + rows -1; ai++) {
								
								TextArea cArea = (TextArea) aChildren.get(ai);
								cArea.setPrefWidth(e.getX() - before);
							}
						}
					});	
				}else {
					
					line.setOnMouseDragged(e -> {
						
						if(e.getX() < page.getPrefWidth()) {
							double last = ((Line) horizontals.getChildren().get(0)).getEndX();
							double first = ((Line) horizontals.getChildren().get(0)).getStartX();
							double oSpace = last - first;
							double nSpace = e.getX() - first;
							if(nSpace > 50*cols) {
								ObservableList<Node> vChildren = verticals.getChildren();
								for(Node cNode : vChildren) {
									 
									Line cLine = (Line) cNode; 
									double ratio = (cLine.getStartX() - first)/oSpace;
									cLine.setStartX(first + nSpace * ratio);
									cLine.setEndX(first + nSpace * ratio);
								}
								
								ObservableList<Node> hChildren = horizontals.getChildren();
								for(Node cNode : hChildren) {
									((Line) cNode).setEndX(e.getX());
								}
								
								ObservableList<Node> aChildren = areas.getChildren();
								for(Node cNode : aChildren) {
								
									TextArea cArea = (TextArea) cNode;
									double lRatio = (cArea.getLayoutX() - first)/oSpace;
									double wRatio = cArea.getPrefWidth()/oSpace;
									cArea.setLayoutX(first + nSpace*lRatio);
									cArea.setPrefWidth(nSpace*wRatio);
								}
							
							}
						}
					});
				}
			}
	}
	}

	/*Action Methods*/
	@FXML
	private void saveAction() {
		
		if(sScene == null) {
			AnchorPane pane = (AnchorPane) MainController.getIOControl().loadPane("saveView.fxml", this);
			sScene = new Scene(pane);
			
			//fPollSList = new FilteredList<String>(FXCollections.observableArrayList(), p -> true);
			TableColumn<String,String> tCol = new TableColumn<String,String>();
			tCol.setCellValueFactory(cD -> new SimpleStringProperty(cD.getValue()));
			tCol.setPrefWidth(tViewSPolls.getPrefWidth());
			tCol.setResizable(false);
			tCol.setText("EvaluationsFormen " + MainController.getBranch());
			tViewSPolls.getColumns().add(tCol);
			tViewSPolls.setItems(fPollSList);
		
			tViewSPolls.getSelectionModel().selectedItemProperty().addListener((ob,ov,nv)->{
				if(nv != null) {
					pollSSearchField.setText(nv);
				}
			});
			pollSSearchField.textProperty().addListener((ob,ov,nv) -> {
				
				fPollSList.setPredicate( poll -> {
					poll = poll.toLowerCase();
					if(nv.isEmpty()) {
						return true;
					}
					if(poll.contains(nv.toLowerCase())) {
						return true;
					}
					return false;
				});
			});
			
		}
		secStage.setScene(sScene);
		updateBranch();
		secStage.showAndWait();
	}
	
	@FXML
	private void loadAction() {
		
		if(lScene == null) {
			AnchorPane pane = (AnchorPane) MainController.getIOControl().loadPane("loadView.fxml", this);
			lScene = new Scene(pane);
			
			
			TableColumn<String,String> tCol = new TableColumn<String,String>();
			tCol.setCellValueFactory(cD -> new SimpleStringProperty(cD.getValue()));
			tCol.setPrefWidth(tViewPolls.getPrefWidth());
			tCol.setResizable(false);
			tCol.setText("EvaluationsFormen " + MainController.getBranch());
			tViewPolls.getColumns().add(tCol);
			tViewPolls.setItems(fPollList);
			
			pollSearchField.textProperty().addListener((ob,ov,nv) -> {
				
				fPollList.setPredicate( poll -> {
					poll = poll.toLowerCase();
					if(nv.isEmpty()) {
						return true;
					}
					if(poll.contains(nv.toLowerCase())) {
						return true;
					}
					return false;
				});
			});
			
		}
		
		secStage.setScene(lScene);
		secStage.setTitle("öffnen");
		updateBranch();
		secStage.showAndWait();
	}
	
	@FXML 
	private void newAction() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Vorsicht!");
		alert.setHeaderText(null);
		alert.setContentText("Wenn sie OK drücken werden alle ungespeicherte Daten verloren\n");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			pages.clear();
			currentPage = -1;
			page.getChildren().clear();
			newPageAction();	
		}	
	}
	
	@FXML
	private void load() {
		
		String selection = tViewPolls.getSelectionModel().getSelectedItem();
		if(selection != null) {
			DBControl db = MainController.getDBControl();
			db.connect();
			String[] fxmls = db.queryPagesInPoll(MainController.getBranch(), selection);
			pages.clear();
			System.out.println(fxmls.length);
			db.close();
			
			for(String fxml : fxmls) {

				Group root = null;
				try {
					root = (Group) new FXMLLoader().load(new ByteArrayInputStream(fxml.getBytes("UTF-8")));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(root != null) {
					ObservableList<Node> rootChildren = root.getChildren();
					for(Node lvlOne : rootChildren) {
						
						if(lvlOne.getClass().equals(Group.class)) {
						
							ObservableList<Node> oneChildren = ((Group) lvlOne).getChildren();

							if(lvlOne.getId().equals("t")) {
								
								Group verticals = null;
								Group horizontals = null;
								Group areas = null;
								for(Node groups : oneChildren) {
								
									if(groups.getId().equals("a")) {
										areas = (Group) groups;
									}else if(groups.getId().equals("v")) {
										verticals = (Group) groups;
									}else {
										horizontals = (Group) groups;
									}
								}
								
								addTblHandlers(areas,verticals,horizontals);
							}else {
								
								for(Node areas : oneChildren) {
									addTxtHandlers((TextArea) areas);
								}
							}
						}else {
							addTxtHandlers((TextArea) lvlOne);
						}
					}
					pages.add(root);
				}	
			}
			
			currentPage = -1;
			nextPageAction();
			secStage.close();
		}
	}
	
	@FXML
	private void save() {
		
		String pollName = pollSSearchField.getText();
		if(!pollName.isEmpty()) {
		
			DBControl dbControl = MainController.getDBControl();
			dbControl.connect();
			if(!dbControl.savePollinBranch(MainController.getBranch(), pollName)) {
				FXMLGenerator generator = new FXMLGenerator();
				for(int i=0;i<pages.size();i++) {
					
					ObservableList<Node> children = pages.get(i).getChildren();
					String fxml = generator.genFXML(children);
					dbControl.savePageinPoll(MainController.getBranch(), pollName, i, fxml);
				}
			}else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Vorsicht");
				alert.setHeaderText(null);
				alert.setContentText("Wollen sie die Form wirklich überschreiben?");
				Optional<ButtonType> result = alert.showAndWait();
				if(result.get() == ButtonType.OK) {
					dbControl.deletepollFromBranch(MainController.getBranch(), pollName);
					save();
				}
			}
			dbControl.close();
		}
		secStage.close();
	}
	
	@FXML
	private void newPageAction() {
		
		Group newGroup = new Group();
		pages.add(newGroup);
		page.getChildren().add(newGroup);
		page.getChildren().clear();
		currentPage = pages.size() - 1;
		page.getChildren().add(pages.get(currentPage));
		updatePageLabel();
	}
	
	@FXML
	private void prevPageAction() {
		if(currentPage > 0) {
			currentPage--;
			page.getChildren().clear();
			page.getChildren().add(pages.get(currentPage));
			updatePageLabel();
		}
	}
	
	@FXML
	private void nextPageAction() {
		if(currentPage < pages.size() - 1) {
			currentPage++;
			page.getChildren().clear();
			page.getChildren().add(pages.get(currentPage));
			updatePageLabel();
		}
	}
	
	private void updatePageLabel() {
		pageLbl.setText((currentPage + 1) + "/" + pages.size());
	}
	
	@FXML
	private void removePageAction() {
		pages.remove(pages.get(currentPage));
		if(currentPage > 0) {
			currentPage --;
		}else {
			currentPage++;
		}
		page.getChildren().clear();
		if(pages.isEmpty()) {
			currentPage = 0;
			pages.add(new Group());
			page.getChildren().add(pages.get(currentPage));
		}
		updatePageLabel();
	}
	
	@FXML
	private void duplicatePageAction() {
		
	}
	
	@FXML
	private void tableOK(){
		secStage.close();
	}
}
