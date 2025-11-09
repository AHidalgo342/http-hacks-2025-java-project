import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GUI entry point.
 *
 * @author Szymon Zemojtel
 * @author Alex Hidalgo
 * @author Daryan Worya
 * @version 22
 */
public final class FFMPEGGUI
        extends Application
{

    private static final String   FILE_DESCRIPTION_VIDEO = "Video Files";
    private static final String   FILE_DESCRIPTION_AUDIO = "Audio Files";
    public static final  String[] FILE_TYPES_VIDEO       = {"*.mp4",
                                                            "*.m4a",
                                                            "*.mov",
                                                            "*.avi",
                                                            "*.wmv",
                                                            "*.webm",
                                                            "*.gif"};
    private static final String[] FILE_TYPES_AUDIO       = {"*.wav",
                                                            "*.mp3",
                                                            "*.aac"};


    private static final List<Node> NODES_CONSTANT = new ArrayList<Node>();
    private static final List<Node> NODES_VIDEO = new ArrayList<Node>();
    private static final List<Node> NODES_AUDIO = new ArrayList<Node>();


    private static VBox LAYOUT_MAIN;

    /**
     * Initial setup of JavaFX GUI and static elements.
     *
     * @param mainStage the main stage the GUI is drawn on.
     */
    @Override
    public void start(final Stage mainStage)
    throws
    IOException,
    InterruptedException
    {
        final Label  label;
        final Button buttonFileChooser;
        final Button buttonDestChooser;
        final Scene  scene;

        System.out.println(Terminal.FFmpegExists());

        TerminalExecutor.convertFile(new File("C:\\Users\\User\\Downloads\\waow.mp4"),
                                     new File("./waow.m4a"));

        // Test label.
        label = new Label("Hello JavaFX!");

        // File Chooser Setup
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter(FILE_DESCRIPTION_VIDEO,
                                                           FILE_TYPES_VIDEO),
                           new FileChooser.ExtensionFilter(FILE_DESCRIPTION_AUDIO,
                                                           FILE_TYPES_AUDIO));

        // Button File Selector setup. Add button event to open file selection.
        buttonFileChooser = new Button("Select a file");
        NODES_CONSTANT.add(buttonFileChooser);
        buttonFileChooser.setOnAction(actionEvent ->
                                      {
                                          // file the user selected
                                          final File selectedFile = fileChooser.showOpenDialog(mainStage);
                                          if(selectedFile == null)
                                          {
                                              return;
                                          }
                                          // update select button text
                                          buttonFileChooser.setText("Selected: " + selectedFile.getName());

                                          // get the name of the description of the file type
                                          final String selectedFileDescription = fileChooser.getSelectedExtensionFilter()
                                                                                            .getDescription();
                                          if(selectedFileDescription.equals(FILE_DESCRIPTION_VIDEO))
                                          {
                                              SetVBox(LAYOUT_MAIN,
                                                      NODES_VIDEO);
                                          }
                                          else if(selectedFileDescription.equals(FILE_DESCRIPTION_AUDIO))
                                          {
                                              SetVBox(LAYOUT_MAIN,
                                                      NODES_AUDIO);
                                          }
                                      });

        //Button for destination chooser
        buttonDestChooser = new Button("Select Destination");
        NODES_CONSTANT.add(buttonDestChooser);
        buttonDestChooser.setOnAction(actionEvent ->
                                      {
                                          //add functionality later
                                          System.out.println("Button clicked");
                                      });

        setupVideo();
        setupAudio();

        // Setup VBox layout. Pass elements that will be displayed on it.
        LAYOUT_MAIN = new VBox(10,
                               label,
                               buttonFileChooser,
                               buttonDestChooser);

        // Setup scene
        scene = new Scene(LAYOUT_MAIN,
                          300,
                          200);
        scene.getStylesheets()
             .add(Objects.requireNonNull(getClass().getResource("style.css"))
                         .toExternalForm());

        mainStage.setTitle("JavaFX Test");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private static void setupVideo()
    {
        final Button           buttonCompressVideo;
        final ComboBox<String> comboBoxFiletypesVideo;

        buttonCompressVideo = new Button("Compress Video");
        NODES_VIDEO.add(buttonCompressVideo);
        buttonCompressVideo.setOnAction(actionEvent ->
                                        {
                                            // action when button clicked

                                        });


        final List<String> fileTypesVideoTrimmed;
        fileTypesVideoTrimmed = Helper.removeFirstCharacters(1, FILE_TYPES_VIDEO);

        comboBoxFiletypesVideo = new ComboBox<String>();
        comboBoxFiletypesVideo.getItems()
                              .addAll(fileTypesVideoTrimmed);
        NODES_VIDEO.add(comboBoxFiletypesVideo);





        // force the field to be numeric only
        final TextField textFieldNumberTargetMB;
        textFieldNumberTargetMB = new TextField("");
        textFieldNumberTargetMB.setPromptText("Target MB");
        textFieldNumberTargetMB.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    textFieldNumberTargetMB.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });

        NODES_VIDEO.add(textFieldNumberTargetMB);


    }



    private static void setupAudio()
    {
        final Button           buttonCompressAudio;
        final ComboBox<String> comboBoxFiletypesAudio;

        buttonCompressAudio = new Button("Compress Audio");
        NODES_AUDIO.add(buttonCompressAudio);
        buttonCompressAudio.setOnAction(actionEvent ->
                                        {
                                            // action when button clicked

                                        });


        final List<String> fileTypesAudioTrimmed;
        fileTypesAudioTrimmed = Helper.removeFirstCharacters(1, FILE_TYPES_AUDIO);

        comboBoxFiletypesAudio = new ComboBox<String>();
        comboBoxFiletypesAudio.getItems()
                              .addAll(fileTypesAudioTrimmed);
        NODES_AUDIO.add(comboBoxFiletypesAudio);

    }

    private static void SetVBox(final VBox vBox,
                                final List<Node> nodes)
    {
        vBox.getChildren()
            .removeAll(vBox.getChildren());
        vBox.getChildren()
            .addAll(NODES_CONSTANT);
        vBox.getChildren()
            .addAll(nodes);
    }
}
