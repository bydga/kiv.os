/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

/**
 *
 * @author bydga
 */
public class Pwd extends cz.zcu.kiv.os.core.Process {

	@Override
	protected void run(String[] args) throws Exception {
		this.getOutputStream().writeLine(this.getWorkingDir());
	}
	
}
