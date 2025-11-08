import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Contains functionality for running terminal commands.
 *
 * @author Alex Hidalgo
 * @version 1.0
 */
public class Terminal {
    private static final boolean isWindows = System.getProperty("os.name")
                                                   .toLowerCase()
                                                   .startsWith("windows");

    public static void runCommand(final String command)
            throws
            IOException,
            InterruptedException
    {
        File location = new File(System.getProperty("user.dir"));
        System.out.println(location);

        System.out.println("Running in: " + location);
        System.out.println("Command: " + command);

        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(location);

        if(isWindows)
        {
            builder.command("cmd.exe", "/c", command);
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
