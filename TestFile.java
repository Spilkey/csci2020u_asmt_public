//  TestFile class holds the data for the tableview

import java.text.DecimalFormat;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TestFile {
    private String filename;
    private String actualClass;
    private double spamProbability;

    public TestFile(String filename, String actualClass, double spamProbability) {
        this.filename = filename;
        this.actualClass = actualClass;
        this.spamProbability = spamProbability;
    }

    public String getFilename() {
        return this.filename;
    }

    public double getSpamProbability() {
        return this.spamProbability;
    }

    public String getSpamProbRounded() {
        DecimalFormat df = new DecimalFormat("0.00000");
        return df.format(this.spamProbability);
    }



    public String getActualClass() {
        return this.actualClass;
    }

    public void setFilename(String value) {
        this.filename = value;
    }

    public void setSpamProbability(double val) {
        this.spamProbability = val;
    }

    public void setActualClass(String value) {
        this.actualClass = value;
    }
}

