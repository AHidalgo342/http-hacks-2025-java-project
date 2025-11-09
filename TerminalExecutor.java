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
     * @param src File to convert
     * @param dst Destination of file
     */
    public static void convertFile(final File src,
                                   final File dst,
                                   final String fileType)
    {
        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("ffmpeg -y ");
        sb.append("-i \"");
        sb.append(src.getAbsolutePath());
        sb.append("\"");
        sb.append(" -c:v copy -b:a 320k -ab 192k ");
        sb.append("\"");
        sb.append(dst.getAbsolutePath());
        sb.append(File.separator);
        sb.append(Helper.getBaseFileName(src.getName()));
        sb.append(fileType);
        sb.append("\"");

        callTerminal(sb);
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
     * @param src     File to convert
     * @param dst     Destination of file
     * @param name    name of the output file
     * @param options Optional arguments
     * @throws IOException          If an IO exception happens
     * @throws InterruptedException If the process is interupted
     */
    public static void compressFile(final File src,
                                    final File dst,
                                    final String name,
                                    final String[] options)
    throws
    IOException,
    InterruptedException
    {
        long bitrateKBPS = getBitrateKBPS(src,
                                          options);
        System.out.println(bitrateKBPS);

        if(isVideo(src))
        {
            System.out.println("Video");
            compressVideo(src,
                          dst,
                          name,
                          options,
                          bitrateKBPS);
        }
        else
        {
            System.out.println("Audio");
            compressAudio(src,
                          dst,
                          name,
                          bitrateKBPS);
        }
    }

    /**
     * Returns true if the given file is in a supported
     * video format, else false.
     *
     * @param src File to check extension type of
     * @return true if file is in a supported video format,
     * else false
     */
    private static boolean isVideo(File src)
    {
        boolean video = false;

        if(src == null)
        {
            return false;
        }

        for(final String it : FFmpegGUI.FILE_TYPES_VIDEO)
        {
            final String fileExtension;
            fileExtension = it.substring(1);

            System.out.println("File extension: " + fileExtension);

            if(src.toString()
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
     * @param src         Source file
     * @param dst         Destination Path
     * @param name        output file name
     * @param bitrateKBPS file target compression bitrate
     */
    private static void compressAudio(File src,
                                      File dst,
                                      String name,
                                      long bitrateKBPS)
    {
        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("ffmpeg -y -flush_packets 1 -i \"");
        sb.append(src.getAbsolutePath());
        // Audio bitrate flag
        sb.append("\" -b:a ");
        // Audio bitrate specified
        sb.append(bitrateKBPS);
        sb.append("k -maxrate ");
        sb.append(bitrateKBPS);
        sb.append("k -bufsize ");
        sb.append(bitrateKBPS);
        sb.append("k ");
        // Specify mono
        sb.append("-ac 1 ");

        sb.append("\"");
        sb.append(dst.getAbsolutePath());
        sb.append(File.separator);
        sb.append(name);
        sb.append(Helper.getFileType(src.getName()));
        sb.append("\"");

        callTerminal(sb);
    }

    /**
     * Generate the FFMPEG command to compress an audio file.
     *
     * @param src         Source file
     * @param dst         Destination Path
     * @param name        output file name
     * @param options     optional arguments
     * @param bitrateKBPS file target compression bitrate
     */
    private static void compressVideo(File src,
                                      File dst,
                                      String name,
                                      String[] options,
                                      long bitrateKBPS)
    {
        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("ffmpeg -y -flush_packets 1 -i \"");
        sb.append(src.getAbsolutePath());
        // Audio bitrate 48k
        sb.append("\" -b:a 48k -b:v ");
        // Video bitrate specified
        sb.append(bitrateKBPS);
        sb.append("k ");
        // UNUSED, CODE DOES NOT ALLOW FOR FRAME RATE ANYMORE
        if(options.length > 1)
        {
            // Specify frame rate
            sb.append("-r ");
            sb.append(options[1]);
            // Specify mono
            sb.append(" -ac 1 ");
        }

        sb.append("\"");
        sb.append(dst.getAbsolutePath());
        sb.append(File.separator);
        sb.append(name);
        sb.append(Helper.getFileType(src.getName()));
        sb.append("\"");

        callTerminal(sb);
    }

    /**
     * Get the Bitrate of the given file (src) in kbps.
     *
     * @param src     source file
     * @param options optional arguments
     * @return a target compression bitrate as long
     * @throws IOException          if an IO exception occurs
     * @throws InterruptedException if the process is interrupted
     */
    private static long getBitrateKBPS(File src,
                                       String[] options)
    throws
    IOException,
    InterruptedException
    {
        String fileLength = getFileLength(src);

        int targetFileSizeMB = Integer.parseInt(options[0]);

        long targetSizeBits = targetFileSizeMB * PREFIX_MULTIPLIER_MEGA * BYTES_TO_BITS;

        int fileLengthSeconds = (int) Math.ceil(Double.parseDouble(fileLength));

        System.out.println("Target file size: " + targetSizeBits + " bits");
        System.out.println("File size: " + fileLengthSeconds + " seconds");

        return targetSizeBits / fileLengthSeconds / PREFIX_MULTIPLIER_KILO;
    }

    /**
     * Get the file length as a {@code HH:MM:SS:mmm} formatted timestamp.
     * @param src source file
     * @return {@code HH:MM:SS:mmm} timestamp
     * @throws IOException if an IO exception occurs
     * @throws InterruptedException if the process is interrupted
     */
    private static String getFileLength(File src)
    throws
    IOException,
    InterruptedException
    {
        String fileLengthVerbose = Terminal.runCommand("ffprobe -v quiet -i \"" + src.getAbsolutePath() + "\" -show_entries format=duration -of csv=\"p=0\"");
        System.out.println(fileLengthVerbose);
        return fileLengthVerbose;
    }

    private static void callTerminal(StringBuilder sb)
    {
        Task<Void> task = new Task<>()
        {
            @Override
            public Void call()
            {
                try
                {
                    Terminal.runFFmpeg(sb.toString());
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
