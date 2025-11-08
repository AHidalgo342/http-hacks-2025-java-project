import java.io.File;
import java.nio.file.Path;

/**
 * Terminal wrapper class.
 *
 * @author Marcy Ordinario
 * @author Alex Hidalgo
 * @version 1.0
 */
public class TerminalExecutor
{
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
     *
     * @param src  File to convert
     * @param dst  Destination of file
     * @param args Optional arguments
     */
    public static void compressFile(final File src,
                                    final Path dst,
                                    final String... args)
    {
        final StringBuilder sb;
        sb = new StringBuilder();

        sb.append("ffmpeg -i ");
        sb.append(src.getAbsolutePath());
        sb.append(" -vcodec h264 -b:v 1000k -an ");
        sb.append(dst.toString());

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
