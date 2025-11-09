import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
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
 * @version 44
 */
public final class FFmpegGUI
        extends Application
{
    private static final String FILE_DESCRIPTION_VIDEO = "Video Files";
    private static final String FILE_DESCRIPTION_AUDIO = "Audio Files";

    public static final  String[] FILE_TYPES_VIDEO = {"*.mp4",
                                                      "*.m4a",
                                                      "*.mov",
                                                      "*.avi",
                                                      "*.wmv",
                                                      "*.webm"};
    private static final String[] FILE_TYPES_AUDIO = {"*.wav",
                                                      "*.mp3",
                                                      "*.aac"};


    private static final int STAGE_HEIGHT_PX = 500;
    private static final int STAGE_WIDTH_PX  = 800;
    private static final int PADDING_PX      = 10;
    private static final int SKIP_FIRST      = 1;

    private static final List<Node> NODES_CONSTANT_TOP    = new ArrayList<>();
    private static final List<Node> NODES_CONSTANT_BOTTOM = new ArrayList<>();
    private static final List<Node> NODES_VIDEO           = new ArrayList<>();
    private static final List<Node> NODES_AUDIO           = new ArrayList<>();

    private static final TextField        TEXT_FIELD_FILENAME_OUTPUT = new TextField();
    private static final ComboBox<String> COMBO_BOX_VIDEO_FILETYPES  = new ComboBox<>();
    private static final ComboBox<String> COMBO_BOX_AUDIO_FILETYPES  = new ComboBox<>();

    private static Label      LABEL_TITLE;
    private static Label      LABEL_TERMINAL_OUTPUT;
    private static ScrollPane SCROLL_PANE_TERMINAL;
    private static Button     BUTTON_CONVERT_FILETYPES_VIDEO;
    private static Button     BUTTON_CONVERT_FILETYPES_AUDIO;
    private static Button     BUTTON_COMPRESS_VIDEO;
    private static Button     BUTTON_COMPRESS_AUDIO;
    private static Button     BUTTON_FILE_DESTINATION_CHOOSER;

    private static VBox VBOX_CONTAINER;

    private static File   fileToUse;
    private static File   fileDestinationDirectory;
    private static String compressionSizeMB;

    static
    {
        fileToUse                = null;
        fileDestinationDirectory = null;
        compressionSizeMB        = null;
    }

    /**
     * Program entry point.
     * Ensures user has fulfilled FFmpeg requirement.
     * If user doesn't have a valid FFmpeg installation, let them know.
     *
     * @param mainStage the main stage the GUI is drawn on.
     */
    @Override
    public void start(final Stage mainStage)
    throws
    IOException,
    InterruptedException
    {
        // if we can't find FFmpeg, tell the user and let them exit early.
        if(Terminal.FFmpegExists())
        {
            startValidFFmpeg(mainStage);
            return;
        }

        final Stage    stagePopup;
        final VBox     vboxPopup;
        final GridPane gridPanePopupButtons;
        final Button   buttonPopupCheckAgain;
        final Button   buttonPopupQuit;
        final Label    labelPopupText;
        final Scene    scenePopup;


        stagePopup            = new Stage();
        vboxPopup             = new VBox();
        gridPanePopupButtons  = new GridPane();
        buttonPopupCheckAgain = new Button("Check for FFmpeg again");
        buttonPopupQuit       = new Button("Quit");
        labelPopupText        = new Label("We couldn't find FFmpeg on your device.\nPlease install it and try again.");
        scenePopup            = new Scene(vboxPopup,
                                          400,
                                          185);


        // add quit button to the bottom left
        gridPanePopupButtons.getChildren()
                            .add(buttonPopupQuit);
        GridPane.setRowIndex(buttonPopupQuit,
                             0);
        GridPane.setColumnIndex(buttonPopupQuit,
                                0);


        // add try again button to the bottom right
        gridPanePopupButtons.getChildren()
                            .add(buttonPopupCheckAgain);
        GridPane.setRowIndex(buttonPopupCheckAgain,
                             0);
        GridPane.setColumnIndex(buttonPopupCheckAgain,
                                1);


        // spacing and alignment for quit and try again buttons
        gridPanePopupButtons.setHgap(150);
        gridPanePopupButtons.setPadding(new Insets(50,
                                                   0,
                                                   50,
                                                   0));
        vboxPopup.setAlignment(Pos.BASELINE_CENTER);
        gridPanePopupButtons.setAlignment(Pos.BASELINE_CENTER);


        // add nodes to the vBox to display
        vboxPopup.getChildren()
                 .addAll(labelPopupText,
                         gridPanePopupButtons);

        // when user clicks quit, exit the program
        buttonPopupQuit.setOnMouseClicked(onClick -> System.exit(0));

        // when user clicks try again, check to see if they have a valid FFmpeeg installation.
        buttonPopupCheckAgain.setOnMouseClicked(onClick ->
                                                {

                                                    // if user has valid FFmpeg installation, let them continue.
                                                    if(Terminal.FFmpegExists())
                                                    {
                                                        stagePopup.hide();
                                                        startValidFFmpeg(mainStage);
                                                    }
                                                });

        // Scene setup
        stagePopup.setScene(scenePopup);
        stagePopup.setResizable(false);
        stagePopup.setMinWidth(400);
        stagePopup.setMinHeight(300);
        stagePopup.show();
    }

    /**
     * May only enter if user has valid FFmpeg installation.
     * Initial setup of JavaFX GUI and static elements.
     *
     * @param mainStage the main stage the GUI is drawn on.
     */
    public void startValidFFmpeg(final Stage mainStage)
    {
        final FileChooser      fileChooser;
        final DirectoryChooser dirChooser;
        final Button           buttonFileChooser;
        final Scene            scene;
        final GridPane         gridPaneFileOutput;
        final VBox             layoutMain;

        // file/directory chooser setup
        fileChooser = new FileChooser();
        fileChooser.setTitle("JEFFmpeg Select File");

        // add file chooser Video and Audio filetypes to dropdown
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter(FILE_DESCRIPTION_VIDEO,
                                                           FILE_TYPES_VIDEO),
                           new FileChooser.ExtensionFilter(FILE_DESCRIPTION_AUDIO,
                                                           FILE_TYPES_AUDIO));

        dirChooser = new DirectoryChooser();
        dirChooser.setTitle("JEFFmpeg Select File");

        // Button File Selector setup. Add button event to open file selection
        buttonFileChooser = new Button("Select a file");
        buttonFileChooser.getStyleClass()
                         .addAll("button",
                                 "selected-file");
        NODES_CONSTANT_TOP.add(buttonFileChooser);

        // called after file is chosen
        buttonFileChooser.setOnAction(actionEvent ->
                                      {

                                          final File selectedFile;

                                          // file the user selected
                                          selectedFile = fileChooser.showOpenDialog(mainStage);

                                          if(selectedFile == null)
                                          {
                                              return;
                                          }

                                          final String baseFileName;
                                          final String selectedFileDescription;

                                          baseFileName = Helper.getBaseFileName(selectedFile.getName());

                                          updateChosenFile(selectedFile);

                                          // update TEXT_FIELD_FILENAME_OUTPUT if blank
                                          if(TEXT_FIELD_FILENAME_OUTPUT.getText()
                                                                       .isBlank())
                                          {
                                              TEXT_FIELD_FILENAME_OUTPUT.setText(baseFileName);
                                          }

                                          // update select button text
                                          buttonFileChooser.setText("Selected: " + selectedFile.getName());

                                          // get the name of the description of the file type
                                          selectedFileDescription = fileChooser.getSelectedExtensionFilter()
                                                                               .getDescription();

                                          // toggle different GUI groups depending on which filetype group was selected
                                          if(selectedFileDescription.equals(FILE_DESCRIPTION_VIDEO))
                                          {
                                              SetVBox(NODES_VIDEO);
                                          }
                                          else if(selectedFileDescription.equals(FILE_DESCRIPTION_AUDIO))
                                          {
                                              SetVBox(NODES_AUDIO);
                                          }
                                      });

        // setup grid pane for file output nodes
        gridPaneFileOutput = new GridPane();
        gridPaneFileOutput.setHgap(10);
        gridPaneFileOutput.setAlignment(Pos.CENTER);
        NODES_CONSTANT_TOP.add(gridPaneFileOutput);

        // text field input for output name
        TEXT_FIELD_FILENAME_OUTPUT.setPromptText("Output filename");

        // add the text field to the left of the grid pane
        gridPaneFileOutput.getChildren()
                          .addFirst(TEXT_FIELD_FILENAME_OUTPUT);
        GridPane.setRowIndex(TEXT_FIELD_FILENAME_OUTPUT,
                             0);
        GridPane.setColumnIndex(TEXT_FIELD_FILENAME_OUTPUT,
                                0);

        // add the button to the right of the grid pane
        BUTTON_FILE_DESTINATION_CHOOSER = new Button("Select destination");
        gridPaneFileOutput.getChildren()
                          .addFirst(BUTTON_FILE_DESTINATION_CHOOSER);
        GridPane.setRowIndex(BUTTON_FILE_DESTINATION_CHOOSER,
                             0);
        GridPane.setColumnIndex(BUTTON_FILE_DESTINATION_CHOOSER,
                                1);

        // handle logic when clicking the file destination chooser button
        BUTTON_FILE_DESTINATION_CHOOSER.setOnAction(actionEvent ->
                                                    {
                                                        // Directory the user selected
                                                        final File selectedDir;
                                                        selectedDir = dirChooser.showDialog(mainStage);

                                                        if(selectedDir == null)
                                                        {
                                                            return;
                                                        }

                                                        updateChosenDir(selectedDir);

                                                        // update select button text
                                                        BUTTON_FILE_DESTINATION_CHOOSER.setText("Selected: " + selectedDir.getName());
                                                    });

        // call file-type-group-specific GUI setup methods
        setupVideo();
        setupAudio();


        // create title label
        LABEL_TITLE = new Label("JEFFmpeg");
        LABEL_TITLE.getStyleClass()
                   .add("neon-text");

        // setup terminal output
        SCROLL_PANE_TERMINAL  = new ScrollPane();
        LABEL_TERMINAL_OUTPUT = new Label("Terminal output will appear here");
        SCROLL_PANE_TERMINAL.setContent(LABEL_TERMINAL_OUTPUT);

        // set terminal output dimensions and alignment
        SCROLL_PANE_TERMINAL.setMaxHeight(100);
        SCROLL_PANE_TERMINAL.setMaxWidth(400);
        LABEL_TERMINAL_OUTPUT.setAlignment(Pos.TOP_LEFT);

        // scroll to the bottom of the terminal scroll pane
        SCROLL_PANE_TERMINAL.setVvalue(0.0);

        // add terminal to bottom of all screens
        NODES_CONSTANT_BOTTOM.add(SCROLL_PANE_TERMINAL);

        // setup the neon glow
        applyNeonGlow(LABEL_TITLE);

        // initialize Vbox container that holds everything for the scene
        VBOX_CONTAINER = new VBox(12);
        VBOX_CONTAINER.setAlignment(Pos.CENTER);
        VBOX_CONTAINER.getStyleClass()
                      .add("white-box");

        // add initial nodes to be displayed
        VBOX_CONTAINER.getChildren()
                      .add(LABEL_TITLE);
        VBOX_CONTAINER.getChildren()
                      .addAll(NODES_CONSTANT_TOP);
        VBOX_CONTAINER.getChildren()
                      .addAll(NODES_CONSTANT_BOTTOM);

        // setup layout main
        layoutMain = new VBox(PADDING_PX,
                              VBOX_CONTAINER);
        layoutMain.setAlignment(Pos.CENTER);
        layoutMain.getStyleClass()
                  .add("vbox");

        // setup  scene
        scene = new Scene(layoutMain,
                          STAGE_WIDTH_PX,
                          STAGE_HEIGHT_PX);

        // use custom  CSS
        scene.getStylesheets()
             .add(Objects.requireNonNull(getClass().getResource("style.css"))
                         .toExternalForm());

        // scale all elements inside main layout without scaling
        // elements like combo box popup (making it unscrollable)
        layoutMain.setScaleX(1.5);
        layoutMain.setScaleY(1.5);

        // set min widths (these were eye-balled)
        mainStage.setMinWidth(615);
        mainStage.setMinHeight(350);

        // scene setup and display
        mainStage.setTitle("JEFFmpeg");
        mainStage.setScene(scene);
        mainStage.setOpacity(1.0);
        mainStage.show();
    }

    /**
     * Adds a line to the terminal output in the GUI.
     *
     * @param terminalOutputToAdd String terminal output to add
     */
    public static void addTerminalOutput(final String terminalOutputToAdd)
    {
        final String originalLabelTerminalOutputText;
        originalLabelTerminalOutputText = LABEL_TERMINAL_OUTPUT.getText();

        LABEL_TERMINAL_OUTPUT.setText(originalLabelTerminalOutputText + terminalOutputToAdd);

        // tell the terminal what its size is
        SCROLL_PANE_TERMINAL.layout();

        // scroll to bottom of scroll pane to see live terminal
        SCROLL_PANE_TERMINAL.setVvalue(1.0);
    }

    /**
     * Sets up video compression/conversion options.
     */
    private static void setupVideo()
    {
        final GridPane     gridPaneVideoCompress;
        final List<String> fileTypesVideoTrimmed;
        final HBox         hBoxFiletypeVideo;
        final TextField    textFieldTargetMBVideo;


        // setup gird pane for video compression nodes
        gridPaneVideoCompress = new GridPane();
        gridPaneVideoCompress.setHgap(10);
        gridPaneVideoCompress.setAlignment(Pos.CENTER);
        NODES_VIDEO.add(new Label("\nCompression"));
        NODES_VIDEO.add(gridPaneVideoCompress);

        // setup button to click to compress video
        BUTTON_COMPRESS_VIDEO = new Button("Enter target MB");
        gridPaneVideoCompress.getChildren()
                             .addFirst(BUTTON_COMPRESS_VIDEO);
        BUTTON_COMPRESS_VIDEO.getStyleClass()
                             .add("compress-video");

        // place compress video button on the right in the grid pane
        GridPane.setRowIndex(BUTTON_COMPRESS_VIDEO,
                             0);
        GridPane.setColumnIndex(BUTTON_COMPRESS_VIDEO,
                                1);

        // when compress video button is clicked, compress the file
        BUTTON_COMPRESS_VIDEO.setOnAction(actionEvent -> compressFile());

        // start disabled, since the target MB text field will start blank
        BUTTON_COMPRESS_VIDEO.setDisable(true);


        // force the field to be numeric only
        textFieldTargetMBVideo = new TextField("");
        textFieldTargetMBVideo.setPromptText("Target MB");
        textFieldTargetMBVideo.getStyleClass()
                              .add("text-field");
        textFieldTargetMBVideo.textProperty()
                              .addListener(new ChangeListener<>()
                              {
                                  @Override
                                  public void changed(final ObservableValue<? extends String> observable,
                                                      final String oldValue,
                                                      String newValue)
                                  {
                                      final String targetMB;
                                      targetMB = textFieldTargetMBVideo.getText();

                                      if(!newValue.matches("\\d*"))
                                      {
                                          textFieldTargetMBVideo.setText(newValue.replaceAll("\\D",
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
                                          BUTTON_COMPRESS_VIDEO.setText("Start compress (aiming for " + targetMB + "MB)");

                                      }

                                      updateCompressionSize(targetMB);
                                  }
                              });

        // make the target MB text field to the left
        gridPaneVideoCompress.getChildren()
                             .addFirst(textFieldTargetMBVideo);
        GridPane.setRowIndex(textFieldTargetMBVideo,
                             0);
        GridPane.setColumnIndex(textFieldTargetMBVideo,
                                0);

        // setup convert video file types. remove leading character from filetypes
        fileTypesVideoTrimmed = Helper.removeFirstCharacters(SKIP_FIRST,
                                                             FILE_TYPES_VIDEO);
        COMBO_BOX_VIDEO_FILETYPES.getItems()
                                 .addAll(fileTypesVideoTrimmed);
        COMBO_BOX_VIDEO_FILETYPES.setPromptText("Select output video filetype");

        // convert video file types combobox visuals
        COMBO_BOX_VIDEO_FILETYPES.setMaxWidth(200);
        COMBO_BOX_VIDEO_FILETYPES.setPrefWidth(200);

        // when a combobox selection is made
        COMBO_BOX_VIDEO_FILETYPES.getSelectionModel()
                                 .selectedItemProperty()
                                 .addListener(event ->
                                              {
                                                  final String newFileType;

                                                  // enable button to convert video
                                                  BUTTON_CONVERT_FILETYPES_VIDEO.setDisable(false);

                                                  // get the String for the file extension we're converting to
                                                  newFileType = COMBO_BOX_VIDEO_FILETYPES.getSelectionModel()
                                                                                         .selectedItemProperty()
                                                                                         .get();

                                                  // set the convert text to "Convert to {filetype to convert to}"
                                                  BUTTON_CONVERT_FILETYPES_VIDEO.setText("Convert to " + newFileType);
                                              });

        // setup button to begin converting file types
        BUTTON_CONVERT_FILETYPES_VIDEO = new Button("Select Converting Filetype");

        // when begin converting button is clicked, begin converting the file to the new file type
        BUTTON_CONVERT_FILETYPES_VIDEO.setOnAction(actionEvent -> convertFile(COMBO_BOX_VIDEO_FILETYPES.getValue()));

        // by default the begin converting button is disabled since by default there is no valid selection made in the
        // new file type selection ComboBox
        BUTTON_CONVERT_FILETYPES_VIDEO.setDisable(true);

        // Create HBox to hold combobox and button side by side
        hBoxFiletypeVideo = new HBox(10);
        hBoxFiletypeVideo.setAlignment(Pos.CENTER);
        hBoxFiletypeVideo.getChildren()
                         .addAll(COMBO_BOX_VIDEO_FILETYPES,
                                 BUTTON_CONVERT_FILETYPES_VIDEO);

        // add a label to avoid confusion of sections
        NODES_VIDEO.add(new Label("\nConversion"));
        NODES_VIDEO.add(hBoxFiletypeVideo);


    }

    /**
     * Sets up audio compression/conversion options.
     */
    private static void setupAudio()
    {
        final GridPane     gridPaneAudioCompress;
        final TextField    textFieldTargetMBAudio;
        final List<String> fileTypesAudioTrimmed;
        final HBox         hBoxFiletypeAudio;

        // setup visual grid pane to organize audio compression next to each other
        gridPaneAudioCompress = new GridPane();
        gridPaneAudioCompress.setHgap(10);
        gridPaneAudioCompress.setAlignment(Pos.CENTER);
        NODES_AUDIO.add(gridPaneAudioCompress);

        // setup button to compress audio
        BUTTON_COMPRESS_AUDIO = new Button("Enter target MB");

        // button to compress is disabled by default, since by default the input field for targeted MB siz
        // is blank, which is an invalid size.
        BUTTON_COMPRESS_AUDIO.setDisable(true);

        // when conversion button is clicked, begin conversion
        BUTTON_COMPRESS_AUDIO.setOnAction(actionEvent -> compressFile());

        // add compress audio button to the left in the grid pane
        gridPaneAudioCompress.getChildren()
                             .addFirst(BUTTON_COMPRESS_AUDIO);
        GridPane.setRowIndex(BUTTON_COMPRESS_AUDIO,
                             0);
        GridPane.setColumnIndex(BUTTON_COMPRESS_AUDIO,
                                1);

        // setup textfield for targeted audio compression MB size
        textFieldTargetMBAudio = new TextField("");
        textFieldTargetMBAudio.setPromptText("Target MB");
        textFieldTargetMBAudio.getStyleClass()
                              .add("text-field");

        // force the field to be numeric only
        textFieldTargetMBAudio.textProperty()
                              .addListener(new ChangeListener<>()
                              {
                                  @Override
                                  public void changed(final ObservableValue<? extends String> observable,
                                                      final String oldValue,
                                                      String newValue)
                                  {
                                      final String targetMB;
                                      targetMB = textFieldTargetMBAudio.getText();

                                      if(!newValue.matches("\\d*"))
                                      {
                                          textFieldTargetMBAudio.setText(newValue.replaceAll("\\D",
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
                                          BUTTON_COMPRESS_AUDIO.setText("Start compress (aiming for " + targetMB + "MB)");

                                      }

                                      updateCompressionSize(targetMB);
                                  }
                              });

        // add target MB to left side of the grid pane
        gridPaneAudioCompress.getChildren()
                             .addFirst(textFieldTargetMBAudio);
        GridPane.setRowIndex(textFieldTargetMBAudio,
                             0);
        GridPane.setColumnIndex(textFieldTargetMBAudio,
                                0);

        // setup for file type conversion.
        // use the audio file types without the leading character in the combo box to select output file type
        fileTypesAudioTrimmed = Helper.removeFirstCharacters(SKIP_FIRST,
                                                             FILE_TYPES_AUDIO);
        COMBO_BOX_AUDIO_FILETYPES.getItems()
                                 .addAll(fileTypesAudioTrimmed);

        // setup visuals for the combobox
        COMBO_BOX_AUDIO_FILETYPES.setMaxWidth(200);
        COMBO_BOX_AUDIO_FILETYPES.setPrefWidth(200);

        // when the combo box has a new selection
        COMBO_BOX_AUDIO_FILETYPES.getSelectionModel()
                                 .selectedItemProperty()
                                 .addListener(event ->
                                              {
                                                  final String convertType;

                                                  // enable the begin converting button since we now have a valid input
                                                  BUTTON_CONVERT_FILETYPES_AUDIO.setDisable(false);

                                                  // set the convert text to "Convert to {filetype to convert to}
                                                  convertType = COMBO_BOX_AUDIO_FILETYPES.getSelectionModel()
                                                                                         .selectedItemProperty()
                                                                                         .get();
                                                  BUTTON_CONVERT_FILETYPES_AUDIO.setText("Convert to " + convertType);
                                              });

        // setup button to begin converting
        BUTTON_CONVERT_FILETYPES_AUDIO = new Button("Select Converting Filetype");

        // visual changes for button to begin converting
        BUTTON_CONVERT_FILETYPES_AUDIO.setMaxWidth(150);
        BUTTON_CONVERT_FILETYPES_AUDIO.setPrefWidth(150);

        // when button to begin converting is pressed, try to start converting
        BUTTON_CONVERT_FILETYPES_AUDIO.setOnAction(actionEvent -> convertFile(COMBO_BOX_AUDIO_FILETYPES.getValue()));

        /*
         * keep button to begin converting disabled by default, since the file type to convert to combobox is
         * empty by default
         */
        BUTTON_CONVERT_FILETYPES_AUDIO.setDisable(true);

        // setup hbox for file type audio
        hBoxFiletypeAudio = new HBox(10);
        hBoxFiletypeAudio.setAlignment(Pos.CENTER);

        // add combobox and button to begin converting to the hbox
        hBoxFiletypeAudio.getChildren()
                         .addAll(COMBO_BOX_AUDIO_FILETYPES,
                                 BUTTON_CONVERT_FILETYPES_AUDIO);
        NODES_AUDIO.add(hBoxFiletypeAudio);
    }

    /**
     * Sets up VBox of nodes.
     * Disables all currently active nodes except the constants and the passed nodes.
     *
     * @param nodes List of nodes to put in VBox.
     */
    private static void SetVBox(final List<Node> nodes)
    {
        // remove all existing nodes in the vbox
        VBOX_CONTAINER.getChildren()
                      .clear();

        // add the constant title label
        VBOX_CONTAINER.getChildren()
                      .add(LABEL_TITLE);

        // add the constants for the top
        VBOX_CONTAINER.getChildren()
                      .addAll(NODES_CONSTANT_TOP);

        // add the passed nodes to display
        VBOX_CONTAINER.getChildren()
                      .addAll(nodes);

        // add the constants for the bottom
        VBOX_CONTAINER.getChildren()
                      .addAll(NODES_CONSTANT_BOTTOM);
    }

    /**
     * Applies neon effect to the title label.
     *
     * @param label Label to make neon.
     */
    private static void applyNeonGlow(final Label label)
    {
        // create an initial glow DropShadow
        final DropShadow glow;
        glow = new DropShadow();

        // set glow parameters
        glow.setRadius(30);
        glow.setSpread(0.6);

        // set colour to pink
        glow.setColor(Color.web("#ff005e"));

        // set glow effect
        label.setEffect(glow);

        // make text white (readable)
        label.setTextFill(Color.WHITE);

        // timeline to animate the glow color between pink and blue
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

    /**
     * Updates the chosen file to compress/convert.
     *
     * @param file File to compress/convert.
     */
    private static void updateChosenFile(final File file)
    {
        fileToUse = file;
    }

    /**
     * Updates the chosen directory.
     *
     * @param dir File directory to store result in.
     */
    private static void updateChosenDir(final File dir)
    {
        fileDestinationDirectory = dir;
    }

    /**
     * Updates the targeted compression size.
     *
     * @param size String file size to aim for.
     */
    private static void updateCompressionSize(final String size)
    {
        compressionSizeMB = size;
    }

    /**
     * Compresses a file.
     */
    private static void compressFile()
    {
        // if the user tried to compress a file without selecting an output directory, let them know and return.
        if(fileDestinationDirectory == null)
        {
            BUTTON_FILE_DESTINATION_CHOOSER.setText("Choose destination directory!");
            BUTTON_FILE_DESTINATION_CHOOSER.setStyle("-fx-fill-color: FF0000;");
            return;
        }

        // setup options
        final String[] options;
        options = new String[] {compressionSizeMB};

        // try to compress
        try
        {
            final String outputFileName;
            outputFileName = TEXT_FIELD_FILENAME_OUTPUT.getText();
            Helper.getBaseFileName(outputFileName);

            TerminalExecutor.compressFile(fileToUse,
                                          fileDestinationDirectory,
                                          outputFileName,
                                          options);
        }
        catch(final Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Converts a file to a given file type.
     *
     * @param fileType String file extension to change to
     */
    private static void convertFile(final String fileType)
    {
        // if the user tried to convert a file without selecting an output directory, let them know and return.
        if(fileDestinationDirectory == null)
        {
            BUTTON_FILE_DESTINATION_CHOOSER.setText("Choose destination directory!");
            return;
        }

        // try to convert file
        try
        {
            final String outputFileName;
            outputFileName = TEXT_FIELD_FILENAME_OUTPUT.getText();
            Helper.getBaseFileName(outputFileName);

            TerminalExecutor.convertFile(fileToUse,
                                         fileDestinationDirectory,
                                         fileType);
        }
        catch(final Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Entry point for the program.
     *
     * @param args unused
     */
    public static void main(final String[] args)
    {
        launch(args);
    }
}
