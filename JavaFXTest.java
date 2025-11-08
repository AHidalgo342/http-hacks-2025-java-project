import com.sun.glass.ui.CommonDialogs;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.File;

public final class JavaFXTest
        extends Application
{
    @Override
    public void start(final Stage mainStage)
    {
        final Label  label;
        final Scene  scene;
        final VBox   layout;
        final Button buttonFileSelect;

        label = new Label("Hello JavaFX!");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter("Text Files",
                                                           "*.txt"),
                           new FileChooser.ExtensionFilter("Image Files",
                                                           "*.png",
                                                           "*.jpg",
                                                           "*.gif"),
                           new FileChooser.ExtensionFilter("Audio Files",
                                                           "*.wav",
                                                           "*.mp3",
                                                           "*.aac"),
                           new FileChooser.ExtensionFilter("All Files",
                                                           "*.*"));

        buttonFileSelect = new Button("Select a file");
        buttonFileSelect.setOnAction(e ->
                                     {
                                         File selectedFile = fileChooser.showOpenDialog(mainStage);
                                         if(selectedFile != null)
                                         {
                                             label.setText("Selected: " + selectedFile.getName());
                                         }
                                     });


        layout = new VBox(10,
                          label,
                          buttonFileSelect);

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
