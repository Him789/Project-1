package com.internshala.connect4;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import javax.xml.soap.Text;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER = 80;
	private static final String discColor1 = "#24303E";
	private static final String discColor2 = "#4CAA88";

	private static String PLAYER_ONE = "Player One";
	private static String PLAYER_TWO = "Player Two";

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscArray = new Disc [ROWS][COLUMNS]; //Fot the structural changes : for the Developer


	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertDiscPane;

	@FXML
	public Label playerNameLabel;


	@FXML
	public TextField playerOneTextField, playerTwoTextField;

	@FXML
	public Button setNamesButton;

	private boolean isAllowedToInsert = true;

	public void createPlayGround() {


		Platform.runLater(() -> setNamesButton.requestFocus());

		Shape rectangleWithHoles = createGamesStructuralGrid();         //calling createGamesStructuralGrid
		rootGridPane.add(rectangleWithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();      //calling createClickableColumns

		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
		setNamesButton.setOnAction(event -> {
			PLAYER_ONE = playerOneTextField.getText();
			PLAYER_TWO = playerTwoTextField.getText();
			playerNameLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
		});
	}

	private Shape createGamesStructuralGrid() {                       // method to create holes in rectangle


		Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);

		for (int row = 0; row < ROWS; row++) {

			for (int col = 0; col < COLUMNS; col++) {

				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER / 2);
				circle.setCenterX(CIRCLE_DIAMETER / 2);
				circle.setCenterY(CIRCLE_DIAMETER / 2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
				circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

				rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle);   //subtacting from rectangle to get the cicle
			}
		}
		rectangleWithHoles.setFill(Color.WHITE);

		return rectangleWithHoles;
	}

	private List<Rectangle> createClickableColumns() {

		List<Rectangle> rectangleList = new ArrayList<>();

		for (int col = 0; col < COLUMNS; col++) {

			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) +CIRCLE_DIAMETER / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
			rectangle.setOnMouseClicked(event -> {

				if(isAllowedToInsert) {
					isAllowedToInsert = false;
					insertDisc(new Disc(isPlayerOneTurn), column);    //calling insertDisc
				}
			});

			rectangleList.add(rectangle);
		}
		return rectangleList;
	}

	private void insertDisc(Disc disc , int column){         //method created to insert Disc

      int row = ROWS-1;
      while(row>=0) {

      	if(getDiscIfPresent(row , column) == null )
      		break;                       // if the row column = null (i.e empty space)then it will break out of the loop

      	row--;    //if the array is not empty then decrement the row value
      }
      if(row<0)      // if the column is full then we cannot insert anymore disc
      	return;

       insertedDiscArray[row] [column] = disc;     //structural changes : for developers
       insertDiscPane.getChildren().add(disc);   //inserting Disc visually (its the 2nd pane in scene builder

		disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) +CIRCLE_DIAMETER / 4);  /* inserting disc in each column
		                                                                             whenever player is clicking the
		                                                                             circle*/

		int currentRow = row;

		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5) , disc);
		translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
		translateTransition.setOnFinished(event -> {

			isAllowedToInsert = true;

			if(gameEnded(currentRow , column)) {

				gameOver();
				return;
			}

			isPlayerOneTurn = !isPlayerOneTurn;  // passed the controller to the second player

			playerNameLabel.setText(isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO);
		});
		translateTransition.play();
	}

	private boolean gameEnded(int row , int column){

		//Index of each element present in column [row][column]

		List<Point2D> verticalPoints =  IntStream.rangeClosed(row-3 , row+3) //range of row values = 0,1,2,3,4,5
				                       .mapToObj(r ->new Point2D(r ,column)) //0,3 1,3 2,3 3,3 4,3 5,3
				                       .collect(Collectors.toList());

		List<Point2D> horizontalPoints =  IntStream.rangeClosed(column-3 , column+3)
				                          .mapToObj(c ->new Point2D(row ,c))
				                          .collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row-3 , column+3);
		List<Point2D> diagonalPoints = IntStream.rangeClosed(0 , 6)
				                       .mapToObj(i -> startPoint1.add(i , -i))
				                       .collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row-3 , column-3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0 , 6)
				.mapToObj(i -> startPoint2.add(i , i))
				.collect(Collectors.toList());



		boolean isEnded = checkCombinations(verticalPoints)|| checkCombinations( horizontalPoints)
			            	|| checkCombinations(diagonalPoints) || checkCombinations(diagonal2Points);
		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;

		for(Point2D point : points){

			int rowIndexForArray = (int)point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscIfPresent(rowIndexForArray , columnIndexForArray);
			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn) {

				chain++;
				if (chain == 4) {
					return true;
				}
			}
				else {
					chain = 0;
				}
		}
		return false;
	}

	private Disc getDiscIfPresent(int row , int column){

		if(row>=ROWS || row <0 || column >= COLUMNS || column<0)
			return null;

		return insertedDiscArray[row][column];

	}

	private void gameOver() {


		String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
		//System.out.println("Winner is: "+ winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect4");
		alert.setHeaderText("The Winner is: "+winner);
		alert.setContentText("Want to play again? ");

		ButtonType yesBut = new ButtonType("Yes");        //Creating Button yes in the result console
		ButtonType noBut = new ButtonType("No Exit");      // Creating No Exit Button in the result console
		alert.getButtonTypes().setAll(yesBut , noBut);

		Platform.runLater( () ->{                       /* this code will endure that show and wait method will
		                                                   only be executed when the animation is over*/

			Optional<ButtonType> btnClicked =  alert.showAndWait();
			if( btnClicked.isPresent() && btnClicked.get() == yesBut){

				resetGame();
			}
			else{
				Platform.exit();
				System.exit(0);
			}
		});
	}

	public void resetGame() {

		insertDiscPane.getChildren().clear();

		for (int row = 0; row < insertedDiscArray.length; row++) {

			for (int col = 0; col < insertedDiscArray.length; col++) {

				insertedDiscArray[row][col] = null;
			}

		}

		isPlayerOneTurn = true;
		playerNameLabel.setText(PLAYER_ONE);

		createPlayGround();
	}

	private static class Disc extends Circle{       //Disc is sub class of Circle

		private final boolean isPlayerOneMove;
		public Disc(boolean isPlayerOneMove){     //Constructor created to verify the color of the player

			this.isPlayerOneMove = isPlayerOneMove;    //this keyword is used
			setRadius(CIRCLE_DIAMETER / 2);
			setFill(isPlayerOneMove ? Color.valueOf(discColor1) : Color.valueOf(discColor2)); /*this statement is used
			                                                                                    to classify the color
			                                                                                    of the player */
			setCenterX(CIRCLE_DIAMETER / 2);
			setCenterY(CIRCLE_DIAMETER / 2);

		}

	}

	    @Override
		public void initialize (URL location, ResourceBundle resources){

		}
	}

