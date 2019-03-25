package nonogram.solver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.google.gson.Gson;

import nonogram.data.Nonogram;

public class Solver {

	public static int[] generateWithOutEmpty(int[] hint) {
		int[] withoutEmpty = new int[hint.length * 2 + 1];
		int h = 0;
		for (int i = 1; i < withoutEmpty.length - 1; i++) {
			if (i % 2 == 1) {
				withoutEmpty[i] = hint[h++];
			} else {
				withoutEmpty[i] = -1;
			}
		}
		return withoutEmpty;
	}

	public static int[] merge(int[] solutionOfFree, int[] initial) {
		int[] copyOfInitial = Arrays.copyOf(initial, initial.length);
		int s = 0;
		for (int i = 0; i < copyOfInitial.length; i++) {
			if (copyOfInitial[i] < 1) {
				copyOfInitial[i] += solutionOfFree[s++] * -1;
			}
		}
		return copyOfInitial;
	}

	public static int[][] generateCombinations(int[] hint, int size) {
		int total = IntStream.of(hint).sum();
		int freeCount = size - total;
		if (total + hint.length - 1 > size) {
			throw new RuntimeException("Size not enough for given hint");
		}
		int[] initial = generateWithOutEmpty(hint);
		if (hint.length > 0) {
			freeCount = freeCount - (hint.length - 1);
		}

		List<int[]> combinations = BaseNumberTotal.getAllCombinationOfSum(freeCount, hint.length + 1);
		int[][] solutions = new int[combinations.size()][0];
		for (int i = 0; i < combinations.size(); i++) {
			solutions[i] = merge(combinations.get(i), initial);
		}
		return solutions;
	}

