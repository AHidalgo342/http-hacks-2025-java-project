import java.util.Arrays;

/**
 * Utility class used to hold application-wide constants.
 *
 * @author Szymon Zemojtel
 * @version 1
 */
public final class Constants
{
    public static final String   FILE_DESCRIPTION_VIDEO = "Only Video Files";
    public static final String   FILE_DESCRIPTION_AUDIO = "Only Audio Files";
    public static final String   FILE_DESCRIPTION_ALL   = "Video & Audio Files";
    public static final String[] FILE_TYPES_VIDEO       = {"*.mp4",
                                                           "*.m4a",
                                                           "*.mov",
                                                           "*.avi",
                                                           "*.wmv",
                                                           "*.webm"};
    public static final String[] FILE_TYPES_AUDIO       = {"*.wav",
                                                           "*.mp3",
                                                           "*.aac"};


    public static final int SKIP_FIRST = 1;

}
