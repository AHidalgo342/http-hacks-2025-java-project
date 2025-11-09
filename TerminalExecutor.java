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
                                   final Path dst)
    {
        boolean isVideo = false;

        for(String it : FFMPEGGUI.FILE_TYPES_VIDEO)
        {
            if(src.toString()
                  .matches(it))
            {
                isVideo = true;
                break;
            }
        }

        try
        {
            if(isVideo)
            {
                Terminal.runCommand("ffmpeg -i " + src + " -c copy " + dst);
            }
            else
            {
                Terminal.runCommand("ffmpeg -i " + src + " " + dst);
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
     * Possible options:
     * - Target File size
     * Examples: 30M, 10K, 50G
     * - Target Framerate:
     * Example: 24, 10, 5
     * <p>
     * Full Example:
     * input.webm compressed.mp4 15M 24
     * <p>
     * Outputs an mp4 with file size 15 Mb and a framerate of 24fps,
     * Converts the input file to the output format on the fly.
     * <p>
     * We should always have at least one option, that being the target size.
     *
     * @param src     File to convert
     * @param dst     Destination of file
     * @param options Optional arguments
     */
    public static void compressFile(final File src,
                                    final Path dst,
                                    final String[] options)
    throws
    IOException,
    InterruptedException
    {
        String fileLengthVerbose = Terminal.runCommand("ffmpeg -v quiet -stats -i " + src + " -f null -");

        String[] fileLengthRemovedFirstHalf = fileLengthVerbose.split("time=");

        String fileLengthTimeStamp = fileLengthRemovedFirstHalf[1].split(" bitrate")[0];

        //        bitrate = target size / duration
        char targetSizePrefix = options[0].charAt(options[0].length() - 1);

        int prefixMultiplier = 1;

        // I do NOT expect anyone to pass in a terabyte
        // file and if they do it's their fault
        switch(targetSizePrefix)
        {
            case 'K':
                prefixMultiplier = PREFIX_MULTIPLIER_KILO;
                break;
            case 'M':
                prefixMultiplier = PREFIX_MULTIPLIER_MEGA;
                break;
            case 'G':
                prefixMultiplier = PREFIX_MULTIPLIER_GIGA;
                break;
            default:
                System.err.println("Invalid target file size");
        }

        // get the first option and convert it to an integer, remove last character (SI prefix)
        int targetFileSize = Integer.parseInt(options[0].substring(0,
                                                                   options[0].length() - 1));

        long targetBitRate = targetFileSize * prefixMultiplier * BYTES_TO_BITS;

        int fileLengthSeconds = LocalTime.parse(fileLengthTimeStamp)
                                         .toSecondOfDay();

        long bitrateKBPS = targetBitRate / fileLengthSeconds / PREFIX_MULTIPLIER_KILO;

        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("ffmpeg -i ");
        sb.append(src.getAbsolutePath());
        sb.append(" -b ");
        sb.append(bitrateKBPS);
        sb.append("k ");
        if(options[1] != null)
        {
            sb.append("-r");
            sb.append(options[1]);
        }
        sb.append(dst.toString());

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
