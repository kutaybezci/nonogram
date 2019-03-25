package nonogram.solver;

import java.util.Arrays;

public class Table {
	private Boolean[][] filled;
	private int[][] rowHint;
	private int[][] columnHint;

	public Table(int[][] rowHint, int[][] columnHint) {
		this.rowHint = rowHint;
		this.columnHint = columnHint;
		this.filled = new Boolean[this.rowHint.length][this.columnHint.length];
	}

	public Table(Boolean[][] filled) {
		int[][][] hints = Solver.getRow0AndColumn1Hint(filled);
		construct(filled, hints[0], hints[1]);
	}

	public Table(int size) {
		this.rowHint = new int[size][];
		this.columnHint = new int[size][];
		this.filled = new Boolean[size][size];
	}

	private void construct(Boolean[][] filled, int[][] rowHint, int[][] columnHint) {
		this.filled = filled;
		this.rowHint = rowHint;
		this.columnHint = columnHint;
		checkHints();
	}

	public static int getFilledCount(int[][] hint) {
		int total = 0;
		for (int r = 0; r < hint.length; r++) {
			for (int c = 0; c < hint[r].length; c++) {
				total += hint[r][c];
			}
		}
		return total;
	}

	public void calculateHintFromTable() {
		int[][][] hints = Solver.getRow0AndColumn1Hint(this.filled);
		this.rowHint = hints[0];
		this.columnHint = hints[1];
	}

	public void clearTable() {
		this.filled = new Boolean[this.rowHint.length][this.columnHint.length];
	}

	public void checkHints() {
		int columnSize = this.filled[0].length;
		for (int r = 1; r < this.filled.length; r++) {
			if (columnSize != this.filled[r].length) {
				throw new RuntimeException("Nonogram table each row should have equal column count");
			}
		}
		int totalFilled = getFilledCount(this.columnHint);
		if (totalFilled != getFilledCount(this.rowHint)) {
			throw new RuntimeException("Row hint is not equal to column hint");
		}
	}

	public Boolean[] getRow(int row) {
		return filled[row];
	}

	public Boolean[] getColumn(int column) {
		Boolean[] columnLine = new Boolean[this.filled.length];
		for (int r = 0; r < this.filled.length; r++) {
			columnLine[r] = filled[r][column];
		}
		return columnLine;
	}

	public void setRow(int row, Boolean[] rowLine) {
		this.filled[row] = rowLine;
	}

	public void setColumn(int column, Boolean[] columnLine) {
		for (int r = 0; r < this.filled.length; r++) {
			filled[r][column] = columnLine[r];
		}
	}

	public int getUnsolvedCount() {
		int unsolvedCount = 0;
		for (int r = 0; r < this.filled.length; r++) {
			for (int c = 0; c < this.filled[r].length; c++) {
				if (this.filled[r][c] == null) {
					unsolvedCount++;
				}
			}
		}
		return unsolvedCount;
	}

	public void solve() {
		int unsolvedCount = getUnsolvedCount();
		while (unsolvedCount > 0) {
			int newUnsolved = solveStep();
			if (newUnsolved >= unsolvedCount) {
				throw new RuntimeException("Cannot solve");
			}
			unsolvedCount = newUnsolved;
		}
	}

	public int solveStep() {
		for (int r = 0; r < this.filled.length; r++) {
			Boolean[] solvedRow = Solver.solve(getRow(r), this.rowHint[r]);
			setRow(r, solvedRow);
		}
		for (int c = 0; c < this.filled[0].length; c++) {
			Boolean[] solvedColumn = Solver.solve(getColumn(c), this.columnHint[c]);
			setColumn(c, solvedColumn);
		}
		return getUnsolvedCount();
	}

	public Boolean[][] getFilled() {
		return filled;
	}

	public int[][] getRowHint() {
		return rowHint;
	}

	public int[][] getColumnHint() {
		return columnHint;
	}

	public Boolean getCell(int r, int c) {
		return this.filled[r][c];
	}

	public void setCell(int r, int c, Boolean f) {
		this.filled[r][c] = f;
	}

	public void checkDone() {
		checkHints();
		for (int r = 0; r < this.filled.length; r++) {
			for (int c = 0; c < this.filled.length; c++) {
				if (filled[r][c] == null) {
					filled[r][c] = false;
				}
			}
		}
		int[][][] hints = Solver.getRow0AndColumn1Hint(this.filled);
		for (int r = 0; r < hints[0].length; r++) {
			if (!Arrays.equals(hints[0][r], this.rowHint[r])) {
				throw new RuntimeException("Row is not suitable for hint at row: " + r);
			}
		}
		for (int c = 0; c < hints[1].length; c++) {
			if (!Arrays.equals(hints[1][c], this.columnHint[c])) {
				throw new RuntimeException("Column is not suitable for hint at column: " + c);
			}
		}
	}
}
