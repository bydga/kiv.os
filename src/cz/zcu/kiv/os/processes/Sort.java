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

	public Sort(int pid, Process parent, String[] args, InputStream stdIn, OutputStream stdOut, OutputStream stdErr) {
		super(pid, parent, args, stdIn, stdOut, stdErr);
	}



	

	@Override
	protected void run() throws Exception {

	}
}