package Chess;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class MESChess extends JPanel implements MouseListener, Runnable {

	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	public static final float SCALE = 1f;

	private Thread thread;
	private BufferedImage image;
	private Graphics g;

	private boolean running;
	private int FPS = 30;
	private int targetTime = 1000 / FPS;
	private int xOff = 120;
	private int yOff = 20;

	private Board board = new Board();
	private Position kingWhite, kingBlack;

	private ArrayList<int[]> hints = new ArrayList<int[]>();

	public MESChess() throws IOException {
		super();
		setPreferredSize(new Dimension((int) (WIDTH * SCALE), (int) (HEIGHT * SCALE)));
		setFocusable(true);
		requestFocus();
	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		addMouseListener(this);
	}

	@Override
	public void run() {
		try {
			init();

			long startTime;
			long urdTime;
			long waitTime;

			while (running) {

				startTime = System.nanoTime();

				update();
				draw();

				urdTime = (System.nanoTime() - startTime) / 1000000;
				waitTime = targetTime - urdTime;

				try {
					if (waitTime < 0)
						waitTime = 5;
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void update() {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				if (board.get(x, y).equals("A"))
					kingWhite.set(x, y);
				if (board.get(x, y).equals("a"))
					kingBlack.set(x, y);
			}
		}
	}

	private void draw() {
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		drawBoard(g);
		drawOptions(g);
		drawPieces(g);

		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, (int) (WIDTH * SCALE), (int) (HEIGHT * SCALE), null);
		g2.dispose();
	}

	private void drawOptions(Graphics g) {
		for (int[] hint : hints) {
			if (hint[2] == 0) {
				g.setColor(new Color(255, 250, 0));
				g.fillRect(xOff + 5 + hint[0] * 70, yOff + 5 + hint[1] * 70, 60, 60);
			}
			if (hint[2] == 1) {
				g.setColor(new Color(0, 255, 255));
				g.fillRect(xOff + 5 + hint[0] * 70, yOff + 5 + hint[1] * 70, 60, 60);
			}
			if (hint[2] == 2) {
				g.setColor(new Color(219, 112, 147));
				g.fillRect(xOff + 5 + hint[0] * 70, yOff + 5 + hint[1] * 70, 60, 60);
			}
		}
	}

	private void drawPieces(Graphics g) {
		for (int i = 0; i < 64; i++) {
			int x = -1, y = -1;
			int width = 6, height = 2;

			switch (board.get(i % 8, i / 8)) {
			case "A":
				x = 0;
				y = 0;
				break;
			case "a":
				x = 0;
				y = 1;
				break;
			case "Q":
				x = 1;
				y = 0;
				break;
			case "q":
				x = 1;
				y = 1;
				break;
			case "B":
				x = 2;
				y = 0;
				break;
			case "b":
				x = 2;
				y = 1;
				break;
			case "K":
				x = 3;
				y = 0;
				break;
			case "k":
				x = 3;
				y = 1;
				break;
			case "R":
				x = 4;
				y = 0;
				break;
			case "r":
				x = 4;
				y = 1;
				break;
			case "P":
				x = 5;
				y = 0;
				break;
			case "p":
				x = 5;
				y = 1;
				break;
			}
			if (x != -1 && y != -1) {
				BufferedImage pieces;
				try {
					pieces = ImageIO.read(new File("pieces.png"));
					g.drawImage(pieces.getSubimage((pieces.getWidth() / width) * x, (pieces.getHeight() / height) * y, pieces.getWidth() / width,
							pieces.getHeight() / height), xOff + (i % 8) * 70, yOff + (i / 8) * 70, 70, 70, this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void drawBoard(Graphics g) {
		g.setColor(Color.RED);
		g.fillRect(xOff - 5, yOff - 5, 570, 570);
		for (int i = 0; i < 64; i += 2) {
			g.setColor(new Color(255, 200, 100));
			g.fillRect(xOff + (i % 8 + (i / 8) % 2) * 70, yOff + (i / 8) * 70, 70, 70);
			g.setColor(new Color(150, 50, 30));
			g.fillRect(xOff + ((i + 1) % 8 - ((i + 1) / 8) % 2) * 70, yOff + ((i + 1) / 8) * 70, 70, 70);
		}
	}

	private void init() throws Exception {
		kingWhite = new Position();
		kingBlack = new Position();

		running = true;

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			if (new Rectangle(xOff, yOff, 560, 560).contains(e.getX(), e.getY())) {
				int x = (e.getX() - xOff) / 70;
				int y = (e.getY() - yOff) / 70;
				System.out.println(x + ", " + y + "-" + board.get(x, y));
				hints.clear();
				if (Character.isUpperCase(board.get(x, y).charAt(0))) {
					if (board.get(x, y).equals("A")) {
						possibleA(x, y);
					}
					if (board.get(x, y).equals("Q")) {
						possibleQ(x, y);
					}
					if (board.get(x, y).equals("B")) {
						possibleB(x, y);
					}
					if (board.get(x, y).equals("K")) {
						possibleK(x, y);
					}
					if (board.get(x, y).equals("R")) {
						possibleR(x, y);
					}
					if (board.get(x, y).equals("P")) {
						possibleP(x, y);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void possibleA(int x, int y) {
		hints.add(new int[] { x, y, 0 });
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx != 0 || dy != 0) {
					try {
						if (board.get(x + dx, y + dy).equals(" ")) {
							if (kingSafe(x, y, x + dx, y + dy, " "))
								hints.add(new int[] { x + dx, y + dy, 1 });
						}
						if (Character.isLowerCase(board.get(x + dx, y + dy).charAt(0))) {
							if (kingSafe(x, y, x + dx, y + dy, board.get(x + dx, y + dy)))
								hints.add(new int[] { x + dx, y + dy, 2 });
						}
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}
		}
	}

	public void possibleQ(int x, int y) {
		possibleB(x, y);
		possibleR(x, y);
	}

	public void possibleB(int x, int y) {
		hints.add(new int[] { x, y, 0 });
		int temp = 1;
		for (int dx = -1; dx <= 1; dx += 2) {
			for (int dy = -1; dy <= 1; dy += 2) {
				try {
					while (board.get(x + temp * dx, y + temp * dy).equals(" ")) {
						if (kingSafe(x, y, x + temp * dx, y + temp * dy, " "))
							hints.add(new int[] { x + temp * dx, y + temp * dy, 1 });
						temp++;
					}
					if (Character.isLowerCase(board.get(x + temp * dx, y + temp * dy).charAt(0))) {
						if (kingSafe(x, y, x + temp * dx, y + temp * dy, board.get(x + temp * dx, y + temp * dy)))
							hints.add(new int[] { x + temp * dx, y + temp * dy, 2 });
					}
					temp = 1;
				} catch (ArrayIndexOutOfBoundsException ex) {
				}
			}
		}
	}

	public void possibleK(int x, int y) {
		hints.add(new int[] { x, y, 0 });
		for (int dx = -1; dx <= 1; dx += 2) {
			for (int dy = -1; dy <= 1; dy += 2) {
				try {
					if (board.get(x + dx, y + dy * 2).equals(" ")) {
						if (kingSafe(x, y, x + dx, y + dy * 2, " "))
							hints.add(new int[] { x + dx, y + dy * 2, 1 });
					}
					if (Character.isLowerCase(board.get(x + dx, y + dy * 2).charAt(0))) {
						if (kingSafe(x, y, x + dx, y + dy * 2, board.get(x + dx, y + dy * 2)))
							hints.add(new int[] { x + dx, y + dy * 2, 2 });
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
				}
				try {
					if (board.get(x + dx * 2, y + dy).equals(" ")) {
						if (kingSafe(x, y, x + dx * 2, y + dy, " "))
							hints.add(new int[] { x + dx * 2, y + dy, 1 });
					}
					if (Character.isLowerCase(board.get(x + dx * 2, y + dy).charAt(0))) {
						if (kingSafe(x, y, x + dx * 2, y + dy, board.get(x + dx * 2, y + dy)))
							hints.add(new int[] { x + dx * 2, y + dy, 1 });
					}
				} catch (ArrayIndexOutOfBoundsException ex) {
				}
			}

		}
	}

	public void possibleR(int x, int y) {
		hints.add(new int[] { x, y, 0 });
		for (int d = -1; d <= 1; d += 2) {
			int temp = 1;
			try {
				while (board.get(x, y + temp * d).equals(" ")) {
					if (kingSafe(x, y, x, y + temp * d, " "))
						hints.add(new int[] { x, y + temp * d, 1 });
					temp++;
				}
				if (Character.isLowerCase(board.get(x, y + temp * d).charAt(0))) {
					if (kingSafe(x, y, x, y + temp * d, board.get(x, y + temp * d)))
						hints.add(new int[] { x, y + temp * d, 2 });
				}
			} catch (ArrayIndexOutOfBoundsException ex) {
			}
		}
		for (int d = -1; d <= 1; d += 2) {
			int temp = 1;
			try {
				while (board.get(x + temp * d, y).equals(" ")) {
					if (kingSafe(x, y, x + temp * d, y, " "))
						hints.add(new int[] { x + temp * d, y, 1 });
					temp++;
				}
				if (Character.isLowerCase(board.get(x + temp * d, y).charAt(0))) {
					if (kingSafe(x, y, x + temp * d, y, board.get(x + temp * d, y)))
						hints.add(new int[] { x + temp * d, y, 2 });
				}
				temp = 0;
			} catch (ArrayIndexOutOfBoundsException ex) {
			}
		}
	}

	public void possibleP(int x, int y) {
		hints.add(new int[] { x, y, 0 });
	}

	public boolean kingSafe(int x1, int y1, int x2, int y2, String take) {
		board.makeMove(x1, y1, x2, y2, take);
		// bishop/queen
		int temp = 1;
		for (int x = -1; x <= 1; x += 2) {
			for (int y = -1; y <= 1; y += 2) {
				try {
					while (board.get(kingWhite.getX() + temp * x, kingWhite.getY() + temp * y, " ")) {
						temp++;
					}
					if (board.get(kingWhite.getX() + temp * x, kingWhite.getY() + temp * y, "b")
							|| board.get(kingWhite.getX() + temp * x, kingWhite.getY() + temp * y, "q")) {
						board.undoMove(x1, y1, x2, y2, take);
						return false;
					}
				} catch (Exception e) {
				}
				temp = 1;
			}
		}
		// rook/queen
		for (int d = -1; d <= 1; d += 2) {
			try {
				while (board.get(kingWhite.getX() + temp * d, kingWhite.getY(), " ")) {
					temp++;
				}
				if (board.get(kingWhite.getX() + temp * d, kingWhite.getY(), "r") || board.get(kingWhite.getX() + temp * d, kingWhite.getY(), "q")) {
					board.undoMove(x1, y1, x2, y2, take);
					return false;
				}
			} catch (Exception e) {
			}
			temp = 1;
			try {
				while (board.get(kingWhite.getX(), kingWhite.getY() + temp * d, " ")) {
					temp++;
				}
				if (board.get(kingWhite.getX(), kingWhite.getY() + temp * d, "r") || board.get(kingWhite.getX(), kingWhite.getY() + temp * d, "q")) {
					board.undoMove(x1, y1, x2, y2, take);
					return false;
				}
			} catch (Exception e) {
			}
			temp = 1;
		}
		// knight
		for (int x = -1; x <= 1; x += 2) {
			for (int y = -1; y <= 1; y += 2) {
				try {
					if (board.get(kingWhite.getX() + x * 2, kingWhite.getY() + y, "k")) {
						board.undoMove(x1, y1, x2, y2, take);
						return false;
					}
				} catch (Exception e) {
				}
				try {
					if (board.get(kingWhite.getX() + x, kingWhite.getY() + y * 2, "k")) {
						board.undoMove(x1, y1, x2, y2, take);
						return false;
					}
				} catch (Exception e) {
				}
			}
		}
		// pawn
		if (kingWhite.getY() >= 2) {
			try {
				if (board.get(kingWhite.getX() - 1, kingWhite.getY() - 1, "p")) {
					board.undoMove(x1, y1, x2, y2, take);
					return false;
				}
			} catch (Exception e) {
			}
			try {
				if (board.get(kingWhite.getX() + 1, kingWhite.getY() - 1, "p")) {
					board.undoMove(x1, y1, x2, y2, take);
					return false;
				}
			} catch (Exception e) {
			}
			// king
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					if (x != 0 || y != 0) {
						try {
							if (board.get(kingWhite.getX() + x, kingWhite.getY() + y, "a")) {
								board.undoMove(x1, y1, x2, y2, take);
								return false;
							}
						} catch (Exception e) {
						}
					}
				}
			}
		}
		board.undoMove(x1, y1, x2, y2, take);
		return true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	public static void main(String[] args) throws Exception {
		JFrame window = new JFrame("Chess Client");
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final MESChess chess = new MESChess();
		window.add("Center", chess);
		// window.setContentPane(new MESChess());
		final JTextField tf = new JTextField();
		tf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = e.getActionCommand();
				if (message.startsWith("put")) {
					String args = message.substring(4);
					chess.board.set(Character.getNumericValue(args.charAt(0)), Character.getNumericValue(args.charAt(1)),
							String.valueOf(args.charAt(2)));
				}
				if (message.startsWith("move")) {
					String args = message.substring(5);
					chess.board.makeMove(Character.getNumericValue(args.charAt(0)), Character.getNumericValue(args.charAt(1)),
							Character.getNumericValue(args.charAt(2)), Character.getNumericValue(args.charAt(3)), String.valueOf(args.charAt(4)));
				}
				if (message.startsWith("undo")) {
					String args = message.substring(5);
					chess.board.undoMove(Character.getNumericValue(args.charAt(0)), Character.getNumericValue(args.charAt(1)),
							Character.getNumericValue(args.charAt(2)), Character.getNumericValue(args.charAt(3)), String.valueOf(args.charAt(4)));
				}
				tf.setText("");
			}
		});
		window.add("South", tf);
		window.pack();
		window.setVisible(true);
		window.setLocationRelativeTo(null);
	}
}
