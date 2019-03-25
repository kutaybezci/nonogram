package nonogram.data;

public class Nonogram {
	private int size;
	private Boolean[][] filled;
	private int[][][] hints;

	public Nonogram(int size) {
		this.size = size;
		this.filled = new Boolean[size][size];
		this.hints = new int[2][size][];
	}

	public void clear() {
		this.filled = new Boolean[size][size];
	}

	public Boolean getCell(int r, int c) {
		return this.filled[r][c];
	}

	public void setCell(int r, int c, Boolean filled) {
		this.filled[r][c] = filled;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Boolean[][] getFilled() {
		return filled;
	}

	public void setFilled(Boolean[][] filled) {
		this.filled = filled;
	}

	public int[][][] getHints() {
		return hints;
	}

	public void setHints(int[][][] hints) {
		this.hints = hints;
	}

}
