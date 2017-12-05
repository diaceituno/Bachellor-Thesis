package generators;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Line;

public class FXMLGenerator {

	public String genFXML(ObservableList<Node> children) {
		
		String header = "<?import javafx.scene.Group?>\n"
					  + "<?import javafx.scene.control.TextArea?>"
					  + "<?import javafx.scene.shape.Line?>\n";
		String fxml = header + "<Group>\n<children>\n";
		for(Node lvlOne : children) {
			System.out.println(lvlOne.getClass() + "|" + lvlOne.getId());
			if(lvlOne.getClass().equals(TextArea.class)) {
				fxml+=genTextArea(lvlOne);
			}else if(lvlOne.getId().equals("t")) {
				fxml+=genTableGroup(lvlOne);
			}else {
				fxml+=genInputGroup(lvlOne);
			}
			
		}
		fxml+="</children>\n</Group>\n";
		
		return fxml;
	}
	
	private String genTextArea(Node node) {
		
		TextArea area = (TextArea) node;
		String retString = "<TextArea prefHeight=\"" + area.getPrefHeight();
		retString+="\" id=\"" + area.getId()
				 + "\" prefWidth=\"" + area.getPrefWidth()
				 + "\" layoutX=\"" + area.getLayoutX() 
				 + "\" layoutY=\"" + area.getLayoutY() 
				 + "\" promptText=\"" + area.getPromptText()
				 + "\" text=\"" + area.getText() + "\"/>\n";
		
		return retString;
	}
	
	private String genLine(Node node ) {
		Line line = (Line) node;
		String retString="<Line startX=\"" + line.getStartX()
						+"\" endX=\"" + line.getEndX() 
						+"\" startY=\"" + line.getStartY() 
						+"\" endY=\"" + line.getEndY() + "\" />";
		return retString;
	}
	
	private String genInputGroup(Node node) {
		
		Group group = (Group) node;
		ObservableList<Node> children = group.getChildren();
		String retString="<Group id=\""+ group.getId() +"\">\n<children>\n";
		for(Node cNode : children) {
			retString+=genTextArea(cNode);
		}
		retString+="</children>\n</Group>\n";
		return retString;
	}
	
	private String genTableGroup(Node node) {
		
		Group group = (Group) node;
		ObservableList<Node> children = group.getChildren();
		String retString="<Group id=\""+ group.getId() +"\">\n<children>\n";;
		for(Node cNode: children) {
			if(cNode.getId().equals("a")) {
				
				ObservableList<Node> aChildren = ((Group) cNode).getChildren();
				retString+="<Group id=\""+ cNode.getId() + "\">\n<children>\n";
				for(Node aNode : aChildren) {
					
					retString+=genTextArea(aNode);
				}
				retString+="</children>\n</Group>\n";
				
			}else{
				
				retString+="<Group id=\""+ cNode.getId() + "\">\n<children>\n";
				ObservableList<Node> vChildren = ((Group) cNode).getChildren();
				for(Node lNode : vChildren) {
					retString+=genLine(lNode);
				}	
				retString+="</children>\n</Group>\n";
				
			}
		}
		retString+="</children>\n</Group>\n";
		return retString;
	}
	
}

	
