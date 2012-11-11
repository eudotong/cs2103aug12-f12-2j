package ui;

/**
 * 
 * @author A0092052N
 *       
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import commandLogic.CommandProcessor;

public class GUI extends JPanel implements ActionListener {
	private static final String EMPTY_STRING = "";
	private static final String BORDER_TITLE = "Jimi - Task Manager";
	private static final String FRAME_NAME = "Jimi";
	private static final String BACKGROUND_IMG = "images/bg.gif";
	private static final String HDR_IMG = "images/hdr.png";
	private static final long serialVersionUID = 1L;

	JList<String> jlist;
	JScrollPane listPane;
	JTextField textField = new JTextField(32);
	CommandProcessor commandProcessor;
	JLabel cmdOutputLbl = null;
	Box verticalBox;

	public GUI() {
		try {
			commandProcessor = new CommandProcessor();
			jlist = new JList<String>(
					commandProcessor.getCurrentListModelOfTasks());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		// create jList to display tasks
		jlist.setVisibleRowCount(4);
		listPane = new JScrollPane(jlist);
		MyCellRenderer cellRenderer = new MyCellRenderer();
		jlist.setCellRenderer(cellRenderer);
		jlist.setFixedCellHeight(30);
		
		// Create and set up the text-field pane.
		JPanel textInputPane = new JPanel(new GridBagLayout());
		textInputPane.setOpaque(false);
		textInputPane.setPreferredSize(new Dimension(400, 50));
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		layeredPane.add(textInputPane, JLayeredPane.PALETTE_LAYER);

		//textField.setBackground(new java.awt.Color(220, 219, 219));
		//textField.setBorder(empty);
		textField.setUI(new HintFieldUI("hold 'ctrl + h' for help", true));
		
		
		// for 'up, down, control+h' key shortcuts
		textField.addActionListener(this);
		textField.getInputMap().put(KeyStroke.getKeyStroke("UP"), "upKey");
		textField.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "downKey");
		textField.getInputMap().put(KeyStroke.getKeyStroke("control H"), "getHelp");
		textField.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "undo");
		textField.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "redo");
		textField.getActionMap().put("upKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String previouslyIssuedCommand = commandProcessor
						.getPreviouslyIssued();
				if (!previouslyIssuedCommand.isEmpty()) {
					textField.setText(previouslyIssuedCommand);
				}
			}
		});
		textField.getActionMap().put("downKey", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String laterIssuedCommand = commandProcessor.getLaterIssued();
				if (!laterIssuedCommand.isEmpty()) {
					textField.setText(laterIssuedCommand);
				}
			}
		});
		textField.getActionMap().put("undo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commandProcessor.processCommand("undo");
				}
		});
		textField.getActionMap().put("redo", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				commandProcessor.processCommand("redo");
				}
		});
		textField.getActionMap().put("getHelp", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "<html>	<u><b>Available Commands</b></u> for more details, please refer to <a href= &#34;&#92;doc&#92;[F12-2j][V0.2].pdf&#34;>Üser Guide</a href><br/><br/><table>" +
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
						
						"</table></html>"
						
						);	
				}
		});
		textInputPane.add(textField);

		
		// layout alignment
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		// command output label
		cmdOutputLbl = new JLabel(EMPTY_STRING);
		cmdOutputLbl.setFont(new Font("Courier", Font.BOLD, 12));
		cmdOutputLbl.setForeground(Color.red);
		cmdOutputLbl.setPreferredSize(new Dimension(100, 10));
		add(createControlPanel());
		add(cmdOutputLbl);
		add(textInputPane);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				jlist.requestFocus();
			}
		});
	}

	class MyCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String text = value.toString();
			// text = HTML_1 + String.valueOf(width) + HTML_2 +
			// value.toString()+ HTML_3;

			return super.getListCellRendererComponent(list, text, index,
					isSelected, cellHasFocus);

		}

	}

	public Component createControlPanel() {

		ImageIcon hdr = createImageIcon(HDR_IMG);
		JLabel hdrLabel = new JLabel(hdr);
		hdrLabel.setPreferredSize(new Dimension(180, 70));

		verticalBox = Box.createVerticalBox();

		verticalBox.add(hdrLabel);
		verticalBox.add(listPane);
		verticalBox.setPreferredSize(new Dimension(180, 250));
		verticalBox.setBorder(BorderFactory.createTitledBorder(BORDER_TITLE));
		return verticalBox;

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
		cmdOutputLbl.setText(output);
		jlist.setModel(commandProcessor.getCurrentListModelOfTasks());
		// refreshCurrentList(commandProcessor.getCurrentListModelOfTasks());

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
					createAndShowGUI();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
