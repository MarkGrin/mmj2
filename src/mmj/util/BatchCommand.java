/**
 * 
 */
package mmj.util;

/**
 * <p>
 * This class contain batch command name and documentation of it and it's
 * options. It's created to unite command name and it's documentation.
 */
public class BatchCommand {

    private final String name;
    private final String documentation;

    /**
     * Constructor of BatchCommand. Sets documentation to null.
     * 
     * @param name_ name of the command that will be stored
     */
    BatchCommand(final String name_) {
        name = name_;
        documentation = null;
    }

    /**
     * Constructor of BatchCommand.
     * 
     * @param name_ name of the command that will be stored
     * @param documentation_ string with the documentation of this command
     */
    BatchCommand(final String name_, final String documentation_) {
        name = name_;
        documentation = documentation_;
    }

    /**
     * getter of name string
     * 
     * @return name string
     */
    final String name() {
        return name;
    }

    /**
     * getter of documentation string
     * 
     * @return string with documentation
     */
    final String documentation() {
        return documentation;
    }

}