/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.processes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author bydga
 */
public class Wc extends cz.zcu.kiv.os.core.Process {
	
	private boolean includeNumbers = false;
	private String path;
	
	@Override
	public void run(String[] args) throws Exception {
		List<String> argList = Arrays.asList(args);
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-h")) {
				this.includeNumbers = true;
				i++;
				if (i < args.length) {
					this.path = args[i];
				} else {
					this.getErrorStream().writeLine("chybi dalsi param za -h");
				}
				continue;
			}
		}
		
		int i = 0;
		String line = null;
		while ((line = this.getInputStream().readLine()) != null) {
			i++;
		}
		
		this.getOutputStream().writeLine("Total of " + i + " lines.");
		
	}
}
