import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Terminal wrapper class.
 *
 * @author Marcy Ordinario
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
        try {
            Terminal.runCommand("ffmpeg -i " + src + " " + dst);
        } catch (Exception e) { /* DO LATER */ }
    }

    /**
     * Compresses a file.
     *
     * @param src File to convert
     * @param dst Destination of file
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

        try {
            Terminal.runCommand(sb.toString());
        } catch(Exception e) { /* DO LATER */ }
    }
}
