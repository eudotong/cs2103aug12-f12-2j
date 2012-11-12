package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

import commandLogic.CommandProcessor;

/**
 * 
 * @author A0092052N
 * 
 */
public class GUI extends JPanel implements ActionListener {

	private static final int VERT_BOX_HEIGHT = 280;
	private static final int VERT_BOX_WIDTH = 180;
	private static final String APP_LOG = "app.log";
	private static final String HELP_HINT = "hold 'ctrl + h' here for help";
	private static final String TAB_HINT = "m	  		Use 'tab' to transverse between list and input-box.";
	private static final int TASKLIST_CELL_HEIGHT = 30;
	private static final int TASKLIST_ROW_COUNT = 4;
	private static final String CTRL_H = "control H";
	private static final String GET_HELP = "getHelp";
	private static final String HELP_MESSAGE = "<html>	<u><b>Available Commands</b></u> for more details, please refer to User Guide<br/><br/><table>"
			+ "<tr><td valign=&#34;baseline&#34;>add &lt;task details (name, start time, end time)&gt</td>"
			+ "<td>Add a task to your existing list of tasks.<br/>Add 'impt' or 'important' to task if important. </li></ul</td></tr>"
			+ "<tr><td>mark &lt;index of task&gt;</td>"
			+ "<td> Mark task as done when completed.</td></tr>"
			+ "<tr><td>edit &lt;index of task&gt; &lt;details to edit&gt;</td>"
			+ "<td> Change certain details of a task.</td></tr>"
			+ "<tr><td>search &lt;query&gt;<br>search all<br>search upcoming<br>search before/after &lt;date&gt</td>"
			+ "<td> Search tasks by name, date or both. Can search<br>for all or upcoming tasks and also tasks that<br>occur before/after a specific date.</td></tr>"
			+ "<tr><td>undo</td>"
			+ "<td> Undo task. </td></tr>"
			+ "<tr><td>redo</td>"
			+ "<td> Redo task. </td></tr>"
			+ "<tr><td>'tab' key </td>"
			+ "<td> Switch between list and textfield. </td></tr>"
			+ "</table></html>";
	private static final String DOWN = "DOWN";
	private static final String UP = "UP";
	private static final String UP_KEY = "upKey";
	private static final String DOWN_KEY = "downKey";
	private static final String EMPTY_STRING = "";
	private static final String BORDER_TITLE = "Jimi - Task Manager";
	private static final String FRAME_NAME = "Jimi";
	private static final String HDR_IMAGE_SRC = "images/hdr.PNG";
	private static final long serialVersionUID = 1L;

	private static final int MIN_FRAME_WIDTH = 400;
	private static final int MIN_FRAME_HEIGHT = 350;
	private static final int FOREGROUND_PANEL_WIDTH = 400;
	private static final int FOREGROUND_PANEL_HEIGHT = 50;
	private static final int HDR_IMAGE_WIDTH = 180;
	private static final int HDR_IMAGE_HEIGHT = 70;

	private static final String TASKLIST_FONT_TYPE = "Serif";
	private static final int TASKLIST_FONT_STYLE = Font.BOLD;
	private static final int TASKLIST_FONT_SIZE = 12;

	private static final int COMMAND_OUTPUT_LABEL_WIDTH = 90;
	private static final int COMMAND_OUTPUT_LABEL_HEIGHT = 30;
	private static final String COMMAND_OUTPUT_FONT_TYPE = "Courier";
	private static final int COMMAND_OUTPUT_FONT_STYLE = Font.BOLD;
	private static final int COMMAND_OUTPUT_FONT_SIZE = 12;
	private static final Color COMMAND_OUTPUT_FONT_COLOR = Color.red;

	private static final Dimension MIN_FRAME_DIMENSION = new Dimension(
			MIN_FRAME_WIDTH, MIN_FRAME_HEIGHT);
	private static final Dimension FOREGROUND_PANEL_DIMENSION = new Dimension(
			FOREGROUND_PANEL_WIDTH, FOREGROUND_PANEL_HEIGHT);
	private static final Dimension HDR_IMAGE_DIMENSION = new Dimension(
			HDR_IMAGE_WIDTH, HDR_IMAGE_HEIGHT);
	private static final Dimension COMMAND_OUTPUT_LABEL_DIMENSION = new Dimension(
			COMMAND_OUTPUT_LABEL_WIDTH, COMMAND_OUTPUT_LABEL_HEIGHT);

	private static final Font TASKLIST_FONT = new Font(TASKLIST_FONT_TYPE,
			TASKLIST_FONT_STYLE, TASKLIST_FONT_SIZE);
	private static final Font COMMAND_OUTPUT_FONT = new Font(
			COMMAND_OUTPUT_FONT_TYPE, COMMAND_OUTPUT_FONT_STYLE,
			COMMAND_OUTPUT_FONT_SIZE);

	private static Logger logger = Logger.getLogger("JIMI");

