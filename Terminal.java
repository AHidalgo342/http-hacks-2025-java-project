import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Contains functionality for running terminal commands.
 *
 * @author Alex Hidalgo
 * @author Marcy Ordinario
 * @version 1.0
 */
public class Terminal
{
    private static final boolean isWindows = System.getProperty("os.name")
                                                   .toLowerCase()
                                                   .startsWith("windows");

    /**
     * Runs a command on the terminal. Also returns the
     * first line of output from the terminal.
     *
     * @param command String command to run
     * @return String first line of output; null if no output
     * @throws IOException          If an IO exception occurs
     * @throws InterruptedException If the process is interrupted
     */
    public static String runCommand(final String command)
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

        final String firstLnOutput;
        final String inputStreamStr;
        final String errorStreamStr;
        inputStreamStr = printStream(inputStream);
        errorStreamStr = printStream(errorStream);

        if(errorStreamStr != null)
        {
            firstLnOutput = errorStreamStr;
        }
        else
        {
            firstLnOutput = inputStreamStr;
        }

        boolean isFinished = process.waitFor(30,
                                             TimeUnit.SECONDS);
        outputStream.flush();
        outputStream.close();

        if(!isFinished)
        {
            process.destroyForcibly();
        }

        return firstLnOutput;
    }

    /**
     * Returns true if FFmpeg is installed in the system,
     * else false.
     *
     * @return true if FFmpeg installed; else false
     */
    public static boolean FFmpegExists()
    {
        try
        {
            String whichResult;

            if(isWindows)
            {
                whichResult = runCommand("where ffmpeg");
                // Unsuccessful find starts with "INFO:"
                return !whichResult.contains("INFO:");
            }
            else
            {
                whichResult = runCommand("which ffmpeg");
                // Unsuccessful find starts with "which:"
                return !whichResult.contains("which:");
            }
        }
        catch(Exception e)
        {
            // If crash happens, assume FFmpeg doesn't exist
            return false;
        }
    }

    /**
     * Prints the result of a terminal command to console.
     * Also returns the first line of output from the terminal.
     *
     * @param inputStream InputStream to print
     * @return String first line of output; null if no output
     * @throws IOException If an IO exception occurs
     */
    private static String printStream(InputStream inputStream)
    throws
    IOException
    {
        final String returnStr;
        final InputStreamReader inputStreamReader;
        inputStreamReader = new InputStreamReader(inputStream);

        try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
        {
            String line;

            line = bufferedReader.readLine();
            System.out.println("a");
            returnStr = line;
            while((line = bufferedReader.readLine()) != null)
            {
                System.out.println(line);
            }
        }

        return returnStr;
    }
}
