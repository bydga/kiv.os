/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

/**
 *
 * @author bydga
 */
public class Wc extends cz.zcu.kiv.os.core.Process {

	@Override
	public void run(String[] args) throws Exception {

		int i = 0;
		String line = null;
		while ((line = this.getInputStream().readLine()) != null) {
			i++;
		}
		
		this.getOutputStream().writeLine("Total of " + i + " lines.");

	}
}
