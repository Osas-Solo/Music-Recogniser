package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import music.MusicFileInformation;

import java.util.*;

public class MusicRecogniserApplication extends Application {

    //  declare window content
    Stage window;
    Scene scene;
    BorderPane windowContent;

    //  declare centre content
    VBox centreContent;
    Label instructionLabel;
    TextArea filesNamesDisplay;
    Button browseButton;
    Button recogniseButton;
    Button resetButton;

    //  declare bottom content
    TextArea resultDisplay;

    //  declare other variables
    ArrayList<MusicFileInformation> musicFilesInformation;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //  initialise window content
        window = new Stage();
        windowContent = new BorderPane();
        scene = new Scene(windowContent, 480, 320);
        scene.getStylesheets().add("gui/style.css");
        window.setScene(scene);
        window.setTitle("Music Recogniser");
        window.setMaximized(true);
        window.show();

        //  initialise centre content
        centreContent = new VBox();
        centreContent.setAlignment(Pos.CENTER);
        centreContent.setSpacing(20);
        instructionLabel = new Label("Select files to recognise:");
        filesNamesDisplay = new TextArea();
        filesNamesDisplay.setPromptText("files to rename");
        filesNamesDisplay.setEditable(false);
        browseButton = new Button("Browse");
        recogniseButton = new Button("Recognise Files");
        resetButton = new Button("Reset");
        centreContent.getChildren().addAll(instructionLabel, filesNamesDisplay, browseButton, recogniseButton, resetButton);
        windowContent.setCenter(centreContent);

        //  initialise bottom content
        resultDisplay = new TextArea("Converted files:\n\n");
        resultDisplay.setEditable(false);
        windowContent.setBottom(resultDisplay);

        //  initialise other variables
        musicFilesInformation = new ArrayList<>();

        //  set actions
        browseButton.setOnAction(e -> musicFilesInformation.addAll(MusicRecogniserController.selectMusicFiles(window, filesNamesDisplay)));

        recogniseButton.setOnAction(e -> MusicRecogniserController.renameMusicFiles(musicFilesInformation, window, resultDisplay));

        resetButton.setOnAction(e -> MusicRecogniserController.reset(musicFilesInformation, filesNamesDisplay, resultDisplay));

    }   //  end of start

}   //  end of class