	public static int[] readForHint(Boolean[] line) {
		List<Integer> hint = new ArrayList<>();
		int groupCount = 0;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == null) {
				throw new RuntimeException("Undecided exists row cannot be readForHint");
			}
			if (line[i] == true) {
				groupCount++;
			}
			if (line[i] == false && groupCount > 0) {
				hint.add(groupCount);
				groupCount = 0;
			}
		}
		if (groupCount > 0) {
			hint.add(groupCount);
		}
		return hint.stream().mapToInt(i -> i).toArray();
	}

	public static boolean equals(List<int[]> actual, int[][] expected) {
		if (actual.size() != expected.length) {
			return false;
		}
		for (int i = 0; i < expected.length; i++) {
			int[] search = expected[i];
			boolean found = false;
			for (int k = 0; k < actual.size(); k++) {
				int[] a = actual.get(k);
				if (Arrays.equals(search, a)) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public static <T> boolean equals(T[][] actual, T[][] expected) {
		if (actual.length != expected.length) {
			return false;
		}
		for (int i = 0; i < expected.length; i++) {
			T[] search = expected[i];
			boolean found = false;
			for (int k = 0; k < actual.length; k++) {
				T[] a = actual[k];
				if (Arrays.equals(search, a)) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public static boolean equals(int[][] actual, int[][] expected) {
		if (actual.length != expected.length) {
			return false;
		}
		for (int i = 0; i < expected.length; i++) {
			int[] search = expected[i];
			boolean found = false;
			for (int k = 0; k < actual.length; k++) {
				int[] a = actual[k];
				if (Arrays.equals(search, a)) {
					found = true;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	public static Boolean[] generateCellLineFromNumericGroup(int numericGroup[]) {
		int absTotal = IntStream.of(numericGroup).map(i -> Math.abs(i)).sum();
		Boolean[] line = new Boolean[absTotal];
		int index = 0;
		for (int i = 0; i < numericGroup.length; i++) {
			int numericAbs = Math.abs(numericGroup[i]);
			for (int k = 0; k < numericAbs; k++, index++) {
				line[index] = numericGroup[i] > 0 ? true : false;
			}
		}
		return line;
	}

	public static boolean checkCombination(Boolean[] tableLine, Boolean[] combination) {
		if (tableLine.length != combination.length) {
			throw new RuntimeException(String.format("Table line length: %d does not match combination length: %d",
					tableLine.length, combination.length));
		}
		for (int i = 0; i < tableLine.length; i++) {
			if (tableLine[i] != null && tableLine[i] != combination[i]) {
				return false;
			}
		}
		return true;
	}

	public static Boolean[][] generateValidCombinations(Boolean[] line, int[] hint) {
		int[][] combinations = Solver.generateCombinations(hint, line.length);
		List<Boolean[]> validCombinations = new ArrayList<>();
		for (int i = 0; i < combinations.length; i++) {
			int[] combination = combinations[i];
			Boolean[] combinationAsCell = Solver.generateCellLineFromNumericGroup(combination);
			if (Solver.checkCombination(line, combinationAsCell)) {
				validCombinations.add(combinationAsCell);
			}
		}
		return validCombinations.toArray(new Boolean[validCombinations.size()][]);
	}

	public static Boolean[] decide(Boolean[][] combinations) {
		Boolean[] decided = Arrays.copyOf(combinations[0], combinations[0].length);
		for (int c = 0; c < decided.length; c++) {
			for (int r = 0; r < combinations.length && decided[c] != null; r++) {
				Boolean[] combination = combinations[r];
				if (combination.length != decided.length) {
					throw new RuntimeException("Combinations donot have the same length");
				}
				if (combination[c] != decided[c]) {
					decided[c] = null;
				}
			}
		}
		return decided;
	}

	public static boolean isFinished(Boolean[] line) {
		for (int i = 0; i < line.length; i++) {
			if (line[i] == null) {
				return false;
			}
		}
		return true;
	}

	public static Boolean[] solve(Boolean[] line, int[] hint) {
		if (isFinished(line)) {
			return line;
		}
		Boolean[][] combinations = generateValidCombinations(line, hint);
		return decide(combinations);
	}

	public static Boolean[] getRow(Boolean[][] square2D, int r) {
		return square2D[r];
	}

	public static Boolean[] getColumn(Boolean[][] square2D, int c) {
		Boolean[] line = new Boolean[square2D.length];
		for (int i = 0; i < square2D.length; i++) {
			line[i] = square2D[i][c];
		}
		return line;
	}

	public static void setRow(Boolean[][] square2d, Boolean[] row, int r) {
		square2d[r] = row;
	}

	public static void setColumn(Boolean[][] square2d, Boolean[] column, int c) {
		for (int i = 0; i < square2d.length; i++) {
			square2d[i][c] = column[i];
		}
	}

	public static int[][][] getRow0AndColumn1Hint(Boolean[][] filled) {
		int rowHint[][] = new int[filled.length][];
		int columnHint[][] = new int[filled[0].length][];
		for (int c = 0; c < filled[0].length; c++) {
			Boolean[] column = new Boolean[filled[0].length];
			for (int r = 0; r < filled.length; r++) {
				if (c == 0) {
					Boolean[] row = filled[r];
					rowHint[r] = Solver.readForHint(row);
				}
				column[r] = filled[r][c];
			}
			columnHint[c] = Solver.readForHint(column);
		}
		return new int[][][] { rowHint, columnHint };
	}

	public static void fillUndecided(Boolean[][] filled) {
		for (int r = 0; r < filled.length; r++) {
			for (int c = 0; c < filled[r].length; c++) {
				if (filled[r][c] == null) {
					filled[r][c] = false;
				}
			}
		}
	}

	public static void checkDone(int size, Boolean[][] filled, int[][][] hints) {
		checkHints(size, hints);
		checkSquare(filled);
		fillUndecided(filled);
		int[][][] hintsFromFilled = Solver.getRow0AndColumn1Hint(filled);
		for (int rc = 0; rc < hints.length; rc++) {
			for (int i = 0; i < size; i++) {
				if (Arrays.equals(hints[rc][i], hintsFromFilled[rc][i])) {
					throw new RuntimeException(String.format("Hint %s is not suitable for %s:%d",
							Arrays.toString(hints[rc][i]), rc == 0 ? "Row" : "Column", i));
				}
			}
		}
	}

	public static void checkSquare(Boolean[][] filled) {
		for (int i = 0; i < filled.length; i++) {
			if (filled[i].length != filled[0].length) {
				throw new RuntimeException("Not a square");
			}
		}
	}

	public static void checkHints(int size, int[][][] hints) {
		int[] rowHintColumnHint = new int[] { 0, 0 };
		if (hints.length != 2) {
			throw new RuntimeException("Hints length must be 2 for row and column");
		}
		for (int rc = 0; rc < 2; rc++) {
			for (int r = 0; r < hints[rc].length; r++) {
				for (int c = 0; c < hints[rc][r].length; c++) {
					rowHintColumnHint[rc] += hints[rc][r][c];
				}
			}
		}
		if (rowHintColumnHint[0] != rowHintColumnHint[1]) {
			throw new RuntimeException(String.format("Row hint total:% is not equals to column hint total:%d",
					rowHintColumnHint[0], rowHintColumnHint[1]));
		}
	}

	public static Nonogram load(String fileName) {
		try {
			File file = new File(String.format("%s/%s.%s", saveFolder, fileName, fileTypeNono));
			if (!file.exists() || !file.isFile()) {
				throw new RuntimeException("File not found");
			}
			StringBuilder sb = new StringBuilder();
			try (FileInputStream fis = new FileInputStream(file)) {
				while (fis.available() > 0) {
					sb.append((char) fis.read());
				}
			}
			Gson gson = new Gson();
			Nonogram nonogram = gson.fromJson(sb.toString(), Nonogram.class);
			if (nonogram.getFilled() == null) {
				nonogram.setFilled(new Boolean[nonogram.getSize()][nonogram.getSize()]);
			}
			return nonogram;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static final String saveFolder = "saved";
	public static final String knowledgeFolder = "kb";
	public static final String fileTypeNono = "nono";

	public static void save(String fileName, Nonogram nonogram) {
		try {
			Gson gson = new Gson();
			File folder = new File(saveFolder);
			if (folder.isFile()) {
				folder.delete();
			}
			if (!folder.exists()) {
				folder.mkdirs();
			}
			if (!isDirty(nonogram.getFilled())) {
				nonogram.setFilled(null);
			}
			String tableJson = gson.toJson(nonogram);
			File file = new File(String.format("%s/%s.%s", folder.getName(), fileName, fileTypeNono));
			if (!file.exists()) {
				file.createNewFile();
			}
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(tableJson.getBytes());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static int getUnsolvedCount(Boolean[][] filled) {
		int unsolvedCount = 0;
		for (int r = 0; r < filled.length; r++) {
			for (int c = 0; c < filled[r].length; c++) {
				if (filled[r][c] == null) {
					unsolvedCount++;
				}
			}
		}
		return unsolvedCount;
	}

	public static boolean isDirty(Boolean[][] filled) {
		for (int r = 0; r < filled.length; r++) {
			for (int c = 0; c < filled[r].length; c++) {
				if (filled[r][c] != null) {
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String arg[]) throws FileNotFoundException, IOException {
		Gson gson = new Gson();
		StringBuilder sb = new StringBuilder();
		try (FileInputStream fis = new FileInputStream("cat.txt")) {
			while (fis.available() > 0) {
				sb.append((char) fis.read());
			}
		}
		Table table = gson.fromJson(sb.toString(), Table.class);
		Nonogram nonogram = new Nonogram(25);
		int[][][] hints = new int[2][][];
		hints[0] = table.getRowHint();
		hints[1] = table.getColumnHint();
		nonogram.setHints(hints);
		save("cat", nonogram);
		nonogram = load("cat");
	}
}
