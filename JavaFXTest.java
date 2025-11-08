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
                   .addAll(new FileChooser.ExtensionFilter("Video Files",
                                                           "*.mp4",
                                                           "*.m4a",
                                                           "*.mov",
                                                           "*.avi",
                                                           "*.wmv",
                                                           "*.webm",
                                                           "*.gif"),
                           new FileChooser.ExtensionFilter("Image Files",
                                                           "*.png",
                                                           "*.jpg"),
                           new FileChooser.ExtensionFilter("Audio Files",
                                                           "*.wav",
                                                           "*.mp3",
                                                           "*.aac"));

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
            builder.command("sh", "-c", command);
        }

        Process process = builder.start();

        OutputStream outputStream = process.getOutputStream();
        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        printStream(inputStream);
        printStream(errorStream);

        boolean isFinished = process.waitFor(30,
                                             TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if (!isFinished) {
            process.destroyForcibly();
        }
    }

    private static void printStream(InputStream inputStream)
    throws
    IOException
    {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
