package cz.zcu.kiv.os.core.filesystem;

/**
 * Modes in which a file can be opened.
 *
 * @author Jakub Danek
 */
public enum FileMode {

    /**
     * Read only filemode
     */
    READ,
    /**
     * Write mode - overwrites existing
     */
    WRITE,
    /**
     * Write mode - appends to existing
     */
    APPEND

}
