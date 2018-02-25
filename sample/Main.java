// Main class holds UI stage and main function to run everything

package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Main extends Application {
    // Define variables
    private TableView<TestFile> email;
    private BorderPane layout = new BorderPane();
    private TextField accuracyField, precisionField;

    @Override
    public void start(Stage primaryStage) {
        // Create table
        TableColumn<TestFile, String> fileCol = new TableColumn("File");
        fileCol.setPrefWidth(300);
        fileCol.setCellValueFactory(new PropertyValueFactory<>("filename"));

        TableColumn<TestFile, String> actualCol = new TableColumn("Actual Class");
        actualCol.setPrefWidth(100);
        actualCol.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

        TableColumn<TestFile, Double> spamprobCol = new TableColumn("Spam Probability");
        spamprobCol.setPrefWidth(300);
        spamprobCol.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));

        this.email = new TableView<>();
        this.email.getColumns().add(fileCol);
        this.email.getColumns().add(actualCol);
        this.email.getColumns().add(spamprobCol);

        // Edit box
        GridPane editArea = new GridPane();
        editArea.setPadding(new Insets(10, 10, 10, 10));
        editArea.setVgap(10);
        editArea.setHgap(10);

        Label accuracy = new Label("Accuracy: ");
        editArea.add(accuracy, 0, 1);
        accuracyField = new TextField();
        accuracyField.setPromptText("");
        editArea.add(accuracyField, 1, 1);

        Label precision = new Label("Precision: ");
        editArea.add(precision, 0, 2);
        precisionField = new TextField();
        precisionField.setPromptText("");
        editArea.add(precisionField, 1, 2);

        layout.setCenter(email);
        layout.setBottom(editArea);

        primaryStage.setTitle("Spam Master 3000");
        primaryStage.setScene(new Scene(layout, 700, 600));
        primaryStage.show();
        //this.email.setItems(TODO put observable list in here);


        // Choose directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(primaryStage);

        // Count the number of words in the file
        WordCounter wordCounter = new WordCounter();
        File dataDir = new File(mainDirectory.toString());
        File outFile = new File("output.txt");

        try {
            wordCounter.processFile(dataDir);
            wordCounter.outputWordCounts(2, outFile);
        } catch (FileNotFoundException e) {
            System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Map<String, Integer> trainSpamFreq = new HashMap<String, Integer>();
        //Map<String, Integer> trainHamFreq = new HashMap<String, Integer>();

    }

    public static void main(String[] args) {
        launch(args);
    }
}