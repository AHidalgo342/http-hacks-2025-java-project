import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;

/**
 * Terminal wrapper class.
 *
 * @author Marcy Ordinario
 * @author Alex Hidalgo
 * @version 1.1
 */
public class TerminalExecutor
{
    public static final int  PREFIX_MULTIPLIER_KILO = 1000;
    public static final int  PREFIX_MULTIPLIER_MEGA = 1000000;
    public static final long BYTES_TO_BITS          = 8L;

    /**
     * Converts a file from one type to another.
     *
     * @param source      File to convert.
     * @param destination Destination of file.
     */
    public static void convertFile(final File source,
                                   final File destination,
                                   final String fileType)
    {
        final StringBuilder terminalCommandBuilder;
        terminalCommandBuilder = new StringBuilder();

        terminalCommandBuilder.append("ffmpeg -y ");
        terminalCommandBuilder.append("-i \"");
        terminalCommandBuilder.append(source.getAbsolutePath());
        terminalCommandBuilder.append("\"");
        terminalCommandBuilder.append(" -c:v copy -b:a 320k -ab 192k ");
        terminalCommandBuilder.append("\"");
        terminalCommandBuilder.append(destination.getAbsolutePath());
        terminalCommandBuilder.append(File.separator);
        terminalCommandBuilder.append(Helper.getBaseFileName(source.getName()));
        terminalCommandBuilder.append(fileType);
        terminalCommandBuilder.append("\"");

        callTerminal(terminalCommandBuilder);
    }

    /**
     * Compresses a file.
     * <p>
     * Possible options:<br>
     * - Target File size<br>
     * Examples: 30M, 10K, 50G<br>
     * <p>
     * Full Example:
     * {@code input.webm compressed.mp4 15M 24}
     * <p>
     * Outputs an mp4 with file size 15 Mb and a framerate of 24fps,
     * Converts the input file to the output format on the fly.
     * <p>
     * We should always have at least one option, that being the target size.
     * Framerate is optional.
     *
     * @param source         File to convert
     * @param destination    Destination of file
     * @param outputFilename name of the output file
     * @param options        Optional arguments
     * @throws IOException          If an IO exception happens
     * @throws InterruptedException If the process is interupted
     */
    public static void compressFile(final File source,
                                    final File destination,
                                    final String outputFilename,
                                    final String[] options)
    throws
    IOException,
    InterruptedException
    {
        final long bitrateKBPS = getBitrateKBPS(source,
                                                options);
        System.out.println(bitrateKBPS);

        if(isVideo(source))
        {
            System.out.println("Video");
            compressVideo(source,
                          destination,
                          outputFilename,
                          options,
                          bitrateKBPS);
        }
        else
        {
            System.out.println("Audio");
            compressAudio(source,
                          destination,
                          outputFilename,
                          bitrateKBPS);
        }
    }

    /**
     * Returns true if the given file is in a supported
     * video format, else false.
     *
     * @param source File to check extension type of
     * @return true if file is in a supported video format,
     * else false
     */
    private static boolean isVideo(final File source)
    {
        boolean video = false;

        if(source == null)
        {
            return false;
        }

        for(final String it : Constants.FILE_TYPES_VIDEO)
        {
            final String fileExtension;
            fileExtension = it.substring(1);

            System.out.println("File extension: " + fileExtension);

            if(source.toString()
                     .endsWith(fileExtension))
            {
                video = true;
                break;
            }
        }
        return video;
    }

    /**
     * Generate the FFMPEG command to compress an audio file.
     *
     * @param source         Source file
     * @param destination    Destination Path
     * @param outputFileName output file name
     * @param bitrateKBPS    file target compression bitrate
     */
    private static void compressAudio(final File source,
                                      final File destination,
                                      final String outputFileName,
                                      final long bitrateKBPS)
    {
        final StringBuilder terminalCommandBuilder;
        terminalCommandBuilder = new StringBuilder();

        terminalCommandBuilder.append("ffmpeg -y -flush_packets 1 -i \"");
        terminalCommandBuilder.append(source.getAbsolutePath());
        // Audio bitrate flag
        terminalCommandBuilder.append("\" -b:a ");
        // Audio bitrate specified
        terminalCommandBuilder.append(bitrateKBPS);
        terminalCommandBuilder.append("k -maxrate ");
        terminalCommandBuilder.append(bitrateKBPS);
        terminalCommandBuilder.append("k -bufsize ");
        terminalCommandBuilder.append(bitrateKBPS);
        terminalCommandBuilder.append("k ");
        // Specify mono
        terminalCommandBuilder.append("-ac 1 ");

        terminalCommandBuilder.append("\"");
        terminalCommandBuilder.append(destination.getAbsolutePath());
        terminalCommandBuilder.append(File.separator);
        terminalCommandBuilder.append(outputFileName);
        terminalCommandBuilder.append(Helper.getFileType(source.getName()));
        terminalCommandBuilder.append("\"");

        callTerminal(terminalCommandBuilder);
    }

