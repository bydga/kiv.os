/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.zcu.kiv.os.terminal;

import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 *
 * @author veveri
 */
public class TerminalFrame extends JFrame {

	public TerminalFrame(String title) throws HeadlessException {
		super(title);
		enableEvents(MessageEvent.EVENT_ID);
	}
}
