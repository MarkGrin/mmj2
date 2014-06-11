/**
 * 
 */
package mmj.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * this class is used to generate documentation about batch commands.
 */
public class BatchDocumentationGenerator {

    /**
     * @param args - name of the file that will be filled with documentation.
     */
    public static void main(final String[] args) {
        if (args.length >= 1)
            try {
                generateDocumentation(args[0]);
            } catch (final FileNotFoundException e) {
                return;
            }
    }

    /**
     * this function writes documentation of every command in
     * UtilConstants.RUNPARM_LIST in the file.
     * 
     * @param fileName - name of the file that will be filled with
     *            documentation.
     * @throws FileNotFoundException - is thrown if file is not found
     */
    private static void generateDocumentation(final String fileName)
        throws FileNotFoundException
    {
        final PrintWriter documentation = new PrintWriter(fileName);

        documentation.append("<html>\n");
        documentation.append("<article>\n");
        documentation.append("<h1>Table of content</h1>\n");

        documentation.append("<ul>");
        for (int index = 0; index < UtilConstants.RUNPARM_LIST.length; index++)
        {
            documentation.append("<li>");
            documentation.append("<a href=\"#command" + index);
            documentation.append("\">");
            documentation.append(UtilConstants.RUNPARM_LIST[index].name());
            documentation.append("</a>\n");
        }
        documentation.append("</ul>");

        documentation.append("<br>");
        documentation.append("<h1>Content</h1>\n");
        documentation.append("<br>");

        for (int index = 0; index < UtilConstants.RUNPARM_LIST.length; index++)
        {
            documentation.append("<hr>\n");
            documentation.append("<h3 id=\"command");
            documentation.append(index + "\">");
            documentation.append(UtilConstants.RUNPARM_LIST[index].name());
            documentation.append("</h3>\n\n");
            documentation.append(UtilConstants.RUNPARM_LIST[index]
                .documentation());
            documentation.append("\n<br>");
        }

        documentation.append("</article>\n</html>");

        documentation.close();
    }
}