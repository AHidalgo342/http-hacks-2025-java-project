import java.io.File;
import java.io.IOException;
import java.time.LocalTime;

import static java.lang.System.exit;

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

    private static boolean isVideo(File src)
    {
        boolean video = false;

        for(final String it : FFMPEGGUI.FILE_TYPES_VIDEO)
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

    private static boolean isGif(File file)
    {
        final String fileExtension;
        fileExtension = FFMPEGGUI.FILE_TYPES_VIDEO[6].substring(1);

        return file.toString()
                   .endsWith(fileExtension);
    }

    /**
     * Converts a file from one type to another.
     *
     * @param src File to convert
     * @param dst Destination of file
     */
    public static void convertFile(final File src,
                                   final File dst)
    {
        try
        {
            if(isGif(src))
            {
                Terminal.runFFmpeg("ffmpeg -y -i " + src + " -movflags faststart -pix_fmt yuv420p -vf \"scale=trunc(iw/2)*2:trunc(ih/2)*2\" " + dst);
            }
            else if(isGif(dst))
            {
                Terminal.runFFmpeg("ffmpeg -y -ss 30 -t 3 -i " + src + " -vf \"fps=10,scale=320:-1:flags=lanczos,split[s0][s1];[s0]palettegen[p];[s1][p]paletteuse\" -loop 0 " + dst);
            }
            if(isVideo(src))
            {
                Terminal.runFFmpeg("ffmpeg -y -i " + src + " -c copy " + dst);
            }
            else
            {
                Terminal.runFFmpeg("ffmpeg -y -i " + src + " " + dst);
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
                                    final String name,
                                    final String[] options)
    throws
    IOException,
    InterruptedException
    {
        String fileLengthVerbose = Terminal.runFFmpeg("ffmpeg -v quiet -stats -i " + src + " -f null -");

        long bitrateKBPS = getBitrateKBPS(options,
                                          fileLengthVerbose);
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
                          options,
                          bitrateKBPS);
        }
    }

    private static void compressAudio(File src,
                                      File dst,
                                      String name,
                                      String[] options,
                                      long bitrateKBPS)
    {
        exit(0);
    }

    private static void compressVideo(File src,
                                      File dst,
                                      String name,
                                      String[] options,
                                      long bitrateKBPS)
    {
        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("ffmpeg -y -i ");
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
        sb.append(File.separator);
        sb.append(name);
        sb.append(Helper.getFileType(src.getName()));

        // no idea if this works, regardless we ball.

        try
        {
            Terminal.runFFmpeg(sb.toString());
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    private static long getBitrateKBPS(String[] options,
                                       String fileLengthVerbose)
    {
        String[] fileLengthRemovedFirstHalf = fileLengthVerbose.split("time=");
        String   fileLengthTimeStamp        = fileLengthRemovedFirstHalf[1].split(" bitrate")[0];


        int targetFileSize = Integer.parseInt(options[0]);

        long targetSizeBits = targetFileSize * PREFIX_MULTIPLIER_MEGA * BYTES_TO_BITS;

        int fileLengthSeconds = LocalTime.parse(fileLengthTimeStamp)
                                         .toSecondOfDay();

        // bitrate = target size / duration
        return targetSizeBits / fileLengthSeconds / PREFIX_MULTIPLIER_KILO;
    }
}