    /**
     * Generate the FFMPEG command to compress an audio file.
     *
     * @param source         Source file
     * @param destination    Destination Path
     * @param outputFilename output file name
     * @param options        optional arguments
     * @param bitrateKBPS    file target compression bitrate
     */
    private static void compressVideo(final File source,
                                      final File destination,
                                      final String outputFilename,
                                      final String[] options,
                                      final long bitrateKBPS)
    {
        final StringBuilder terminalCommandBuilder;
        terminalCommandBuilder = new StringBuilder();

        terminalCommandBuilder.append("ffmpeg -y -flush_packets 1 -i \"");
        terminalCommandBuilder.append(source.getAbsolutePath());
        // Audio bitrate 48k
        terminalCommandBuilder.append("\" -b:a 48k -b:v ");
        // Video bitrate specified
        terminalCommandBuilder.append(bitrateKBPS);
        terminalCommandBuilder.append("k ");
        // UNUSED, CODE DOES NOT ALLOW FOR FRAME RATE ANYMORE
        if(options.length > 1)
        {
            // Specify frame rate
            terminalCommandBuilder.append("-r ");
            terminalCommandBuilder.append(options[1]);
            // Specify mono
            terminalCommandBuilder.append(" -ac 1 ");
        }

        terminalCommandBuilder.append("\"");
        terminalCommandBuilder.append(destination.getAbsolutePath());
        terminalCommandBuilder.append(File.separator);
        terminalCommandBuilder.append(outputFilename);
        terminalCommandBuilder.append(Helper.getFileType(source.getName()));
        terminalCommandBuilder.append("\"");

        callTerminal(terminalCommandBuilder);
    }

    /**
     * Get the Bitrate of the given file (src) in kbps.
     *
     * @param source  source file
     * @param options optional arguments
     * @return a target compression bitrate as long
     * @throws IOException          if an IO exception occurs
     * @throws InterruptedException if the process is interrupted
     */
    private static long getBitrateKBPS(final File source,
                                       final String[] options)
    throws
    IOException,
    InterruptedException
    {
        final String fileLength;
        final int    targetFileSizeMB;
        final long   targetSizeBits;
        final int    fileLengthSeconds;

        fileLength        = getFileLength(source);
        targetFileSizeMB  = Integer.parseInt(options[0]);
        targetSizeBits    = targetFileSizeMB * PREFIX_MULTIPLIER_MEGA * BYTES_TO_BITS;
        fileLengthSeconds = (int) Math.ceil(Double.parseDouble(fileLength));

        System.out.println("Target file size: " + targetSizeBits + " bits");
        System.out.println("File size: " + fileLengthSeconds + " seconds");

        return targetSizeBits / fileLengthSeconds / PREFIX_MULTIPLIER_KILO;
    }

    /**
     * Get the file length as a {@code HH:MM:SS:mmm} formatted timestamp.
     *
     * @param source source file
     * @return {@code HH:MM:SS:mmm} timestamp
     * @throws IOException          if an IO exception occurs
     * @throws InterruptedException if the process is interrupted
     */
    private static String getFileLength(final File source)
    throws
    IOException,
    InterruptedException
    {
        String fileLengthVerbose = Terminal.runCommand("ffprobe -v quiet -i \"" + source.getAbsolutePath() + "\" -show_entries format=duration -of csv=\"p=0\"");
        System.out.println(fileLengthVerbose);
        return fileLengthVerbose;
    }

    private static void callTerminal(final StringBuilder terminalCommandBuilder)
    {
        final Task<Void> task;

        task = new Task<>()
        {
            @Override
            public Void call()
            {
                try
                {
                    Terminal.runFFmpeg(terminalCommandBuilder.toString());
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }

                return null;
            }
        };

        new Thread(task).start();
    }
}
