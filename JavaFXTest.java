import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * GUI entry point.
 *
 * @author Szymon Zemojtel
 * @author alex-hidalgo
 * @version 1
 */
public final class JavaFXTest
        extends Application
{

    private static final boolean isWindows = System.getProperty("os.name")
                                                   .toLowerCase()
                                                   .startsWith("windows");
    private static final boolean  isWindows              = System.getProperty("os.name")
                                                                 .toLowerCase()
                                                                 .startsWith("windows");
    private static       String   FILE_DESCRIPTION_VIDEO = "Video Files";
    private static       String   FILE_DESCRIPTION_IMAGE = "Image Files";
    private static       String   FILE_DESCRIPTION_AUDIO = "Audio FIles";
    private static       String[] FILE_TYPES_VIDEO       = {"*.mp4",
                                                            "*.m4a",
                                                            "*.mov",
                                                            "*.avi",
                                                            "*.wmv",
                                                            "*.webm",
                                                            "*.gif"};

    private static String[] FILE_TYPES_IMAGE = {"*.png",
                                                "*.jpg"};
    private static String[] FILE_TYPES_AUDIO = {"*.wav",
                                                "*.mp3",
                                                "*.aac"};

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
        final VBox   layout;
        final Button buttonFileChooser;
        final Scene  scene;

        // START OS Check testing

        File location = new File(System.getProperty("user.dir"));
        System.out.println(location);

        if(isWindows)
        {
            runCommand(location,
                       "dir");
        }
        else
        {
            runCommand(location,
                       "ls");
        }
        // END OS Check testing

        // Test label.
        label = new Label("Hello JavaFX!");

        // File Chooser Setup
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters()
                   .addAll(new FileChooser.ExtensionFilter(FILE_DESCRIPTION_VIDEO,
                                                           FILE_TYPES_VIDEO),
                           new FileChooser.ExtensionFilter(FILE_DESCRIPTION_IMAGE,
                                                           FILE_TYPES_IMAGE),
                           new FileChooser.ExtensionFilter(FILE_DESCRIPTION_AUDIO,
                                                           FILE_TYPES_AUDIO));

        // Button File Selector setup. Add button event to open file selection.
        buttonFileChooser = new Button("Select a file");
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
                                              }

                                              //                                            label.setVisible(false);
                                              label.setDisable(true);
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

    public static void runCommand(final File whereToRun,
                                  final String command)
    throws
    IOException,
    InterruptedException
    {
        System.out.println("Running in: " + whereToRun);
        System.out.println("Command: " + command);

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(whereToRun);

        if(isWindows)
        {
            builder.command("cmd.exe",
                            "/c",
                            command);
        }
        else
        {
            builder.command("sh",
                            "-c",
                            command);
        }

        Process process = builder.start();

        OutputStream outputStream = process.getOutputStream();
        InputStream  inputStream  = process.getInputStream();
        InputStream  errorStream  = process.getErrorStream();

        printStream(inputStream);
        printStream(errorStream);

        boolean isFinished = process.waitFor(30,
                                             TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if(!isFinished)
        {
            process.destroyForcibly();
        }
    }

    private static void printStream(InputStream inputStream)
    throws
    IOException
    {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)))
        {
            String line;
            while((line = bufferedReader.readLine()) != null)
            {
                System.out.println(line);
            }
        }
    }
}
