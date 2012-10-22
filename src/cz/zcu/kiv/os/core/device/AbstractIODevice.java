package cz.zcu.kiv.os.core.device;

/**
 * Join class for default IDevice implementation and IInputDevice and IOutputDevice
 * interfaces.
 *
 * Implementations of this class should be capable of both writing to/reading from
 * the backing data storage (memory buffer, file...).
 *
 * @author Jakub Danek
 */
public abstract class AbstractIODevice extends AbstractDevice implements IInputDevice, IOutputDevice{

}
