/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.core.Process;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observer;

/**
 *
 * @author bydga
 */
public class Sort extends Process {

	public Sort(int pid, int ppid, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr, Observer processManager) {
		super(pid, ppid, args, stdIn, stdOut, stdErr, processManager);
	}

	@Override
	public void run() throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	
}
