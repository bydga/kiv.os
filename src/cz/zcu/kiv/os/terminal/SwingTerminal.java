package cz.zcu.kiv.os.terminal;

import cz.zcu.kiv.os.core.device.IInputDevice;
import cz.zcu.kiv.os.core.device.InOutDevice;
import cz.zcu.kiv.os.core.device.IOutputDevice;
import java.awt.BorderLayout;
import java.awt.Dimension;
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
        outer.setPreferredSize(new Dimension(1024, 800));

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
                    historyArea.append(stdout.readLine());
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
        topPanel.setPreferredSize(new Dimension(1024, 768));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setPreferredSize(new Dimension(1024, 768));

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
        bottomPanel.setPreferredSize(new Dimension(1024, 33));

        //TODO dummy data
        promptLabel = new JLabel("uzivatel  /path/to/dest/ $");
        bottomPanel.add(promptLabel, BorderLayout.WEST);

        JTextField inputField = new JTextField();
        bottomPanel.add(inputField);
        


        return bottomPanel;
    }

}
