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
    private enum streamType {INPUT, ERROR}
    private static final boolean isWindows = System.getProperty("os.name")
                                                   .toLowerCase()
                                                   .startsWith("windows");

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

    public static String runFFmpeg(final String command)
    throws
    IOException,
    InterruptedException
    {
        return runCommand(command,
                          streamType.ERROR,
                          false);
    }

    public static String runFFmpeg(final String command,
                                   final boolean last)
    throws
    IOException,
    InterruptedException
    {
        return runCommand(command,
                          streamType.ERROR,
                          last);
    }

    private static String runCommand(final String command)
    throws
    IOException,
    InterruptedException
    {
        return runCommand(command,
                          streamType.INPUT,
                          false);
    }

    /**
     * Runs a command on the terminal. Also returns the
     * first line of output from the terminal.
     *
     * @param command String command to run
     * @return String first line of output; null if no output
     * @throws IOException          If an IO exception occurs
     * @throws InterruptedException If the process is interrupted
     */
    private static String runCommand(final String command,
                                     final streamType streamType,
                                     final boolean last)
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

        final OutputStream outputStream;
        final InputStream  inputStream;

        outputStream = process.getOutputStream();

        if(streamType == Terminal.streamType.INPUT)
        {
            inputStream = process.getInputStream();
        }
        else
        {
            inputStream = process.getErrorStream();
        }

        final String firstLnOutput;

        final String inputStreamStr;
        inputStreamStr = printStream(inputStream,
                                     last);

        firstLnOutput = inputStreamStr;

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
     * Prints the result of a terminal command to console.
     * Also returns the first line of output from the terminal.
     *
     * @param inputStream InputStream to print
     * @return String first line of output; null if no output
     * @throws IOException If an IO exception occurs
     */
    private static String printStream(InputStream inputStream,
                                      boolean last)
    throws
    IOException
    {
        final String            returnStr;
        final InputStreamReader inputStreamReader;
        inputStreamReader = new InputStreamReader(inputStream);

        try(BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
        {
            String line;

            line      = bufferedReader.readLine();
            if(!last)
            {
                returnStr = line;
                while((line = bufferedReader.readLine()) != null)
                {
                    FFMPEGGUI.addTerminalOutput(line + "\n");
                    System.out.println(line);
                }
            }
            else
            {
                String lastNonNullLine = null;
                while((line = bufferedReader.readLine()) != null)
                {
                    lastNonNullLine = line;
                    FFMPEGGUI.addTerminalOutput(line + "\n");
                    System.out.println(line);
                }
                returnStr = lastNonNullLine;
            }
            return returnStr;
        }
    }
}
