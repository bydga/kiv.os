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
public class Wc extends cz.zcu.kiv.os.core.Process {

	@Override
	public void run(String[] args) throws Exception {

		int i = 0;
		String line = null;
		while ((line = this.stdIn.readLine()) != null) {
			i++;
		}
		
		this.stdOut.writeLine("Total of " + i + " lines.");

	}
}
