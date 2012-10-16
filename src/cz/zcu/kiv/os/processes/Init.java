/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import cz.zcu.kiv.os.core.device.InOutDevice;
import cz.zcu.kiv.os.terminal.SwingTerminal;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 *
 * @author bydga
 */
public class Init extends cz.zcu.kiv.os.core.Process {

	private InOutDevice createStdDevice() throws IOException {
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream(pis);
		return new InOutDevice(pis, pos);
	}

	@Override
	public void run(String[] args) throws Exception {

		this.stdIn = this.createStdDevice();
		this.stdOut = this.createStdDevice();
		this.stdErr = this.createStdDevice();
		SwingTerminal terminal = new SwingTerminal((IInputDevice) stdOut, (IOutputDevice) stdIn);
		while (true) {
			
		}
	}
}
