/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os;

import cz.zcu.kiv.os.core.Core;
import cz.zcu.kiv.os.processes.Init;
import cz.zcu.kiv.os.terminal.InputParser;
import cz.zcu.kiv.os.terminal.ParseResult;
import cz.zcu.kiv.os.terminal.SwingTerminal;

/**
 *
 * @author bydga
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {


		cz.zcu.kiv.os.core.Process p = new Init();
		p.init(0, null, null, null, null, null);


		InputParser parser = new InputParser();
		ParseResult pr = parser.parse("cat ahoj | sort | wc -l");
		cz.zcu.kiv.os.core.Process result = Core.getInstance().getServices().createProcess(p, pr);

		SwingTerminal t = new SwingTerminal();


	}
}
