import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
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
    private static final List<Node> NODES_VIDEO    = new ArrayList<Node>();
    private static final List<Node> NODES_AUDIO    = new ArrayList<Node>();


    private static final TextField TEXT_FIELD_FILENAME_OUTPUT = new TextField();


    private static VBox LAYOUT_MAIN;

    // Files chosen
    private static File   fileToUse;
    private static File   destDir;
    // Compression size (if compressing)
    private static String compressionSize;

    {
        fileToUse       = null;
        destDir         = null;
        compressionSize = null;
    }

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

        final String[] options = {"1", "10"};

        //        TerminalExecutor.convertFile(new File("C:\\Users\\User\\Downloads\\meep.mp4"),
        //                                     new File("./meep.m4a"));
//                TerminalExecutor.compressFile(new File("C:\\Users\\User\\Downloads\\waow.mp4"),
//                                              new File("."),
//                                              options);

        // Test label
        label = new Label("FFmpeg GUI");

        // File/Directory Chooser Setup
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter(FILE_DESCRIPTION_VIDEO,
                                                           FILE_TYPES_VIDEO),
                           new FileChooser.ExtensionFilter(FILE_DESCRIPTION_AUDIO,
                                                           FILE_TYPES_AUDIO));

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Open Resource File");

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

                                          updateChosenFile(selectedFile);


                                          final String filenameNoFiletype = Helper.getBaseFileName(selectedFile.getName());

                                          // update TEXT_FIELD_FILENAME_OUTPUT if blank
                                          if(TEXT_FIELD_FILENAME_OUTPUT.getText().isBlank())
                                          {
                                              TEXT_FIELD_FILENAME_OUTPUT.setText(filenameNoFiletype);
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

        // text field input for output name
        TEXT_FIELD_FILENAME_OUTPUT.setPromptText("Output filename");
        NODES_CONSTANT.add(TEXT_FIELD_FILENAME_OUTPUT);

        //Button for destination chooser
        buttonDestChooser = new Button("Select destination");
        NODES_CONSTANT.add(buttonDestChooser);
        buttonDestChooser.setOnAction(actionEvent ->
                                      {
                                          // Directory the user selected
                                          final File selectedDir = dirChooser.showDialog(mainStage);
                                          if(selectedDir == null)
                                          {
                                              return;
                                          }

                                          updateChosenDir(selectedDir);

                                          // update select button text
                                          buttonDestChooser.setText("Selected: " + selectedDir.getName());
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
                          500,
                          500);
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

        final GridPane gridPaneVideoCompress;
        gridPaneVideoCompress = new GridPane();
        gridPaneVideoCompress.setHgap(10);
        NODES_VIDEO.add(gridPaneVideoCompress);

        buttonCompressVideo = new Button("Start Compressing Video");
        gridPaneVideoCompress.getChildren().addFirst(buttonCompressVideo);

        GridPane.setRowIndex(buttonCompressVideo, 0);
        GridPane.setColumnIndex(buttonCompressVideo, 1);
        buttonCompressVideo.setOnAction(actionEvent ->
                                        {
                                            String[] options = {compressionSize};
                                            System.out.println(compressionSize);
                                            try {
                                                TerminalExecutor.compressFile(fileToUse,
                                                                              destDir,
                                                                              options);
                                            }
                                            catch(Exception e)
                                            {
                                                throw new RuntimeException(e);
                                            }
                                        });

        buttonCompressVideo.getStyleClass()
                           .add("compress-video");


        final List<String> fileTypesVideoTrimmed;
        fileTypesVideoTrimmed = Helper.removeFirstCharacters(1,
                                                             FILE_TYPES_VIDEO);

        comboBoxFiletypesVideo = new ComboBox<String>();
        comboBoxFiletypesVideo.getItems()
                              .addAll(fileTypesVideoTrimmed);
        NODES_VIDEO.add(comboBoxFiletypesVideo);


        // force the field to be numeric only
        final TextField textFieldNumberTargetMB;
        textFieldNumberTargetMB = new TextField("");
        textFieldNumberTargetMB.setPromptText("Target MB");
        textFieldNumberTargetMB.textProperty()
                               .addListener(new ChangeListener<String>()
                               {
                                   @Override
                                   public void changed(final ObservableValue<? extends String> observable,
                                                       final String oldValue,
                                                       String newValue)
                                   {
                                       if(!newValue.matches("\\d*"))
                                       {
                                           textFieldNumberTargetMB.setText(newValue.replaceAll("[^\\d]",
                                                                                               ""));
                                       }

                                       updateCompressionSize(textFieldNumberTargetMB.getText());
                                   }
                               });


        gridPaneVideoCompress.getChildren().addFirst(textFieldNumberTargetMB);
        GridPane.setRowIndex(textFieldNumberTargetMB, 0);
        GridPane.setColumnIndex(textFieldNumberTargetMB, 0);


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
        fileTypesAudioTrimmed = Helper.removeFirstCharacters(1,
                                                             FILE_TYPES_AUDIO);

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


    private static void updateChosenFile(final File file)
    {
        fileToUse = file;
    }

    private static void updateChosenDir(final File dir)
    {
        destDir = dir;
    }

    private static void updateCompressionSize(final String size)
    {
        compressionSize = size;
    }
}
