package cz.zcu.kiv.os.terminal;

import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 * JFrame extension which is able to listen to MessageEvents.
 *
 * @author Jakub Danek
 */
public class TerminalFrame extends JFrame {

	public TerminalFrame(String title) throws HeadlessException {
		super(title);
		enableEvents(MessageEvent.EVENT_ID);
	}
}
