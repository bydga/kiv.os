/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Process;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author bydga
 */
public class Sort extends Process {

	protected OutputStream out;

	public Sort(int pid, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) {
		super(pid, stdIn, stdOut, stdErr);
		this.out = stdOut;
	}

	@Override
	protected void run() throws Exception {
		this.out.write("ahoj a necum\n".getBytes());

	}
}
