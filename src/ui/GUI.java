package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import commandLogic.CommandProcessor;

public class GUI extends JPanel implements ActionListener {
	private static final String BORDER_TITLE = "Jimi - Task Manager";
	private static final String FRAME_NAME = "Jimi";
	private static final String BACKGROUND_IMG = "images/bg.gif";
	private static final String HDR_IMG = "images/hdr.png";
	private static final String ERROR_COULD_NOT_ACCESS_STORAGE = "Error: Could not access storage.";
	private static final long serialVersionUID = 1L;
	protected JTextArea textArea;
	protected JTextField textField;
	protected JLabel commandOutputLabel;
	private CommandProcessor commandProcessor;
	private final static String NEW_LINE = "\n";

	public GUI() {
		ImageIcon icon = createImageIcon(BACKGROUND_IMG);
		JLabel bgLabel = new JLabel(icon);
		bgLabel.setSize(bgLabel.getPreferredSize());
		
		// Create and set up the layered pane.
		JPanel forgroundPanel = new JPanel(new GridBagLayout());
        forgroundPanel.setOpaque(false);
        forgroundPanel.setSize(bgLabel.getPreferredSize());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(bgLabel.getPreferredSize());
        layeredPane.add(bgLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(forgroundPanel, JLayeredPane.PALETTE_LAYER);
        
         textField = new JTextField(30);
         textField.addActionListener(this);
         
         GridBagConstraints gbc_textField = new GridBagConstraints();
         gbc_textField.insets = new Insets(0, 0, 5, 0);
         gbc_textField.gridx = 0;
         gbc_textField.gridy = 0;
         forgroundPanel.add(textField, gbc_textField);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        add(createControlPanel());
		add(Box.createRigidArea(new Dimension(0, 10)));
        
        commandOutputLabel = new JLabel("");
        commandOutputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(commandOutputLabel);
        add(layeredPane);
		
		try{
			commandProcessor = new CommandProcessor();
			textArea.append(commandProcessor.getCurrentListOfTasks());
		}catch(IOException e){
			textArea.append(NEW_LINE + ERROR_COULD_NOT_ACCESS_STORAGE);
		}
		
	}

	private Component createControlPanel() {
		textArea = new JTextArea(7, 40);
		textArea.setEditable(true);
		textArea.setText("");
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 200));

		ImageIcon hdr = createImageIcon(HDR_IMG);
		JLabel hdrLabel = new JLabel(hdr);
		hdrLabel.setSize(150, 150);
		
		Box verticalBox = Box.createVerticalBox();
	    verticalBox.add(hdrLabel);
	    verticalBox.add(scrollPane);
	    verticalBox.setPreferredSize(new Dimension(400, 200));

		
		JPanel controls = new JPanel();
		controls.add(verticalBox);
		controls.setBorder(BorderFactory.createTitledBorder(BORDER_TITLE));
		
		return controls;
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = GUI.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String command = textField.getText();
		String output = commandProcessor.processCommand(command);
		commandOutputLabel.setText(output);
		textArea.setText(commandProcessor.getCurrentListOfTasks());
		textField.selectAll();

		// Make sure the new text is visible, even if there
		// was a selection in the text area.
		textArea.setCaretPosition(textArea.getDocument().getLength());
		textField.setText("");
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame(FRAME_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		frame.getContentPane().add(new GUI());
		
        // Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}