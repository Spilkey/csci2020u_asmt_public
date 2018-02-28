package sample;
// Main class holds UI stage and main function to run everything

// Javafx imports
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

// File and Math imports for taking in files and using ln and e
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.lang.Math;

public class Main extends Application {
    // Define variables
    private TableView<TestFile> email;
    private BorderPane layout = new BorderPane();
    private ObservableList<TestFile> files = FXCollections.observableArrayList();
    private double truePositive = 0.0;
    private double trueNegative = 0.0;

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

        // Choose directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));

        directoryChooser.setTitle("Open Folder Ham1 Folder for training");
        File directoryChooseHam1 = directoryChooser.showDialog(primaryStage);
        directoryChooser.setTitle("Open Folder Ham2 Folder for training");
        File directoryChooseHam2 = directoryChooser.showDialog(primaryStage);
        directoryChooser.setTitle("Open Folder Spam Folder for training");
        File directoryChooseSpam = directoryChooser.showDialog(primaryStage);

        // Count the number of words in the file
        WordCounter wordCounterHam1 = new WordCounter();
        WordCounter wordCounterHam2 = new WordCounter();
        WordCounter wordCounterSpam = new WordCounter();

        File ham1Dir = new File(directoryChooseHam1.toString());
        File ham2Dir = new File(directoryChooseHam2.toString());
        File spamDir = new File(directoryChooseSpam.toString());

