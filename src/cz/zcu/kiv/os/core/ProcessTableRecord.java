package cz.zcu.kiv.os.core;

import cz.zcu.kiv.os.core.device.IDevice;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bydga
 */
public class ProcessTableRecord {

	private Process process;
	private boolean backgroundProcess;
	private List<IDevice> openedStreams;

        public ProcessTableRecord(Process p, boolean isBackgroundProcess) {
		this.process = p;
		this.backgroundProcess = isBackgroundProcess;
		this.openedStreams = new ArrayList<IDevice>();
	}

        public boolean isForegroundProcess() {
		return this.backgroundProcess;
	}

	public List<IDevice> getOpenedStreams() {
		return this.openedStreams;
	}

	public Process getProcess() {
		return this.process;
	}

        public void addOpenStream(IDevice stream) {
            this.openedStreams.add(stream);
        }

}
