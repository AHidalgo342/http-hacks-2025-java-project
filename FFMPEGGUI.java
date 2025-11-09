import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.util.Duration;


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
 * @version 32
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

    private static final int STAGE_HEIGHT_PX = 500;
    private static final int STAGE_WIDTH_PX  = 500;
    private static final int PADDING_PX      = 10;

    private static final int SKIP_FIRST = 1;

    private static final List<Node> NODES_CONSTANT_TOP    = new ArrayList<>();
    private static final List<Node> NODES_CONSTANT_BOTTOM = new ArrayList<>();
    private static final List<Node> NODES_VIDEO           = new ArrayList<>();
    private static final List<Node> NODES_AUDIO           = new ArrayList<>();


    private static final TextField TEXT_FIELD_FILENAME_OUTPUT = new TextField();

    private static Label LABEL_TITLE; // the animated neon title label
    private static Label LABEL_TERMINAL_OUTPUT;


    private static VBox LAYOUT_MAIN;
    private static VBox WHITE_BOX;   // the centered white card that holds buttons/controls


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

        //        final String[] options = {"1", "10"};
        //        TerminalExecutor.convertFile(new File("C:\\Users\\User\\Downloads\\meep.mp4"),//                                     new File("./meep.m4a"));
        //                        TerminalExecutor.compressFile(new File("C:\\Users\\User\\Downloads\\waow.mp4"),
        //                                                      new File("."),
        //                                                      options);
        //                TerminalExecutor.compressFile(new File("C:\\Users\\User\\Downloads\\waow.mp4"),
        //                                              new File("."),
        //                                              options);
        //        TerminalExecutor.compressFile(new File("C:\\Users\\User\\Downloads\\meep.mp4"),
        //                                      new File("./meep.mp4"),
        //                                      options);
        //        TerminalExecutor.convertFile(new File("/home/alex-hidalgo/Downloads/knower.gif"),
        //                                     new File("./knower.m4a"));
        //        TerminalExecutor.convertFile(new File("/home/alex-hidalgo/Videos/deltarune.mp4"),
        //                                     new File("./splosion.gif"));
        //
        //        TerminalExecutor.convertFile(new File("/home/alex-hidalgo/Videos/meep.mp4"),
        //                                     new File("./meep.m4a"));
        //        TerminalExecutor.compressFile(new File("/home/alex-hidalgo/Videos/meep.mp4"),
        //                                      new File("./meep.mp4"),
        //                                      options);
        //        TerminalExecutor.compressFile(new File("/home/alex-hidalgo/Videos/meep.mp4"),
        //                                      new File("./meep.mov"),
        //                                      options);


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
        buttonFileChooser.getStyleClass()
                         .addAll("button",
                                 "selected-file");
        NODES_CONSTANT_TOP.add(buttonFileChooser);
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
                                          if(TEXT_FIELD_FILENAME_OUTPUT.getText()
                                                                       .isBlank())
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

        final GridPane gridPaneFileOutput;
        gridPaneFileOutput = new GridPane();
        gridPaneFileOutput.setHgap(10);
        gridPaneFileOutput.setAlignment(Pos.CENTER);
        NODES_CONSTANT_TOP.add(gridPaneFileOutput);

        // text field input for output name
        TEXT_FIELD_FILENAME_OUTPUT.setPromptText("Output filename");

        gridPaneFileOutput.getChildren()
                          .addFirst(TEXT_FIELD_FILENAME_OUTPUT);
        GridPane.setRowIndex(TEXT_FIELD_FILENAME_OUTPUT,
                             0);
        GridPane.setColumnIndex(TEXT_FIELD_FILENAME_OUTPUT,
                                0);

        //Button for destination chooser
        buttonDestChooser = new Button("Select destination");
        gridPaneFileOutput.getChildren()
                          .addFirst(buttonDestChooser);
        GridPane.setRowIndex(buttonDestChooser,
                             0);
        GridPane.setColumnIndex(buttonDestChooser,
                                1);
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

        // create title label
        LABEL_TITLE = new Label("FFmpeg GUI");
        LABEL_TITLE.getStyleClass()
                   .add("neon-text");

        LABEL_TERMINAL_OUTPUT = new Label("Terminal output will appear here");
        NODES_CONSTANT_BOTTOM.add(LABEL_TERMINAL_OUTPUT);

        // start the neon glow animation
        applyNeonAnimation(LABEL_TITLE);


        WHITE_BOX = new VBox(12);
        WHITE_BOX.setAlignment(Pos.CENTER);
        WHITE_BOX.getStyleClass()
                 .add("white-box");

        WHITE_BOX.getChildren()
                 .add(LABEL_TITLE);
        WHITE_BOX.getChildren()
                 .addAll(NODES_CONSTANT_TOP);
        WHITE_BOX.getChildren()
                 .addAll(NODES_CONSTANT_BOTTOM);


        LAYOUT_MAIN = new VBox(PADDING_PX,
                               WHITE_BOX);
        LAYOUT_MAIN.setAlignment(Pos.CENTER);
        LAYOUT_MAIN.getStyleClass()
                   .add("vbox");


        // Setup scene
        scene = new Scene(LAYOUT_MAIN,
                          STAGE_HEIGHT_PX,
                          STAGE_WIDTH_PX);
        scene.getStylesheets()
             .add(Objects.requireNonNull(getClass().getResource("style.css"))
                         .toExternalForm());

        mainStage.setTitle("JavaFX Test");
        mainStage.setScene(scene);
        mainStage.setOpacity(0.7);
        mainStage.show();
    }

    private static void setupVideo()
    {
        final Button           buttonCompressVideo;
        final ComboBox<String> comboBoxFiletypesVideo;
        final Button           placeholderButton;

        final GridPane gridPaneVideoCompress;
        gridPaneVideoCompress = new GridPane();
        gridPaneVideoCompress.setHgap(10);
        gridPaneVideoCompress.setAlignment(Pos.CENTER);
        NODES_VIDEO.add(gridPaneVideoCompress);

        buttonCompressVideo = new Button("Start Compressing Video");



        gridPaneVideoCompress.getChildren()
                             .addFirst(buttonCompressVideo);

        GridPane.setRowIndex(buttonCompressVideo,
                             0);
        GridPane.setColumnIndex(buttonCompressVideo,
                                1);
        buttonCompressVideo.setOnAction(actionEvent -> compressFile());

        buttonCompressVideo.getStyleClass()
                           .add("compress-video");


        final List<String> fileTypesVideoTrimmed;
        fileTypesVideoTrimmed = Helper.removeFirstCharacters(SKIP_FIRST,
                                                             FILE_TYPES_VIDEO);

        comboBoxFiletypesVideo = new ComboBox<>();
        comboBoxFiletypesVideo.getItems()
                              .addAll(fileTypesVideoTrimmed);
        comboBoxFiletypesVideo.setPromptText("Select output video filetype");

        comboBoxFiletypesVideo.setMaxWidth(200);
        comboBoxFiletypesVideo.setPrefWidth(200);

        placeholderButton = new Button("Placeholder");
        placeholderButton.setOnAction(actionEvent -> {
            System.out.println("Video placeholder button clicked");
        });

        // Create HBox to hold combobox and button side by side
        HBox hBoxFiletypeVideo = new HBox(10); // 10px spacing
        hBoxFiletypeVideo.setAlignment(Pos.CENTER);
        hBoxFiletypeVideo.getChildren().addAll(comboBoxFiletypesVideo, placeholderButton);

        NODES_VIDEO.add(hBoxFiletypeVideo);

        // force the field to be numeric only
        final TextField textFieldNumberTargetMBVideo;
        textFieldNumberTargetMBVideo = new TextField("");
        textFieldNumberTargetMBVideo.setPromptText("Target MB video");
        textFieldNumberTargetMBVideo.getStyleClass()
                                    .add("text-field");
        textFieldNumberTargetMBVideo.textProperty()
                                    .addListener(new ChangeListener<>()
                                    {
                                        @Override
                                        public void changed(final ObservableValue<? extends String> observable,
                                                            final String oldValue,
                                                            String newValue)
                                        {
                                            if(!newValue.matches("\\d*"))
                                            {
                                                textFieldNumberTargetMBVideo.setText(newValue.replaceAll("\\D",
                                                                                                         ""));
                                            }

                                            updateCompressionSize(textFieldNumberTargetMBVideo.getText());
                                        }
                                    });


        gridPaneVideoCompress.getChildren()
                             .addFirst(textFieldNumberTargetMBVideo);
        GridPane.setRowIndex(textFieldNumberTargetMBVideo,
                             0);
        GridPane.setColumnIndex(textFieldNumberTargetMBVideo,
                                0);

    }

    private static void setupAudio()
    {
        final Button           buttonCompressAudio;
        final ComboBox<String> comboBoxFiletypesAudio;
        final Button           placeholderButton;


        final GridPane gridPaneAudioCompress;
        gridPaneAudioCompress = new GridPane();
        gridPaneAudioCompress.setHgap(10);
        gridPaneAudioCompress.setAlignment(Pos.CENTER);
        NODES_AUDIO.add(gridPaneAudioCompress);


        buttonCompressAudio = new Button("Compress Audio");
        buttonCompressAudio.setOnAction(actionEvent -> compressFile());


        gridPaneAudioCompress.getChildren()
                             .addFirst(buttonCompressAudio);
        GridPane.setRowIndex(buttonCompressAudio,
                             0);
        GridPane.setColumnIndex(buttonCompressAudio,
                                1);


        // force the field to be numeric only
        final TextField textFieldNumberTargetMBAudio;
        textFieldNumberTargetMBAudio = new TextField("");
        textFieldNumberTargetMBAudio.setPromptText("Target MB audio");
        textFieldNumberTargetMBAudio.getStyleClass()
                                    .add("text-field");
        textFieldNumberTargetMBAudio.textProperty()
                                    .addListener(new ChangeListener<>()
                                    {
                                        @Override
                                        public void changed(final ObservableValue<? extends String> observable,
                                                            final String oldValue,
                                                            String newValue)
                                        {
                                            if(!newValue.matches("\\d*"))
                                            {
                                                textFieldNumberTargetMBAudio.setText(newValue.replaceAll("\\D",
                                                                                                         ""));
                                            }

                                            updateCompressionSize(textFieldNumberTargetMBAudio.getText());
                                        }
                                    });


        gridPaneAudioCompress.getChildren()
                             .addFirst(textFieldNumberTargetMBAudio);
        GridPane.setRowIndex(textFieldNumberTargetMBAudio,
                             0);
        GridPane.setColumnIndex(textFieldNumberTargetMBAudio,
                                0);


        final List<String> fileTypesAudioTrimmed;
        fileTypesAudioTrimmed = Helper.removeFirstCharacters(SKIP_FIRST,
                                                             FILE_TYPES_AUDIO);

        comboBoxFiletypesAudio = new ComboBox<>();
        comboBoxFiletypesAudio.getItems()
                              .addAll(fileTypesAudioTrimmed);


        comboBoxFiletypesAudio.setMaxWidth(200);
        comboBoxFiletypesAudio.setPrefWidth(200);

        placeholderButton = new Button("Placeholder");
        placeholderButton.setMaxWidth(150);
        placeholderButton.setPrefWidth(150);
        placeholderButton.setOnAction(actionEvent -> {
            System.out.println("Audio placeholder button clicked");
        });

        HBox hBoxFiletypeAudio = new HBox(10);
        hBoxFiletypeAudio.setAlignment(Pos.CENTER);
        hBoxFiletypeAudio.getChildren().addAll(comboBoxFiletypesAudio, placeholderButton);

        NODES_AUDIO.add(hBoxFiletypeAudio); // Add the HBox instead of individual combobox
    }

    private static void SetVBox(VBox layoutMain,
                                final List<Node> nodes)
    {

        WHITE_BOX.getChildren()
                 .clear();
        WHITE_BOX.getChildren()
                 .add(LABEL_TITLE);
        WHITE_BOX.getChildren()
                 .addAll(NODES_CONSTANT_TOP);
        WHITE_BOX.getChildren()
                 .addAll(nodes);
        WHITE_BOX.getChildren()
                 .addAll(NODES_CONSTANT_BOTTOM);

    }

    private static void applyNeonAnimation(final Label label)
    {
        // create an initial glow DropShadow
        final DropShadow glow = new DropShadow();
        glow.setRadius(30);            // blur radius
        glow.setSpread(0.6);          // how much the glow spreads
        glow.setColor(Color.web("#ff005e")); // start color (pink)

        label.setEffect(glow);
        label.setTextFill(Color.WHITE); // keep text readable

        // Timeline to animate the glow color between pink and blue
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO,
                                                      new KeyValue(glow.colorProperty(),
                                                                   Color.web("#ff005e"))),
                                         new KeyFrame(Duration.seconds(1.5),
                                                      new KeyValue(glow.colorProperty(),
                                                                   Color.web("#00d4ff"))));
        timeline.setAutoReverse(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
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

    private static void compressFile()
    {
        String[] options = {compressionSize};
        System.out.println(compressionSize);
        try
        {
            final String outputFileName;
            outputFileName = TEXT_FIELD_FILENAME_OUTPUT.getText();
            Helper.getBaseFileName(outputFileName);

            TerminalExecutor.compressFile(fileToUse,
                                          destDir,
                                          outputFileName,
                                          options);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public static void setTerminalOutput(final String terminalOutputToSet)
    {
        LABEL_TERMINAL_OUTPUT.setText(terminalOutputToSet);

    }

    public static void addTerminalOutput(final String terminalOutputToAdd)
    {
        final String originalLabelTerminalOutputText;
        originalLabelTerminalOutputText = LABEL_TERMINAL_OUTPUT.getText();
        LABEL_TERMINAL_OUTPUT.setText(originalLabelTerminalOutputText + terminalOutputToAdd);

    }
}
