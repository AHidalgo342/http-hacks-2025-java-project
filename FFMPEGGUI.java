import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI entry point.
 *
 * @author Szymon Zemojtel
 * @author alex-hidalgo
 * @version 1
 */
public final class FFMPEGGUI
        extends Application
{

    public static final String[] FILE_TYPES_VIDEO       = {"*.mp4",
                                                           "*.m4a",
                                                           "*.mov",
                                                           "*.avi",
                                                           "*.wmv",
                                                           "*.webm",
                                                           "*.gif"};
    public static final String[] FILE_TYPES_AUDIO = {"*.wav",
                                                     "*.mp3",
                                                     "*.aac",
                                                     "*.flac",
                                                     "*.m4a"};
    private static final String   FILE_DESCRIPTION_VIDEO = "Video Files";
    private static final String   FILE_DESCRIPTION_AUDIO = "Audio Files";


    private static final List<Node> NODES_CONSTANT = new ArrayList<Node>();
    private static final List<Node> NODES_VIDEO    = new ArrayList<Node>();


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
        System.out.println(Terminal.FFmpegExists());
        final Label  label;
        final Button buttonFileChooser;
        final Button buttonDestinationChooser;
        final Scene  scene;

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
                                          if(selectedFile != null)
                                          {
                                              // update select button text
                                              buttonFileChooser.setText("Selected: " + selectedFile.getName());


                                              // get the name of the description of the file type
                                              final String selectedFileDescription = fileChooser.getSelectedExtensionFilter()
                                                                                                .getDescription();
                                              System.out.println(selectedFileDescription);
                                              if(selectedFileDescription.equals(FILE_DESCRIPTION_VIDEO))
                                              {
                                                  System.out.println("this shit is a video");
                                                  SetVBox(LAYOUT_MAIN,
                                                          NODES_VIDEO);
                                              }


                                          }
                                      });

        //Button for destination chooser
        buttonDestinationChooser = new Button("Select Destination");
        NODES_CONSTANT.add(buttonDestinationChooser);
        buttonDestinationChooser.setOnAction(actionEvent ->
                                                     //add functionality later
                                                     System.out.println("Button clicked"));

        setupVideo();

        // Setup VBox layout. Pass elements that will be displayed on it.
        LAYOUT_MAIN = new VBox(10,
                               label,
                               buttonFileChooser,
                               buttonDestinationChooser);

        // Setup scene
        scene = new Scene(LAYOUT_MAIN,
                          300,
                          200);
        mainStage.setTitle("JavaFX Test");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private static void setupVideo()
    {
        final Button buttonCompressVideo;


        buttonCompressVideo = new Button("Compress Video");
        NODES_VIDEO.add(buttonCompressVideo);
        buttonCompressVideo.setOnAction(actionEvent ->
                                        {
                                            // action when button clicked

                                        });

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
