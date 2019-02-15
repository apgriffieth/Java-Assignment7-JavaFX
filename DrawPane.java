// Assignment #: Arizona State University CSE205 #7
//         Name: Alan Griffieth
//    StudentID: 1212575453
//      Lecture: MWF 9:40 - 10:30 a.m.
//  Description: The DrawPane class creates a canvas where we can use
//               mouse key to draw either a Rectangle or a Circle with different
//               colors. We can also use the the two buttons to erase the last
//				 drawn shape or clear them all.

//import any classes necessary here
//----
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

import java.util.ArrayList;

public class DrawPane extends BorderPane
{
	private Button undoBtn, eraseBtn;
	private ComboBox<String> colorCombo;
	private RadioButton rbRect, rbCircle;
	private ArrayList<Shape> shapeList;
	private Pane canvas;
	//declare any other necessary instance variables here
	//----
	private double x1, y1;
	private Circle c1;
	private Rectangle r1;
	private Color fillColor = Color.BLACK;
	//Constructor
	public DrawPane()
	{
		//Step #1: initialize each instance variable and set up layout
		undoBtn = new Button("Undo");
		eraseBtn = new Button("Erase");
		undoBtn.setMinWidth(80.0);
		eraseBtn.setMinWidth(80.0);
		x1 = y1 = 0;
		c1 = null;
		r1 = null;
		//Create the color comboBox and intial its default color
		//----
		colorCombo = new ComboBox<String>();
		colorCombo.getItems().addAll("Black", "Blue", "Green", "Red", "Yellow", "Orange", "Pink");
		colorCombo.setValue("Black");

		//Create the two radio buttons and also a ToggleGroup
		//so that the two radio buttons can be selected
		//mutually exclusively. Otherwise they are indepedant of each other
		//----
		rbRect = new RadioButton("Rectangle");
		rbCircle = new RadioButton("Circle");

		ToggleGroup group = new ToggleGroup();
		rbRect.setToggleGroup(group);
		rbCircle.setToggleGroup(group);
		rbRect.setSelected(true);
		
		//initialize shapeList, it is a data structure we used
		//to track the shape we created
		//----
		shapeList = new ArrayList<Shape>();

		//canvas is a Pane where we will draw rectagles and circles on it
		canvas = new Pane();
		canvas.setPrefSize(300, 550);
		canvas.setStyle("-fx-background-color: beige;");

		//initialize the remaining instance variables and set up
		//the layout
		//----
		//----		
		VBox left = new VBox();
		left.setPrefSize(100, 600);
		left.setPadding(new Insets(10, 12, 10, 12));
		left.setSpacing(40);
		left.setAlignment(Pos.TOP_LEFT);
		left.setStyle("-fx-border-color: black");
		left.getChildren().addAll(colorCombo, rbRect, rbCircle);
		
		HBox bottom = new HBox();
		bottom.setPrefSize(400, 50);
		bottom.setPadding(new Insets(10, 12, 10, 12));
		bottom.setSpacing(40);
		bottom.setAlignment(Pos.BOTTOM_CENTER);
		bottom.setStyle("-fx-border-color: black");
		bottom.getChildren().addAll(undoBtn, eraseBtn);
		
		this.setCenter(canvas);
		this.setLeft(left);
		this.setBottom(bottom);
		
		//Step #3: Register the source nodes with its handler objects
		canvas.setOnMousePressed(new MouseHandler());
		canvas.setOnMouseDragged(new MouseHandler());
		canvas.setOnMouseReleased(new MouseHandler());
		
		rbRect.setOnAction(new ShapeHandler());
		rbCircle.setOnAction(new ShapeHandler());
		
		undoBtn.setOnAction(new ButtonHandler());
		eraseBtn.setOnAction(new ButtonHandler());
		
		colorCombo.setOnAction(new ColorHandler());
	}

