package cz.zcu.kiv.os.processes;

import cz.zcu.kiv.os.Utilities;
import cz.zcu.kiv.os.core.Process;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jiri Zikmund
 */
public class Echo extends cz.zcu.kiv.os.core.ProcessWithOptions {

	/* @Override */
	protected String helpText =
		  "------------------------------"
		+ "This is help for cat process  "
		+ "This help is not completed yet"
		+ "This is help for cat process  "
		+ "This help is not completed yet"
		+ "This is help for cat process  "
		+ "This help is not completed yet"
		+ "------------------------------";
	
	/* @Override */
	protected String[] definedOptions = {
		"-n",
		"-r"
	};
	
	@Override
	protected void runWithOptions(String[] args, String[] names, String[] options) throws Exception {
		
		for (int i = 0; i < names.length; i++) {
			
			this.writeln("Jmeno souboru " + i + ": '" +  names[i] + "'");
			
		}
		
	}

	private void writeln(String line) {
		try {
			this.stdOut.writeLine(line);
		} catch (Exception ex) {
			this.stop("stdOut writeLine exception: " + ex.getMessage());
		}
	}
	
	private String readln() {
		try {
			return this.stdIn.readLine();
		} catch (Exception ex) {
			this.stop("stdIn readLine exception: " + ex.getMessage());
			return null;
		}
	}
	
	
}