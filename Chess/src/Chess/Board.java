package Chess;

public class Board {

	private String[] col0 = { "r", "k", "b", "q", "a", "b", "k", "r" };
	private String[] col1 = { "p", "p", "p", "p", "p", "p", "p", "p" };
	private String[] col2 = { " ", " ", " ", " ", " ", " ", " ", " " };
	private String[] col3 = { " ", " ", " ", " ", " ", " ", " ", " " };
	private String[] col4 = { " ", " ", " ", " ", " ", " ", " ", " " };
	private String[] col5 = { " ", " ", " ", " ", " ", " ", " ", " " };
	private String[] col6 = { "P", "P", "P", "P", "P", "P", "P", "P" };
	private String[] col7 = { "R", "K", "B", "Q", "A", "B", "K", "R" };

	private String[][] board = { col0, col1, col2, col3, col4, col5, col6, col7 };

	public void swap(int x1, int y1, int x2, int y2) {
		String temp = get(x1, y1);
		set(x1, y1, get(x2, y2));
		set(x2, y2, temp);
	}

	public void makeMove(int x1, int y1, int x2, int y2, String take, int turn) {
		if (turn == 0)
			flip();
		set(x2, y2, get(x1, y1));
		set(x1, y1, " ");
		if (turn == 0)
			flip();
	}

	public void undoMove(int x1, int y1, int x2, int y2, String take, int turn) {
		if (turn == 0)
			flip();
		set(x1, y1, get(x2, y2));
		set(x2, y2, take);
		if (turn == 0)
			flip();
	}

	public boolean get(int x, int y, String value) {
		return board[y][x].equals(value);
	}

	public void set(int x, int y, String value) {
		board[y][x] = value;
	}

	public String get(int x, int y) {
		return board[y][x];
	}

	public void flip() {
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				if (Character.isLowerCase(get(x, y).charAt(0))) {
					set(x, y, String.valueOf(Character.toUpperCase(get(x, y).charAt(0))));
				} else if (Character.isUpperCase(get(x, y).charAt(0))) {
					set(x, y, String.valueOf(Character.toLowerCase(get(x, y).charAt(0))));
				}
			}
		}
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 8; x++) {
				swap(x, y, 7 - x, 7 - y);
			}
		}
	}
}
