import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingsLayoutController extends Dialog<Void> implements Initializable
{

    @FXML
    private Button btnDefaults;

    @FXML
    private ChoiceBox<Integer> choiceSegments;

    @FXML
    private Spinner<Integer> spinnerRocks;

    private ObservableList<Integer> segmentChoices = FXCollections.observableArrayList(5,6,7,8,9,10,11,12,13,14,15);

    //private ObservableList<Integer> rockChoices = FXCollections.observableArrayList(10,11,12,13,14,15,16,17,18,19,20);

    private SpinnerValueFactory<Integer> rockChoices = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 20, settingsData.rockCount);


    @FXML
    private void onDefaults(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Back to Defaults");
        alert.setHeaderText("All settings have been reset!");
        alert.showAndWait();
        settingsData.rockCount = 15;
        settingsData.numSegments = 10;
        choiceSegments.setValue(10);
        spinnerRocks.getValueFactory().setValue(15);

    }
    public SettingsLayoutController() throws IOException
    {
        super();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("settingsController.fxml"));
        //Set Controller
        loader.setController(this);
        Parent root = loader.load();
        getDialogPane().setContent(root);

        //Initialize OK button
        ButtonType btnOK = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().add(btnOK);

        //OK button handler
        Button ok = (Button) getDialogPane().lookupButton(btnOK);
        ok.addEventFilter(ActionEvent.ACTION, actionEvent -> onOK(actionEvent));

        //Cancel Button
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(btnCancel);

        //Defaults button
        btnDefaults.addEventFilter(ActionEvent.ACTION, actionEvent -> onDefaults(actionEvent));

    }


    private void onOK(ActionEvent event)
    {
        settingsData.numSegments = choiceSegments.getValue();
        settingsData.rockCount = spinnerRocks.getValue();
        storePref(settingsData.class);
    }

    public void readPref(Class c)
    {
        Preferences pref = Preferences.userNodeForPackage(c);

        settingsData.numSegments = pref.getInt("Number of Segments", settingsData.numSegments);
        settingsData.rockCount = pref.getInt("Number of Rocks", settingsData.rockCount);
        settingsData.highScore = pref.getInt("High Score", settingsData.highScore);
    }

    public void storePref(Class c)
    {
        Preferences pref = Preferences.userNodeForPackage(c);

        pref.putInt("Number of Segments", settingsData.numSegments);
        pref.putInt("Number of Rocks", settingsData.rockCount);
        pref.putInt("High Score", settingsData.highScore);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        readPref(settingsData.class);
        choiceSegments.setItems(segmentChoices);
        choiceSegments.setValue(settingsData.numSegments);
        spinnerRocks.setValueFactory(rockChoices);

    }
}