	//Step #2(A) - MouseHandler
	private class MouseHandler implements EventHandler<MouseEvent>
	{
		public void handle(MouseEvent event)
		{
			//handle MouseEvent here
			//Note: you can use if(event.getEventType()== MouseEvent.MOUSE_PRESSED)
			//to check whether the mouse key is pressed, dragged or released
			//write your own codes here
			//----
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED && rbCircle.isSelected()) {
				x1 = event.getX();
				y1 = event.getY();
				
				c1 = new Circle();
				c1.setCenterX(x1);
				c1.setCenterY(y1);
				
				c1.setFill(Color.WHITE);
				c1.setStroke(Color.BLACK);
				
				shapeList.add(c1);
				canvas.getChildren().add(shapeList.get(shapeList.size() - 1));
			}
			
			else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && rbCircle.isSelected()) {
				double tempX = event.getX();
				double tempY = event.getY();
				
				double dx = Math.abs(tempX - x1);
				double dy = Math.abs(tempY - y1);
				
				double radius = Math.sqrt(dx*dx + dy*dy);
				
				c1.setRadius(radius);
			}
			
			else if (event.getEventType() == MouseEvent.MOUSE_RELEASED && rbCircle.isSelected()) {
				c1.setFill(fillColor);
			}
			
			else if (event.getEventType() == MouseEvent.MOUSE_PRESSED && rbRect.isSelected()) {
				x1 = event.getX();
				y1 = event.getY();
				
				r1 = new Rectangle();
				r1.setX(x1);
				r1.setY(x1);
				
				r1.setStroke(Color.BLACK);
				r1.setFill(Color.WHITE);
				
				shapeList.add(r1);
				canvas.getChildren().add(shapeList.get(shapeList.size() - 1));
			}

			else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED && rbRect.isSelected()) {
				double dx = Math.abs(event.getX() - x1);
				double dy = Math.abs(event.getY() - y1);
				
				r1.setWidth(dx);
				r1.setHeight(dy);
				r1.setX(Math.min(x1, event.getX()));
				r1.setY(Math.min(y1, event.getY()));
				
				
			}
			
			else if (event.getEventType() == MouseEvent.MOUSE_RELEASED && rbRect.isSelected()) {
				r1.setFill(fillColor);
			}


		}//end handle()
	}//end MouseHandler

	//Step #2(B)- A handler class used to handle events from Undo & Erase buttons
	private class ButtonHandler implements EventHandler<ActionEvent>
	{
		public void handle(ActionEvent event)
		{
			//write your codes here
			//----
			Object source = event.getSource();
			if (source == undoBtn) {
				
				canvas.getChildren().remove(shapeList.size() - 1);
				shapeList.remove(shapeList.size() - 1);
			}
			
			else {
				canvas.getChildren().removeAll(shapeList);
				shapeList.clear();
			}


		}
	}//end ButtonHandler

	//Step #2(C)- A handler class used to handle events from the two radio buttons
	private class ShapeHandler implements EventHandler<ActionEvent>
	{
		public void handle(ActionEvent event)
		{
			//write your own codes here
			//----
			if (rbRect.isSelected()) {
				r1 = new Rectangle();
			}
			
			else {
				c1 = new Circle();
			}

		}
	}//end ShapeHandler

	//Step #2(D)- A handler class used to handle colors from the combo box
	private class ColorHandler implements EventHandler<ActionEvent>
	{
		public void handle(ActionEvent event)
		{
			//write your own codes here
			//----
			if (colorCombo.getValue() == "Blue") {
				fillColor = Color.BLUE;
			}
			
			else if (colorCombo.getValue() == "Green") {
				fillColor = Color.GREEN;
			}
			
			else if (colorCombo.getValue() == "Red") {
				fillColor = Color.RED;
			}
			
			else if (colorCombo.getValue() == "Yellow") {
				fillColor = Color.YELLOW;
			}
			
			else if (colorCombo.getValue() == "Orange") {
				fillColor = Color.ORANGE;
			}
			
			else if (colorCombo.getValue() == "Pink") {
				fillColor = Color.PINK;
			}
			
			else {
				fillColor = Color.BLACK;
			}
		}
	}//end ColorHandler

}//end class DrawPane