package ui;
//TODO Organise the codes in (FRAMES - PANEL - LABELS)
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
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
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	
	private static final String CTRL_H = "control H";
	private static final String GET_HELP = "getHelp";
	private static final String HELP_MESSAGE = "<html>	<u><b>Available Commands</b></u> for more details, please refer to <a href= &#34;&#92;doc&#92;[F12-2j][V0.2].pdf&#34;>Üser Guide</a href><br/><br/><table>" +
			"<tr><td valign=&#34;baseline&#34;>add &lt;data&gt;</td>" +
			"<td>Add a task to your existing list of tasks.<br/>Add 'impt' or 'important' to task if important. </li></ul</td></tr>" +
			"<tr><td>mark &lt;number&gt;</td>" +
			"<td> Mark task as done when completed.</td></tr>"+
			"<tr><td>edit &lt;number&gt; &lt;data&gt;</td>" +
			"<td> Change certain details of a task.</td></tr>" +
			"<tr><td>search &lt;data&gt;</td>" +
			"<td> Search tasks by name, date or both. </td></tr>" +
			"<tr><td>undo</td>" +
			"<td> Undo task. </td></tr>" +
			"<tr><td>redo</td>" +
			"<td> Redo task. </td></tr>" +
			"<tr><td>'tab' key </td>" +
			"<td> Switch between list and textfield. </td></tr>" +
			"</table></html>";
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
	
	private static final int COMMAND_OUTPUT_LABEL_WIDTH = 100;
	private static final int COMMAND_OUTPUT_LABEL_HEIGHT = 10;
	private static final String COMMAND_OUTPUT_FONT_TYPE = "Courier";
	private static final int COMMAND_OUTPUT_FONT_STYLE = Font.BOLD;
	private static final int COMMAND_OUTPUT_FONT_SIZE = 12;
	private static final Color COMMAND_OUTPUT_FONT_COLOR = Color.red;
	
	
	private static final Dimension MIN_FRAME_DIMENSION = new Dimension(MIN_FRAME_WIDTH,MIN_FRAME_HEIGHT) ;
	private static final Dimension FOREGROUND_PANEL_DIMENSION = new Dimension(FOREGROUND_PANEL_WIDTH,FOREGROUND_PANEL_HEIGHT) ;
	private static final Dimension HDR_IMAGE_DIMENSION = new Dimension(HDR_IMAGE_WIDTH, HDR_IMAGE_HEIGHT);
	private static final Dimension COMMAND_OUTPUT_LABEL_DIMENSION = new Dimension(COMMAND_OUTPUT_LABEL_WIDTH, COMMAND_OUTPUT_LABEL_HEIGHT);
	
	private static final Font TASKLIST_FONT = new Font(TASKLIST_FONT_TYPE,TASKLIST_FONT_STYLE,TASKLIST_FONT_SIZE);
	private static final Font COMMAND_OUTPUT_FONT = new Font(COMMAND_OUTPUT_FONT_TYPE,COMMAND_OUTPUT_FONT_STYLE,COMMAND_OUTPUT_FONT_SIZE);
	
	private static Logger logger = Logger.getLogger("JIMI");

	// list of tasks
	JList<String> tasklist;
	JScrollPane listPane;
	JTextField textField = new JTextField(32);
	CommandProcessor commandProcessor;
	JLabel commandOutputLabel;
	Box verticalBox;

	public GUI(){

		try {
			commandProcessor = new CommandProcessor();
			tasklist = new JList<String>(
					commandProcessor.getCurrentListModelOfTasks());
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error: could not get list of tasks.");
		}
		
		tasklist.setVisibleRowCount(4);
		tasklist.setFont(TASKLIST_FONT);
		listPane = new JScrollPane(tasklist);
		tasklist.setFixedCellHeight(30);

		// Create and set up the layered pane.
		JPanel foregroundPanel = new JPanel(new GridBagLayout());
		foregroundPanel.setOpaque(false); 
		foregroundPanel.setPreferredSize(FOREGROUND_PANEL_DIMENSION);
		
		//hint in textfield
		textField.setUI(new HintFieldUI("hold 'ctrl + h' for help", true));
		
		// key shortcuts for previous command and next commands (like cmd), and help
		textField.addActionListener(this);
		textField.getInputMap().put(KeyStroke.getKeyStroke(UP), UP_KEY);
		textField.getInputMap().put(KeyStroke.getKeyStroke(DOWN), DOWN_KEY);
		textField.getInputMap().put(KeyStroke.getKeyStroke(CTRL_H), GET_HELP);
		textField.getActionMap().put(UP_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String previouslyIssuedCommand = commandProcessor
						.getPreviouslyIssued();
				if (!previouslyIssuedCommand.isEmpty()) {
					textField.setText(previouslyIssuedCommand);
				}
			}
		});
		
		textField.getActionMap().put(DOWN_KEY, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String laterIssuedCommand = commandProcessor.getLaterIssued();
				if (!laterIssuedCommand.isEmpty()) {
					textField.setText(laterIssuedCommand);
				}
			}
		});
		textField.getActionMap().put(GET_HELP, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, HELP_MESSAGE);	
				}
		});
		foregroundPanel.add(textField);

		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// output after processing command
		commandOutputLabel = new JLabel(EMPTY_STRING); 
		commandOutputLabel.setFont(COMMAND_OUTPUT_FONT);
		commandOutputLabel.setForeground(COMMAND_OUTPUT_FONT_COLOR);
		commandOutputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		commandOutputLabel.setPreferredSize(COMMAND_OUTPUT_LABEL_DIMENSION);
		add(createControlPanel());
		add(commandOutputLabel);
		add(foregroundPanel);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textField.requestFocus();
			}
		});
	}

	public Component createControlPanel() {
		JLabel hdrLabel = new JLabel();
		try{
			// header image
			ImageIcon hdr = new ImageIcon(this.getClass().getResource(HDR_IMAGE_SRC));
			hdrLabel = new JLabel(hdr);
			hdrLabel.setPreferredSize(HDR_IMAGE_DIMENSION);
			hdrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		}catch(NullPointerException e){
			logger.log(Level.WARNING, "Could not load image");
		}
		verticalBox = Box.createVerticalBox();
		verticalBox.add(hdrLabel);
		verticalBox.add(listPane);
		verticalBox.setPreferredSize(new Dimension(180, 250));
		verticalBox.setBorder(BorderFactory.createTitledBorder(BORDER_TITLE));
		return verticalBox;

	}

	// process command output for display
	public void actionPerformed(ActionEvent evt) {
		String command = textField.getText();

		String output = commandProcessor.processCommand(command);
		commandOutputLabel.setText(output);
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
		JFrame frame = new JFrame(FRAME_NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(MIN_FRAME_DIMENSION);

		// Create and set up the content pane.
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
					FileHandler fileHandler = new FileHandler("app.log", true);
					logger.addHandler(fileHandler);
					logger.log(Level.INFO, "Starting GUI");
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}