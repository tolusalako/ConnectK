package connectK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

public class ConnectKGUI extends JFrame {
	private static final long serialVersionUID = 1L;

	static List<CKPlayerFactory> loadedAIs = new ArrayList<CKPlayerFactory>();

	private JButton[][] buttons;
	public Color[] playerColors;
	private JLabel statusLabel;

	public ConnectKGUI(BoardModel model) {
		super("ConnectK");
		setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());
		setLayout(new BorderLayout());

		// make a status bar
		JPanel statusBar = new JPanel();
		statusBar.setPreferredSize(new Dimension(getWidth(), 16));
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
		statusLabel = new JLabel("status");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusBar.add(statusLabel);
		add(statusBar, BorderLayout.SOUTH);

		// close program on closing window
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// Create menu bar and add it to the JFrame
		CKMenuBar menuBar = new CKMenuBar(this);
		setJMenuBar(menuBar);

		initializeGame(model.width, model.height);
		setSize(500, 400 + menuBar.getHeight() + statusBar.getHeight());
		setVisible(true);
	}

	private void initializeGame(int width, int height) {
		setStatus("");
		buttons = new JButton[width][height];
		playerColors = new Color[] { null, Color.RED, Color.BLUE };
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(height, width));
		for (int j = height - 1; j >= 0; --j) {
			for (int i = 0; i < width; ++i) {
				JButton button = new JButton("" + i + ":" + j);
				buttons[i][j] = button;
				button.putClientProperty("x", new Integer(i));
				button.putClientProperty("y", new Integer(j));
				panel.add(button);
			}
		}
		add(panel, BorderLayout.CENTER);
	}

	public void setStatus(String s) {
		statusLabel.setText(s);
	}

	public void placePiece(java.awt.Point p, byte player) {
		buttons[p.x][p.y].setBackground(playerColors[player]);
	}

	public void highlightSpaces(List<Point> winningSpaces, Color winnerColor) {
		for (Point p : winningSpaces) {
			// Prev
			// buttons[p.x][p.y].setVisible(!buttons[p.x][p.y].isVisible());
			// After
			Color hColor = new Color(Math.min(winnerColor.getRed() + 150, 255),
					Math.min(winnerColor.getGreen() + 150, 255), Math.min(winnerColor.getBlue() + 150, 255));
			buttons[p.x][p.y].setBackground(hColor);
		}
	}

	public void addButtonListener(ActionListener l) {
		for (int i = 0; i < buttons.length; ++i)
			for (int j = 0; j < (buttons[0].length); ++j)
				buttons[i][j].addActionListener(l);
	}

	public void newGameDialog(ConnectKGUI parentFrame) {
		final JDialog ngDialog = new JDialog(this, "New Game");
		final ConnectKGUI pf = parentFrame;
		JPanel cp = new JPanel();
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
		JPanel rulesPanel = new JPanel();
		JPanel player1Panel = new JPanel();
		JPanel player2Panel = new JPanel();
		JPanel goPanel = new JPanel();
		cp.add(rulesPanel);
		cp.add(player1Panel);
		cp.add(player2Panel);
		cp.add(goPanel);
		rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.X_AXIS));
		player1Panel.setLayout(new BoxLayout(player1Panel, BoxLayout.X_AXIS));
		player2Panel.setLayout(new BoxLayout(player2Panel, BoxLayout.X_AXIS));
		goPanel.setLayout(new BoxLayout(goPanel, BoxLayout.X_AXIS));

		JLabel widthLabel = new JLabel("Width:");
		JLabel heightLabel = new JLabel("Height:");
		JLabel kLengthLabel = new JLabel("K:");
		JLabel gravityLabel = new JLabel("Gravity:");
		final JSpinner width = new JSpinner(new SpinnerNumberModel(BoardModel.DEFAULT_WIDTH, 1, 99, 1));
		final JSpinner height = new JSpinner(new SpinnerNumberModel(BoardModel.DEFAULT_HEIGHT, 1, 99, 1));
		final JSpinner kLength = new JSpinner(new SpinnerNumberModel(BoardModel.DEFAULT_K, 1, 99, 1));
		final JSpinner gravity = new JSpinner(new SpinnerListModel(new String[] { "On", "Off" }));
		if (!BoardModel.DEFAULT_GRAVITY)
			gravity.setValue("Off");
		rulesPanel.add(widthLabel);
		rulesPanel.add(width);
		rulesPanel.add(heightLabel);
		rulesPanel.add(height);
		rulesPanel.add(kLengthLabel);
		rulesPanel.add(kLength);
		rulesPanel.add(gravityLabel);
		rulesPanel.add(gravity);

		final JFileChooser fc = new javax.swing.JFileChooser(new java.io.File("."));

		JRadioButton player1Human = new JRadioButton("Player 1 Human");
		player1Human.setActionCommand("player1Human");
		player1Human.setSelected(true);
		JRadioButton player1AI = new JRadioButton("Player 1 AI");
		player1AI.setActionCommand("player1AI");
		final ButtonGroup player1Type = new ButtonGroup();
		player1Type.add(player1Human);
		player1Type.add(player1AI);
		player1Panel.add(player1Human);
		player1Panel.add(player1AI);
		final JComboBox<CKPlayerFactory> player1AIOptions = new JComboBox<CKPlayerFactory>();
		final JComboBox<CKPlayerFactory> player2AIOptions = new JComboBox<CKPlayerFactory>();
		for (CKPlayerFactory pf1 : loadedAIs) {
			player1AIOptions.addItem(pf1);
			player2AIOptions.addItem(pf1);
		}
		player1Panel.add(player1AIOptions);

		JRadioButton player2Human = new JRadioButton("Player 2 Human");
		player2Human.setActionCommand("player2Human");
		JRadioButton player2AI = new JRadioButton("Player 2 AI");
		player2AI.setActionCommand("player2AI");
		player2AI.setSelected(true);
		final ButtonGroup player2Type = new ButtonGroup();
		player2Type.add(player2Human);
		player2Type.add(player2AI);
		player2Panel.add(player2Human);
		player2Panel.add(player2AI);

		player2Panel.add(player2AIOptions);

		fc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				java.io.File file = ((JFileChooser) arg0.getSource()).getSelectedFile();

				int playerType = -1;
				if (file.getName().endsWith("class"))
					playerType = CKPlayerFactory.JAVA;
				else if (file.getName().endsWith("exe"))
					playerType = CKPlayerFactory.CPLUSPLUS;
				else if (file.getName().endsWith("py"))
					playerType = CKPlayerFactory.PYTHON;
				else {
					//
				}

				CKPlayerFactory ai = new CKPlayerFactory(playerType, file);
				player1AIOptions.addItem(ai);
				player1AIOptions.setSelectedItem(ai);
				player2AIOptions.addItem(ai);
				player2AIOptions.setSelectedItem(ai);
			}
		});

		JButton addAI = new JButton("Add AI");
		addAI.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fc.showOpenDialog(pf);
			}
		});

		JButton newGame = new JButton("New Game");
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final BoardModel model = new BoardModel((Integer) width.getValue(), (Integer) height.getValue(),
						(Integer) kLength.getValue(), gravity.getValue().equals("On"));
				CKPlayer player1 = null;
				CKPlayer player2 = null;
				if (player1Type.getSelection().getActionCommand().equals("player1Human"))
					player1 = new GUIPlayer((byte) 1, model);
				else
					player1 = ((CKPlayerFactory) player1AIOptions.getSelectedItem()).getPlayer((byte) 1, model.clone());

				if (player2Type.getSelection().getActionCommand().equals("player2Human"))
					player2 = new GUIPlayer((byte) 2, model);
				else
					player2 = ((CKPlayerFactory) player2AIOptions.getSelectedItem()).getPlayer((byte) 2, model.clone());
				ngDialog.dispose();
				pf.dispose();
				final ConnectKGUI gui = new ConnectKGUI(model);
				if (player1 instanceof GUIPlayer)
					gui.addButtonListener((GUIPlayer) player1);
				if (player2 instanceof GUIPlayer)
					gui.addButtonListener((GUIPlayer) player2);
				final CKPlayer player1Final = player1;
				final CKPlayer player2Final = player2;
				new Thread() {
					@Override
					public void run() {
						(new ConnectK(model, player1Final, player2Final, gui)).play();
					}
				}.start();
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ngDialog.dispose();
			}
		});
		goPanel.add(addAI);
		goPanel.add(newGame);
		goPanel.add(cancel);
		ngDialog.setContentPane(cp);
		ngDialog.setSize(400, 150);
		ngDialog.setVisible(true);
	}

	public static void main(String[] args) throws NoGuiException {
		boolean makeGui = true;
		BoardModel model = BoardModel.defaultBoard();
		CKPlayer player1 = null;
		CKPlayer player2 = null;

		// Handle Args
		for (String arg : args) {
			if (arg.equals("-nogui"))
				makeGui = false;
			// else if(arg.startsWith("-g:")
			// else if(arg.startsWith("-w:")
			// else if(arg.startsWith("-h:")
			// else if(arg.startsWith("-k:")
			else {
				try {
					loadedAIs.add(new CKPlayerFactory(arg));
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Could not load AI: " + arg);
					System.exit(1);
				}
			}
		}

		if (makeGui) {
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");// a
																					// cross
																					// platform
																					// L&F;
																					// other
																					// L&F
																					// had
																					// some
																					// compatibility
																					// issues
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (loadedAIs.size() == 2) {
			player1 = loadedAIs.get(0).getPlayer((byte) 1, model.clone());
			player2 = loadedAIs.get(1).getPlayer((byte) 2, model.clone());
		} else if (loadedAIs.size() == 1) {
			if (!makeGui)
				throw new NoGuiException("If -nogui option is used, please make sure both players are AIs.");
			player1 = new GUIPlayer((byte) 1, model);
			player2 = loadedAIs.get(0).getPlayer((byte) 2, model.clone());
		} else {
			if (!makeGui)
				throw new NoGuiException("If -nogui option is used, please make sure both players are AIs.");
			player1 = new GUIPlayer((byte) 1, model);
			player2 = new GUIPlayer((byte) 2, model);
		}
		System.out.println("player1, " + player1);
		if (makeGui) {
			ConnectKGUI gui = new ConnectKGUI(model);
			if (player1 instanceof GUIPlayer)
				gui.addButtonListener((GUIPlayer) player1);
			if (player2 instanceof GUIPlayer)
				gui.addButtonListener((GUIPlayer) player2);
			(new ConnectK(model, player1, player2, gui)).play();
		} else {
			try {
				// For tournaments
				ConnectK tournament = new ConnectK(model, player1, player2, null);
				byte winner = tournament.play();
				System.out.println("Winner is: " + winner);
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Couldn't determine winner or run game. Please run again.");
				System.exit(1);
			}
		}
	}
}

class CKMenuBar extends JMenuBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6040805383728634794L;
	private ConnectKGUI pf;

	public CKMenuBar(ConnectKGUI parentFrame) {
		pf = parentFrame;
		JMenu fileMenu;
		JMenuItem menuItem;
		// Build the File menu.
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);

		// attach a group of JMenuItems to it
		menuItem = new JMenuItem("New", KeyEvent.VK_N);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				pf.newGameDialog(pf);
			}
		});
		fileMenu.add(menuItem);
		menuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		fileMenu.add(menuItem);
	}
}

class NoGuiException extends Exception {
	public NoGuiException(String message) {
		super(message);
	}
}