        try {
            wordCounterHam1.processFile(ham1Dir);
            wordCounterHam2.processFile(ham2Dir);
            wordCounterSpam.processFile(spamDir);
        } catch (FileNotFoundException e) {
            System.err.println("Invalid input hamDir: " + ham1Dir.getAbsolutePath());
            System.err.println("Invalid input ham2Dir: " + ham2Dir.getAbsolutePath());
            System.err.println("Invalid input spamDir: " + spamDir.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Define maps that hold ham/spam word counts and probability for each word
        Map<String, Integer> HamsMap = new HashMap<>();
        Map<String, Integer> SpamsMap = wordCounterSpam.getWordCounts();;
        Map<String, Double> HamsProb = new HashMap<>();
        Map<String, Double> SpamsProb = new HashMap<>();
        Map<String, Double> isWordSpamProb = new HashMap<>();
        // Linked list containing all the words whether ham or spam
        LinkedList<String> allWords = new LinkedList<>();

        // Merge wordCounterHam1 and wordCounterHam2 into one map (since they are 2 seperate file folders)
        HamsMap.putAll(wordCounterHam1.getWordCounts());
        for (Map.Entry<String, Integer> entry: wordCounterHam2.getWordCounts().entrySet()) {
            if (HamsMap.containsKey(entry.getKey())) {
                HamsMap.replace(entry.getKey(), HamsMap.get(entry.getKey()), HamsMap.get(entry.getKey()) + entry.getValue());
                System.out.println(entry.getKey()+ " new value is "+  HamsMap.get(entry.getKey()) + entry.getValue()+ "Individual values are "+ HamsMap.get(entry.getKey())+ " and "+entry.getValue());
            } else {
                HamsMap.put(entry.getKey(),entry.getValue());
            }
        }

        System.out.println(HamsMap);

        // Find number of files in each training folder
        int numOfHamFiles = ham1Dir.listFiles().length + ham2Dir.listFiles().length;
        int numOfSpamFiles = spamDir.listFiles().length;

        //Calculating Pr ( Wi|H)=
        for (Map.Entry<String, Integer> entry : HamsMap.entrySet()) {
            HamsProb.put(entry.getKey(), (double)entry.getValue()/numOfHamFiles);

            if (!allWords.contains(entry.getKey())) {
                allWords.add(entry.getKey());
            }
        }

        //Calculating Pr ( Wi|S)=
        for (Map.Entry<String, Integer> entry : SpamsMap.entrySet()) {
            SpamsProb.put(entry.getKey(), (double)entry.getValue()/numOfSpamFiles);

            if (!allWords.contains(entry.getKey())) {
                allWords.add(entry.getKey());
            }
        }

        System.out.println(SpamsProb);
        for (String currentWord : allWords) {
            double currentHamProb = 0.0;
            double currentSpamProb = 0.0;
            if (SpamsProb.containsKey(currentWord)) {
                currentSpamProb = SpamsProb.get(currentWord);
            }
            if (HamsProb.containsKey(currentWord)) {
                currentHamProb = HamsProb.get(currentWord);
            }
            isWordSpamProb.put(currentWord, (currentSpamProb)/(currentHamProb+currentSpamProb));
            System.out.println("Current word is this "+currentWord);
            System.out.println("Probablility of that word being in spam file"+(currentSpamProb)/(currentHamProb+currentSpamProb));
        }

        directoryChooser.setTitle("Open Folder Ham Folder for Testing");
        File directoryChooseTestHam = directoryChooser.showDialog(primaryStage);
        directoryChooser.setTitle("Open Folder Spam Folder for Testing");
        File directoryChooseTestSpam = directoryChooser.showDialog(primaryStage);

        try {
            processTestFile(directoryChooseTestHam, "Ham", isWordSpamProb);
            processTestFile(directoryChooseTestSpam, "Spam", isWordSpamProb);
            System.out.println(isWordSpamProb);
        } catch(FileNotFoundException e) {
            System.err.println("Invalid input hamDir: " + ham1Dir.getAbsolutePath());
            System.err.println("Invalid input spamDir: " + spamDir.getAbsolutePath());
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File hamtestDir = new File(directoryChooseTestHam.toString());
        File spamtestDir = new File(directoryChooseTestSpam.toString());

        int hamTestCount = hamtestDir.list().length;
        int spamTestCount = spamtestDir.list().length;

        double Accuracy = (trueNegative + truePositive)/(hamTestCount +spamTestCount);
        double Precision = truePositive/((spamTestCount-truePositive) + truePositive);
        double HamPrecision = trueNegative/((hamTestCount-trueNegative)+trueNegative);

        // Printing accuracy and precision in labels
        Label accuracy = new Label("Accuracy: ");
        editArea.add(accuracy, 0, 1);
        Label accuracynum = new Label(Double.toString(Accuracy));
        editArea.add(accuracynum, 1, 1);

        Label precision = new Label("Precision: ");
        editArea.add(precision, 0, 2);
        Label precisionnum = new Label(Double.toString(Precision));
        editArea.add(precisionnum, 1, 2);

        Label hamprecision = new Label("Ham Precision: ");
        editArea.add(hamprecision, 0, 3);
        Label hamprecisionnum = new Label(Double.toString(HamPrecision));
        editArea.add(hamprecisionnum, 1, 3);

        layout.setCenter(email);
        layout.setBottom(editArea);

        primaryStage.setTitle("Spam Master 3000");
        primaryStage.setScene(new Scene(layout, 700, 600));

        this.email.setItems(this.printFiles());

        primaryStage.show(); // end of UI code

    }

    // Main function Overridden by javafx
    public static void main(String[] args) {
        launch(args);
    }

    // #TODO Maybe put the next 4 functions in another class
    public ObservableList<TestFile> printFiles() {
        return files;
    }

    public void addFiles(TestFile file) {
        this.files.add(file);
    }

    public void processTestFile(File file, String actualclass, Map<String, Double> isWordSpam) throws IOException {
        System.out.println("Processing " + file.getAbsolutePath() + "...");

        if (file.isDirectory()) {
            // process all the files in that directory
            File[] contents = file.listFiles();
            for (File current : contents) {
                processTestFile(current,actualclass, isWordSpam);
            }
        } else if (file.exists()) {
            // count the words in this file
            double sum = 0.0;
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s"); // "[\s\.;:\?\!,]");//" \t\n.;,!?-/\\");
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isWord(word)) {
                    if(isWordSpam.containsKey(word)) {
                        // temps used to calculate sums and stop infinite values from division
                        double temp1 = Math.log(1.0 - isWordSpam.get(word));
                        double temp2 = Math.log(isWordSpam.get(word));
                        if(Double.isInfinite(temp1)){ temp1 = 0.0;}
                        if(Double.isInfinite(temp2)){ temp2 = 0.0;}

                        sum += temp1 - temp2;
                    }
                }

            }
            double SFprob = 1.0/(1.0 + Math.exp(sum)); // 1/1+(e^sum)
            if ((actualclass.equals("Ham") && SFprob <= 0.5)) {
                trueNegative ++;
            } else if(actualclass.equals("Spam") && SFprob > 0.5) {
                truePositive ++;
            }
            this.addFiles(new TestFile(file.getName(), actualclass, SFprob));
        }
    }

    private boolean isWord(String word) {
        String pattern = "^[a-zA-Z]+$";
        return word.matches(pattern);
    }
}
