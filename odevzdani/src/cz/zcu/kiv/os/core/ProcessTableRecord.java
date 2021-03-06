package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IDevice;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple structure wrapping process and its streams and some additional info about the process.
 *
 * @author bydga
 */
public class ProcessTableRecord {

	private Process process;
	private List<IDevice> openedStreams;

	public ProcessTableRecord(Process p) {
		this.process = p;
		this.openedStreams = new ArrayList<IDevice>();
	}

	/**
	 * Gets all streams associated with this ProcessInfo entry.
	 *
	 * @return
	 */
	public List<IDevice> getOpenedStreams() {
		synchronized(this.openedStreams) {
			return Collections.unmodifiableList(this.openedStreams);
		}
	}

	/**
	 * Get process associated with this ProcessInfo entry.
	 *
	 * @return
	 */
	public Process getProcess() {
		return this.process;
	}

	/**
	 * Adds new stream to the process.
	 *
	 * @param stream Stream to be addded.
	 */
	public void addOpenStream(IDevice stream) {
		synchronized(this.openedStreams) {
			this.openedStreams.add(stream);
		}
	}

	/**
	 * Removes stram from the process table record.
	 * @param stream stream to be removed
	 */
	public void removeStream(IDevice stream) {
		synchronized(this.openedStreams) {
			this.openedStreams.remove(stream);
		}
	}
}
