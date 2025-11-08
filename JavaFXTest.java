import com.sun.glass.ui.CommonDialogs;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.File;

/**
 * GUI entry point.
 *
 * @author Szymon Zemojtel
 * @version 1
 */
public final class JavaFXTest
        extends Application
{

    /**
     * Initial setup of JavaFX GUI and static elements.
     *
     * @param mainStage the main stage the GUI is drawn on.
     */
    @Override
    public void start(final Stage mainStage)
    {
        final Label  label;
        final VBox   layout;
        final Button buttonFileChooser;
        final Scene  scene;

        // Test label.
        label = new Label("Hello JavaFX!");

        // File Chooser Setup
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter("Image Files",
                                                           "*.png",
                                                           "*.jpg",
                                                           "*.gif"),
                           new FileChooser.ExtensionFilter("Text Files",
                                                           "*.txt"),

                           new FileChooser.ExtensionFilter("Audio Files",
                                                           "*.wav",
                                                           "*.mp3",
                                                           "*.aac"),
                           new FileChooser.ExtensionFilter("Video Files",
                                                           "*.mp4",
                                                           "*.m4a",
                                                           "*.mov",
                                                           "*.avi",
                                                           "*.wmv",
                                                           "*.webm"),
                           new FileChooser.ExtensionFilter("All Files",
                                                           "*.*"));

        // Button File Selector setup. Add button event to open file selection.
        buttonFileChooser = new Button("Select a file");
        buttonFileChooser.setOnAction(e ->
                                      {
                                          File selectedFile = fileChooser.showOpenDialog(mainStage);
                                          if(selectedFile != null)
                                          {
                                              buttonFileChooser.setText("Selected: " + selectedFile.getName());
                                          }
                                      });

        // Setup VBox layout. Pass elements that will be displayed on it.
        layout = new VBox(10,
                          label,
                          buttonFileChooser);

        // Setup scene
        scene = new Scene(layout,
                          300,
                          200);
        mainStage.setTitle("JavaFX Test");
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(final String[] args)
    {
        launch(args);
    }
}
