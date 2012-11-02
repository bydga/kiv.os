package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IDevice;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bydga
 */
public class ProcessTableRecord {

	protected Process process;
	private boolean backgroundProcess;

	public boolean isForegroundProcess() {
		return this.backgroundProcess;
	}
	protected List<Closeable> openedStreams;

	public List<IDevice> getOpenedStreams() {
		return this.openedStreams;
	}

	public boolean isRunning() {
		return this.process.isRunning();
	}

	public Process getProcess() {
		return this.process;
	}

	public ProcessTableRecord(Process p, boolean isBackgroundProcess) {
		this.process = p;
		this.backgroundProcess = isBackgroundProcess;
		this.openedStreams = new ArrayList<Closeable>();
	}
}
