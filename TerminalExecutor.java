import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalTime;

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
    public static final int  PREFIX_MULTIPLIER_GIGA = 1000000000;
    public static final long BYTES_TO_BITS          = 8L;

    /**
     * Converts a file from one type to another.
     *
     * @param src File to convert
     * @param dst Destination of file
     */
    public static void convertFile(final File src,
                                   final File dst)
    {
        boolean isVideo = false;

        for(final String it : FFMPEGGUI.FILE_TYPES_VIDEO)
        {
            final String fileExtension;
            fileExtension = src.toString().substring(1);

            if(it.endsWith(fileExtension))
            {
                isVideo = true;
                break;
            }
        }

        try
        {
            if(isVideo)
            {
                Terminal.runCommand("ffmpeg -flush_packets 1 -i " + src + " -c copy -y " + dst);
            }
            else
            {
                Terminal.runCommand("ffmpeg -flush_packets 1 -i " + src + " -y " + dst);
            }
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Compresses a file.
     * <p>
     * Possible options:<br>
     * - Target File size<br>
     * Examples: 30M, 10K, 50G<br>
     * - Target Framerate:<br>
     * Example: 24, 10, 5
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
     * @param options Optional arguments
     */
    public static void compressFile(final File src,
                                    final File dst,
                                    final String[] options)
    throws
    IOException,
    InterruptedException
    {
        String fileLengthVerbose = Terminal.runCommand("ffmpeg -v quiet -stats -i " + src + " -f null -");
        String[] fileLengthRemovedFirstHalf = fileLengthVerbose.split("time=");
        String fileLengthTimeStamp = fileLengthRemovedFirstHalf[1].split(" bitrate")[0];

        // bitrate = target size / duration
        char targetSizePrefix = options[0].charAt(options[0].length() - 1);

        // get the first option and convert it to an integer, remove last character (SI prefix)
        int targetFileSize = Integer.parseInt(options[0]);

        long targetBitRate = targetFileSize * PREFIX_MULTIPLIER_MEGA * BYTES_TO_BITS;

        int fileLengthSeconds = LocalTime.parse(fileLengthTimeStamp)
                                         .toSecondOfDay();

        long bitrateKBPS = targetBitRate / fileLengthSeconds / PREFIX_MULTIPLIER_KILO;

        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("ffmpeg -avioflags direct -y -i ");
        sb.append(src.getAbsolutePath());
        // Audio bitrate 48k
        sb.append(" -b:a 48k -b:v ");
        // Video bitrate specified
        sb.append(bitrateKBPS);
        sb.append("k ");
        if(options.length > 1)
        {
            // Specify frame rate
            sb.append("-r ");
            sb.append(options[1]);
            // Specify mono
            sb.append(" -ac 1 ");
        }
        sb.append(dst.toString());
        sb.append(Terminal.directoryCharacter);
        sb.append(src.getName());

        // no idea if this works, regardless we ball.

        try
        {
            Terminal.runCommand(sb.toString());
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}
