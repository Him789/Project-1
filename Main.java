package com.internshala.connect4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();

        controller = loader.getController();
        controller.createPlayGround();

        MenuBar menuBar = createMenu();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());    //Stretch the menuBar

        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);

        Scene scene = new Scene(rootGridPane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect4");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){


        //Fil Menu
        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> {                       // setting action in New Game
            controller.resetGame();
        });
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> {                       // setting action in Reset Game
            controller.resetGame();
        });
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event -> exitGame());          //setting action in Exit Game


        fileMenu.getItems().addAll(newGame , resetGame , separatorMenuItem , exitGame);

        //Help Menu
        Menu helpMenu = new Menu("Help");

        MenuItem connect4 = new MenuItem("About Connect4");
        connect4.setOnAction(event -> aboutConnect4());

        SeparatorMenuItem seperate  = new SeparatorMenuItem();    //seperator

        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> aboutMe());

        helpMenu.getItems().addAll(connect4 , seperate , aboutMe);

       //Displaying MenuBar i.e File and About
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu , helpMenu);


        return menuBar;
    }

    private void aboutMe() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("Raj Himanshu");
        alert.setContentText("I love to code and create games, Connect4 is my first game that I developed ");
        alert.show();
    }

    private void aboutConnect4() {      //Method created for aboutGame in MenuBar

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect4");
        alert.setHeaderText("How to Play");
        alert.setContentText("Connect Four (also known as Four Up, Plot Four, Find Four, Four in a Row, Drop "+
                             "Four, and Gravitrips in the Soviet Union) is a two-player connection board game,"+
                             "in which the players choose a color and then take turns dropping colored discs "+
                             "into a seven-column, six-row vertically suspended grid.");
        alert.show();
    }

    private void exitGame() {          //Method created for exiting the game
        Platform.exit();
        System.exit(0);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
