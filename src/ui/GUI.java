package ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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
import javax.swing.border.Border;
import commandLogic.CommandProcessor;

public class GUI extends JPanel implements ActionListener {
	private static final String BORDER_TITLE = "Jimi - Task Manager";
	private static final String FRAME_NAME = "Jimi";
	private static final String BACKGROUND_IMG = "images/bg.gif";
	private static final String HDR_IMG = "images/hdr.png";
	private static final String ERROR_COULD_NOT_ACCESS_STORAGE = "Error: Could not access storage.";
	private static final String WELCOME_MESSAGE = "Welcome to Jimi, your friendly neighbourhood task manager.\n \n";
	private static final long serialVersionUID = 1L;
	private final static String NEW_LINE = "\n";
	private Border empty = BorderFactory.createEmptyBorder();
	JTextArea textArea = new JTextArea(7, 40);
	JTextField textField = new JTextField(32);
	CommandProcessor commandProcessor;
	JLabel commandOutputLabel = null;
	
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
		//layeredPane.add(bgLabel, JLayeredPane.DEFAULT_LAYER);
		layeredPane.add(forgroundPanel, JLayeredPane.PALETTE_LAYER);

		textField.setBackground(new java.awt.Color(220,219,219));
		textField.setBorder(empty);
		textField.addActionListener(this);

		forgroundPanel.add(textField);
		textField.setAlignmentX(Component.CENTER_ALIGNMENT);  //?

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		
		add(createControlPanel());
		add(Box.createRigidArea(new Dimension(0, 10)));

		JLabel commandOutputLabel = new JLabel("");
		commandOutputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(commandOutputLabel);
		add(layeredPane);

		try{
			CommandProcessor commandProcessor = new CommandProcessor();
			textArea.append(commandProcessor.getCurrentListOfTasks());
		}catch(IOException e){
			textArea.append(NEW_LINE + ERROR_COULD_NOT_ACCESS_STORAGE);
		}

	}

	private Component createControlPanel(){
		
		textArea.setEditable(false);
		textArea.setText("");
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText(WELCOME_MESSAGE);

		GUIdata();

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

	private void GUIdata(){
		//parse file into a single string

		Scanner s = null;

		try {
			s = new Scanner(new BufferedReader(new FileReader("taskrecords.txt")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String output = null;
		while (s.hasNext()) {
			s.useDelimiter(",\\s*");
			output = output + s.nextLine() + "\n";
		}


		s.close();
		textArea.append(output);
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
	 * @throws FileNotFoundException 
	 */
	private static void createAndShowGUI() throws FileNotFoundException {
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
				try {
					createAndShowGUI();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}