	JList<String> tasklist;
	JScrollPane listPane;
	JTextField textField = new JTextField(32);
	CommandProcessor commandProcessor;
	JTextArea commandOutputText;
	Box verticalBox;

	public GUI() {

		try {
			commandProcessor = new CommandProcessor();
			tasklist = new JList<String>(
					commandProcessor.getCurrentListModelOfTasks());
		} catch (final IOException e) {
			logger.log(Level.SEVERE, "Error: could not get list of tasks.");
		}

		// list of tasks
		tasklist.setVisibleRowCount(TASKLIST_ROW_COUNT);
		tasklist.setFont(TASKLIST_FONT);
		listPane = new JScrollPane(tasklist);
		tasklist.setFixedCellHeight(TASKLIST_CELL_HEIGHT);

		// Create and set up the panel that contains textfield/input.
		final JPanel textFieldPanel = new JPanel(new GridBagLayout());
		textFieldPanel.setOpaque(false);
		textFieldPanel.setPreferredSize(FOREGROUND_PANEL_DIMENSION);

		// hint in textfield to display help
		textField.setUI(new HintFieldUI(HELP_HINT, true));


		// key shortcuts for previous command and next commands, and help
		textField.addActionListener(this);
		textField.getInputMap().put(KeyStroke.getKeyStroke(UP), UP_KEY);
		textField.getInputMap().put(KeyStroke.getKeyStroke(DOWN), DOWN_KEY);
		textField.getInputMap().put(KeyStroke.getKeyStroke(CTRL_H), GET_HELP);
		textField.getActionMap().put(UP_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final String previouslyIssuedCommand = commandProcessor
						.getPreviouslyIssued();
				if (!previouslyIssuedCommand.isEmpty()) {
					textField.setText(previouslyIssuedCommand);
				}
			}
		});
		textField.getActionMap().put(DOWN_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final String laterIssuedCommand = commandProcessor.getLaterIssued();
				if (!laterIssuedCommand.isEmpty()) {
					textField.setText(laterIssuedCommand);
				}
			}
		});
		textField.getActionMap().put(GET_HELP, new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				JOptionPane.showMessageDialog(null, HELP_MESSAGE);
			}
		});
		textFieldPanel.add(textField);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// output after processing command
		commandOutputText = new JTextArea(EMPTY_STRING);
		commandOutputText.setFont(COMMAND_OUTPUT_FONT);
		commandOutputText.setForeground(COMMAND_OUTPUT_FONT_COLOR);
		commandOutputText.setAlignmentX(Component.CENTER_ALIGNMENT);
		commandOutputText.setPreferredSize(COMMAND_OUTPUT_LABEL_DIMENSION);
		commandOutputText.setWrapStyleWord(true);  
		commandOutputText.setLineWrap(true); 
		commandOutputText.setBackground(new java.awt.Color(238, 238, 238));
		commandOutputText.setMargin(new Insets(0,10,0,0));
		commandOutputText.setEditable(false) ;
		commandOutputText.setUI(new HintFieldUI(TAB_HINT, true));
		
		add(createListPanel());
		add(commandOutputText);
		add(textFieldPanel);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textField.requestFocus();
			}
		});
	}

	public Component createListPanel() {
		JLabel hdrLabel = new JLabel();
		try {
			// header image 'Jimi' logo
			final ImageIcon hdr = new ImageIcon(this.getClass().getResource(
					HDR_IMAGE_SRC));
			hdrLabel = new JLabel(hdr);
			hdrLabel.setPreferredSize(HDR_IMAGE_DIMENSION);
			hdrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		} catch (final NullPointerException e) {
			logger.log(Level.WARNING, "Could not load image");
		}
		verticalBox = Box.createVerticalBox();
		verticalBox.add(hdrLabel);
		verticalBox.add(listPane);
		verticalBox.setPreferredSize(new Dimension(VERT_BOX_WIDTH,
				VERT_BOX_HEIGHT));
		verticalBox.setBorder(BorderFactory.createTitledBorder(BORDER_TITLE));
		return verticalBox;

	}

	// process command output for display
	public void actionPerformed(final ActionEvent evt) {
		final String command = textField.getText();
		final String output = commandProcessor.processCommand(command);
		commandOutputText.setText(output);
		tasklist.setModel(commandProcessor.getCurrentListModelOfTasks());

		textField.setText(EMPTY_STRING);
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 * 
	 * @throws FileNotFoundException
	 */
	private static void createAndShowGUI() throws FileNotFoundException {
		// Create and set up the window.
		final JFrame frame = new JFrame(FRAME_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(MIN_FRAME_DIMENSION);

		// Create and set up the content pane.
		frame.getContentPane().add(new GUI());

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(final String[] args) {
		// Schedule a job for the event dispatch thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					final FileHandler fileHandler = new FileHandler(APP_LOG, true);
					logger.addHandler(fileHandler);
					logger.log(Level.INFO, "Starting GUI");
					createAndShowGUI();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}