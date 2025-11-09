import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
 * @author Marcy Ordinario
 * 
 * @version 44
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


    private static final TextField        TEXT_FIELD_FILENAME_OUTPUT = new TextField();
    private static final ComboBox<String> COMBO_BOX_VIDEO_FILETYPES  = new ComboBox<>();

    private static Label      LABEL_TITLE; // the animated neon title label
    private static Label      LABEL_TERMINAL_OUTPUT;
    private static ScrollPane SCROLL_PANE_TERMINAL;
    private static Button     BUTTON_CONVERT_FILETYPES_VIDEO;
    private static Button     BUTTON_CONVERT_FILETYPES_AUDIO;
    private static Button     BUTTON_COMPRESS_VIDEO;
    private static Button     BUTTON_COMPRESS_AUDIO;
    private static Button     BUTTON_DEST_CHOOSER;

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
        final Button buttonFileChooser;
        final Scene  scene;

        System.out.println(Terminal.FFmpegExists());

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
        BUTTON_DEST_CHOOSER = new Button("Select destination");
        gridPaneFileOutput.getChildren()
                          .addFirst(BUTTON_DEST_CHOOSER);
        GridPane.setRowIndex(BUTTON_DEST_CHOOSER,
                             0);
        GridPane.setColumnIndex(BUTTON_DEST_CHOOSER,
                                1);
        BUTTON_DEST_CHOOSER.setOnAction(actionEvent ->
                                        {
                                            // Directory the user selected
                                            final File selectedDir = dirChooser.showDialog(mainStage);
                                            if(selectedDir == null)
                                            {
                                                return;
                                            }

                                            updateChosenDir(selectedDir);

                                            // update select button text
                                            BUTTON_DEST_CHOOSER.setText("Selected: " + selectedDir.getName());
                                        });

        setupVideo();
        setupAudio();

        // create title label
        LABEL_TITLE = new Label("FFmpeg GUI");
        LABEL_TITLE.getStyleClass()
                   .add("neon-text");

        SCROLL_PANE_TERMINAL = new ScrollPane();

        LABEL_TERMINAL_OUTPUT = new Label("Terminal output will appear here");
        SCROLL_PANE_TERMINAL.setContent(LABEL_TERMINAL_OUTPUT);
        SCROLL_PANE_TERMINAL.setMaxHeight(100);
        SCROLL_PANE_TERMINAL.setMaxWidth(400);
        SCROLL_PANE_TERMINAL.setVvalue(0.0);

        NODES_CONSTANT_BOTTOM.add(SCROLL_PANE_TERMINAL);

        LABEL_TERMINAL_OUTPUT.setAlignment(Pos.TOP_LEFT);


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

        //        TerminalExecutor.convertFile(new File("C:\\Users\\User\\Downloads\\waow.mp4"),
        //                                     new File("C:\\Users\\User\\Downloads"),
        //                                     ".gif");

        // Setup scene
        scene = new Scene(LAYOUT_MAIN,
                          STAGE_HEIGHT_PX,
                          STAGE_WIDTH_PX);
        scene.getStylesheets()
             .add(Objects.requireNonNull(getClass().getResource("style.css"))
                         .toExternalForm());


        // scale all elements inside main layout without scaling elements like combo box popup (making it unscrollable)
        LAYOUT_MAIN.setScaleX(1.5);
        LAYOUT_MAIN.setScaleY(1.5);

        mainStage.setMinWidth(615);
        mainStage.setMinHeight(350);
        // debug to display current width and height of stage
        //        mainStage.widthProperty().addListener((obs, oldVal, newVal) -> {
        //            // on stage width change
        //            LABEL_TITLE.setText(mainStage.getWidth() + " x " + mainStage.getHeight());
        //        });
        //
        //        mainStage.heightProperty().addListener((obs, oldVal, newVal) -> {
        //            // on stage height change
        //            LABEL_TITLE.setText(mainStage.getWidth() + " x " + mainStage.getHeight());
        //
        //        });

        mainStage.setTitle("FFmpeg GUI");
        mainStage.setScene(scene);
        mainStage.setOpacity(1.0);

        mainStage.show();
    }

    private static void setupVideo()
    {
        final GridPane gridPaneVideoCompress;
        gridPaneVideoCompress = new GridPane();
        gridPaneVideoCompress.setHgap(10);
        gridPaneVideoCompress.setAlignment(Pos.CENTER);
        NODES_VIDEO.add(gridPaneVideoCompress);

        BUTTON_COMPRESS_VIDEO = new Button("Enter target MB");


        gridPaneVideoCompress.getChildren()
                             .addFirst(BUTTON_COMPRESS_VIDEO);

        GridPane.setRowIndex(BUTTON_COMPRESS_VIDEO,
                             0);
        GridPane.setColumnIndex(BUTTON_COMPRESS_VIDEO,
                                1);
        BUTTON_COMPRESS_VIDEO.setOnAction(actionEvent -> compressFile());

        BUTTON_COMPRESS_VIDEO.getStyleClass()
                             .add("compress-video");
        BUTTON_COMPRESS_VIDEO.setDisable(true);


        final List<String> fileTypesVideoTrimmed;
        fileTypesVideoTrimmed = Helper.removeFirstCharacters(SKIP_FIRST,
                                                             FILE_TYPES_VIDEO);

        COMBO_BOX_VIDEO_FILETYPES.getItems()
                                 .addAll(fileTypesVideoTrimmed);
        COMBO_BOX_VIDEO_FILETYPES.setPromptText("Select output video filetype");

        COMBO_BOX_VIDEO_FILETYPES.setMaxWidth(200);
        COMBO_BOX_VIDEO_FILETYPES.setPrefWidth(200);

        COMBO_BOX_VIDEO_FILETYPES.getSelectionModel()
                                 .selectedItemProperty()
                                 .addListener(event ->
                                              {
                                                  BUTTON_CONVERT_FILETYPES_VIDEO.setDisable(false);

                                                  // set the convert text to "Convert to {filetype to convert to}
                                                  BUTTON_CONVERT_FILETYPES_VIDEO.setText("Convert to " + COMBO_BOX_VIDEO_FILETYPES.getSelectionModel()
                                                                                                                                  .selectedItemProperty()
                                                                                                                                  .get());
                                              });


        BUTTON_CONVERT_FILETYPES_VIDEO = new Button("Select Converting Filetype");
        BUTTON_CONVERT_FILETYPES_VIDEO.setOnAction(actionEvent -> convertFile());
        BUTTON_CONVERT_FILETYPES_VIDEO.setDisable(true);

        // Create HBox to hold combobox and button side by side
        HBox hBoxFiletypeVideo = new HBox(10); // 10px spacing
        hBoxFiletypeVideo.setAlignment(Pos.CENTER);
        hBoxFiletypeVideo.getChildren()
                         .addAll(COMBO_BOX_VIDEO_FILETYPES,
                                 BUTTON_CONVERT_FILETYPES_VIDEO);

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

                                            if(newValue.isBlank() || newValue.equals("0"))
                                            {
                                                BUTTON_COMPRESS_VIDEO.setDisable(true);
                                                BUTTON_COMPRESS_VIDEO.setText("Enter target MB");
                                            }
                                            else
                                            {
                                                BUTTON_COMPRESS_VIDEO.setDisable(false);
                                                BUTTON_COMPRESS_VIDEO.setText("Start compress (aiming for " + textFieldNumberTargetMBVideo.getText() + "MB)");

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
        final ComboBox<String> comboBoxFiletypesAudio;


        final GridPane gridPaneAudioCompress;
        gridPaneAudioCompress = new GridPane();
        gridPaneAudioCompress.setHgap(10);
        gridPaneAudioCompress.setAlignment(Pos.CENTER);
        NODES_AUDIO.add(gridPaneAudioCompress);


        BUTTON_COMPRESS_AUDIO = new Button("Enter target MB");
        BUTTON_COMPRESS_AUDIO.setDisable(true);
        BUTTON_COMPRESS_AUDIO.setOnAction(actionEvent -> compressFile());


        gridPaneAudioCompress.getChildren()
                             .addFirst(BUTTON_COMPRESS_AUDIO);
        GridPane.setRowIndex(BUTTON_COMPRESS_AUDIO,
                             0);
        GridPane.setColumnIndex(BUTTON_COMPRESS_AUDIO,
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


                                            if(newValue.isBlank() || newValue.equals("0"))
                                            {
                                                BUTTON_COMPRESS_AUDIO.setDisable(true);
                                                BUTTON_COMPRESS_AUDIO.setText("Enter target MB");
                                            }
                                            else
                                            {
                                                BUTTON_COMPRESS_AUDIO.setDisable(false);
                                                BUTTON_COMPRESS_AUDIO.setText("Start compress (aiming for " + textFieldNumberTargetMBAudio.getText() + "MB)");

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


        comboBoxFiletypesAudio.getSelectionModel()
                              .selectedItemProperty()
                              .addListener(event ->
                                           {
                                               BUTTON_CONVERT_FILETYPES_AUDIO.setDisable(false);

                                               // set the convert text to "Convert to {filetype to convert to}
                                               BUTTON_CONVERT_FILETYPES_AUDIO.setText("Convert to " + comboBoxFiletypesAudio.getSelectionModel()
                                                                                                                            .selectedItemProperty()
                                                                                                                            .get());
                                           });

        BUTTON_CONVERT_FILETYPES_AUDIO = new Button("Select Converting Filetype");
        BUTTON_CONVERT_FILETYPES_AUDIO.setMaxWidth(150);
        BUTTON_CONVERT_FILETYPES_AUDIO.setPrefWidth(150);
        BUTTON_CONVERT_FILETYPES_AUDIO.setOnAction(actionEvent ->
                                                   {
                                                       // on convert filetypes audio button clicked
                                                   });
        BUTTON_CONVERT_FILETYPES_AUDIO.setDisable(true);

        HBox hBoxFiletypeAudio = new HBox(10);
        hBoxFiletypeAudio.setAlignment(Pos.CENTER);
        hBoxFiletypeAudio.getChildren()
                         .addAll(comboBoxFiletypesAudio,
                                 BUTTON_CONVERT_FILETYPES_AUDIO);

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
        if(destDir == null)
        {
            BUTTON_DEST_CHOOSER.setText("Choose destination directory!");
            return;
        }

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

    private static void convertFile()
    {
        if(destDir == null)
        {
            BUTTON_DEST_CHOOSER.setText("Choose destination directory!");
            return;
        }

        String fileType = COMBO_BOX_VIDEO_FILETYPES.getValue();
        System.out.println(compressionSize);
        try
        {
            final String outputFileName;
            outputFileName = TEXT_FIELD_FILENAME_OUTPUT.getText();
            Helper.getBaseFileName(outputFileName);

            TerminalExecutor.convertFile(fileToUse,
                                         destDir,
                                         fileType);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    public static void setTerminalOutput(final String terminalOutputToSet)
    {
        LABEL_TERMINAL_OUTPUT.setText(terminalOutputToSet);
        SCROLL_PANE_TERMINAL.setVvalue(1.0);

    }

    public static void addTerminalOutput(final String terminalOutputToAdd)
    {
        final String originalLabelTerminalOutputText;
        originalLabelTerminalOutputText = LABEL_TERMINAL_OUTPUT.getText();
        LABEL_TERMINAL_OUTPUT.setText(originalLabelTerminalOutputText + terminalOutputToAdd);

        // update what the terminal thinks its size is
        SCROLL_PANE_TERMINAL.layout();
        // scroll to bottom of scroll pane to see live terminal
        SCROLL_PANE_TERMINAL.setVvalue(1.0);


    }
}
