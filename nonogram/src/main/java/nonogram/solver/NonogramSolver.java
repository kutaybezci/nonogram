package nonogram.solver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nonogram.ai.KnowledgeBase;

public class NonogramSolver {

	private int size;
	private int[][][] hints;
	private Boolean[][] filled;
	private Boolean[][][][] allSolutions;
	private KnowledgeBase knowledgeBase;

	public NonogramSolver(int size, int[][][] hints, File disk) {
		this.hints = hints;
		this.size = size;
		this.allSolutions = new Boolean[2][this.size][][];
		this.filled = new Boolean[this.size][this.size];
		knowledgeBase = new KnowledgeBase(disk, this.size, this.hints);
	}

	public int countNull() {
		return Solver.getUnsolvedCount(this.filled);
	}

	public int solveStep() {
		for (int rc = 0; rc < 2; rc++) {
			for (int i = 0; i < size; i++) {
				Boolean line[] = rc == 0 ? Solver.getRow(this.filled, i) : Solver.getColumn(this.filled, i);
				Boolean[][] solutions = allSolutions[rc][i];
				if (solutions == null) {
					solutions = knowledgeBase.get(hints[rc][i]).getSolutions();
				}
				List<Boolean[]> validCombinations = new ArrayList<>();
				for (Boolean[] solution : solutions) {
					if (Solver.checkCombination(line, solution)) {
						validCombinations.add(solution);
					}
				}
				Boolean[][] solved = validCombinations.toArray(new Boolean[0][0]);
				allSolutions[rc][i] = solved;
				Boolean[] toBeFilled = Solver.decide(solved);
				if (rc == 0) {
					Solver.setRow(this.filled, toBeFilled, i);
				} else {
					Solver.setColumn(this.filled, toBeFilled, i);
				}
			}
		}
		return countNull();
	}

	public void solve() {
		while (countNull() > 0) {
			solveStep();
		}
	}

	public static void main(String arg[]) {
		Boolean[][] filled = new Boolean[][] { //
				{ true, false, false }, //
				{ true, true, false }, //
				{ true, true, true } };
		int[][][] hints = Solver.getRow0AndColumn1Hint(filled);
		File kb = new File("c:\\kb\\3.kb");
		NonogramSolver solver = new NonogramSolver(3, hints, kb);
		solver.solve();
		for (int r = 0; r < solver.size; r++) {
			for (int c = 0; c < solver.size; c++) {
				System.out.print(solver.filled[r][c]);
			}
			System.out.println();
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int[][][] getHints() {
		return hints;
	}

	public void setHints(int[][][] hints) {
		this.hints = hints;
	}

	public Boolean[][] getFilled() {
		return filled;
	}

	public void setFilled(Boolean[][] filled) {
		this.filled = filled;
	}

	public Boolean[][][][] getAllSolutions() {
		return allSolutions;
	}

	public void setAllSolutions(Boolean[][][][] allSolutions) {
		this.allSolutions = allSolutions;
	}

	public KnowledgeBase getKnowledgeBase() {
		return knowledgeBase;
	}

	public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
		this.knowledgeBase = knowledgeBase;
	}
}
