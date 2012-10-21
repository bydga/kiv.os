package cz.zcu.kiv.os.terminal;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.InOutDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * Terminal window implemented in Swing API.
 *
 * @author Jakub Danek
 */
public class SwingTerminal extends InOutDevice {

    private JFrame frame;
    private JTextArea historyArea;
    private Thread messageListener;
    private JLabel promptLabel;

    private IInputDevice stdout;
    private IOutputDevice stdin;
    

    /**
     * Default constructor.
     */
    public SwingTerminal(IInputDevice stdout, IOutputDevice stdin) {
        super(stdout, stdin);
        this.stdin = stdin;
        this.stdout = stdout;
        runGui();
    }

    private void runGui() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                initFrame();
            }
        });
    }

    /**
     * Initialize frame parameters.
     */
    private void initFrame() {
        frame = new JFrame("OS simulation");
        initComponents();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.pack();
    }

    /**
     * Create form components.
     */
    private void initComponents() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(640, 480));

        outer.add(createTopPanel(), BorderLayout.NORTH);
        outer.add(createBottomPanel(), BorderLayout.SOUTH);

        frame.getContentPane().add(outer);
        startListening();
    }

    private void startListening() {
        messageListener = new Thread(new Runnable() {

            @Override
            public void run() {
                while(stdout.isOpen()) {
					String s = stdout.readLine();
                    historyArea.append(s +"\n");
                }
            }
        });
        messageListener.start();
    }

    /**
     * Create msg output panel.
     * @return
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(640, 450));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setPreferredSize(new Dimension(640, 450));

        JScrollPane scroll = new JScrollPane(historyArea);

        topPanel.add(scroll);
        return topPanel;
    }

    /**
     * Create prompt line.
     * @return
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setPreferredSize(new Dimension(640, 30));

        //TODO dummy data
        promptLabel = new JLabel("uzivatel  /path/to/dest/ $");
        bottomPanel.add(promptLabel, BorderLayout.WEST);

        final JTextField inputField = new JTextField();
		inputField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingTerminal.this.stdin.writeLine(inputField.getText());
				inputField.setText("");
			}
		});
        bottomPanel.add(inputField);
        


        return bottomPanel;
    }

}
