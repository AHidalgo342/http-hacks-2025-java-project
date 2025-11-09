import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * General helper methods.
 *
 * @author Szymon Zemojtel
 * @version 1
 */
public final class Helper
{
    /**
     * Trims the first X characters from all strings in passed string list.
     *
     * @param beginCharacterIndex index of characters in the string to remove.
     * @param stringInput         input string list.
     * @return input string list with all strings missing the first beginCharacterIndex characters.
     */
    public static List<String> removeFirstCharacters(final int beginCharacterIndex,
                                                     final List<String> stringInput)
    {
        final List<String> stringsTrimmed = new ArrayList<String>();
        for(final String curString : stringInput)
        {
            final String curStringTrimmed;
            curStringTrimmed = curString.substring(beginCharacterIndex);
            stringsTrimmed.add(curStringTrimmed);
        }

        return stringsTrimmed;
    }

    /**
     * Trims the first X characters from all strings in passed string array.
     *
     * @param beginCharacterIndex index of characters in the string to remove.
     * @param stringInput         input string array.
     * @return input string list with all strings missing the first beginCharacterIndex characters.
     */
    public static List<String> removeFirstCharacters(final int beginCharacterIndex,
                                                     final String[] stringInput)
    {
        final List<String> stringInputList;
        stringInputList = Arrays.stream(stringInput)
                                .toList();

        return removeFirstCharacters(beginCharacterIndex,
                                     stringInputList);

    }

    public static String getBaseFileName(final String filename)
    {
        final int index = filename.lastIndexOf('.');
        if(index == -1)
        {
            return filename; // Filename without extension
        }
        else
        {
            return filename.substring(0,
                                      index);
        }
    }
}
