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

    public static final String[] FILE_TYPES_VIDEO = {"*.mp4",
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
    private static Scene MAIN_SCENE;

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


        mainStage.setTitle("JavaFX Test");
        mainStage.setScene(MAIN_SCENE);
        mainStage.show();

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
                                                  showFileSelectedScene(mainStage, selectedFile);
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

        MAIN_SCENE = new Scene(LAYOUT_MAIN, 300, 200);
        mainStage.setTitle("JavaFX Test");
        mainStage.setScene(MAIN_SCENE);
        mainStage.show();
    }

    private static void setupVideo()
    {
        final Button buttonCompressVideo;

        buttonCompressVideo = new Button("Compress Video");
        NODES_VIDEO.add(buttonCompressVideo);
        buttonCompressVideo.setOnAction(actionEvent ->
                                        { /* action when button clicked */ });

    }
    private static void showFileSelectedScene(Stage mainStage, File selectedFile)
    {
        VBox fileSelectedLayout = new VBox(10);

        Label fileLabel = new Label("Selected: " + selectedFile.getName());
        Button backButton = getBackButton(mainStage);

        fileSelectedLayout.getChildren().addAll(fileLabel, backButton);

        Scene fileSelectedScene = new Scene(fileSelectedLayout, 300, 200);
        mainStage.setScene(fileSelectedScene);
    }

    private static Button getBackButton(Stage mainStage) {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Reset the file chooser button text
            for (Node _ : NODES_CONSTANT) {
                resetToOriginalLayout();
                mainStage.setScene(MAIN_SCENE);
                }

            // Clear any video-specific nodes that were added
            SetVBox(LAYOUT_MAIN, new ArrayList<Node>());
            // Goes back to the main scene
            mainStage.setScene(MAIN_SCENE);
        });
        return backButton;
    }

    private static void resetToOriginalLayout() {
        LAYOUT_MAIN.getChildren().clear();
        // This is to reset the main scene
        // You'll need to get the original label somehow, or recreate it
        Label originalLabel = new Label("Hello JavaFX!");
        LAYOUT_MAIN.getChildren().addAll(originalLabel);
        LAYOUT_MAIN.getChildren().addAll(NODES_CONSTANT);

        // Reset the file chooser button text
        for (Node node : NODES_CONSTANT) {
            if (node instanceof Button button && button.getText().startsWith("Selected: ")) {
                    button.setText("Select a file");
                    break;
                }

        }
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
