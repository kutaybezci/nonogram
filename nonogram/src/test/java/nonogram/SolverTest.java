package nonogram;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import nonogram.solver.Solver;

public class SolverTest {

	@Test
	public void testReadForHint() {
		int[] hint = { 1, 2, 3 };
		Boolean[] cellLine123 = new Boolean[] { false, false, true, false, true, true, false, true, true, true };
		int[] hintCalculated = Solver.readForHint(cellLine123);
		assertArrayEquals(hint, hintCalculated);
	}

	@Test
	public void testReadForHintEmpty() {
		int[] hint = {};
		Boolean[] empty = { false, false };
		int[] hintCalculated = Solver.readForHint(empty);
		assertArrayEquals(hint, hintCalculated);
	}

	@Test
	public void testReadForHintUndecided() {
		Boolean[] undecided = new Boolean[] { false, true, null, false };
		assertThrows(RuntimeException.class, () -> {
			Solver.readForHint(undecided);
		});
	}

	@Test
	public void testGenerateWithoutEmpty1() {
		assertArrayEquals(new int[] { 0, 1, 0 }, Solver.generateWithOutEmpty(new int[] { 1 }));
	}

	@Test
	public void testGenerateWithoutEmpty() {
		assertArrayEquals(new int[] { 0 }, Solver.generateWithOutEmpty(new int[] {}));
	}

	@Test
	public void testGenerateWithoutEmpty123() {
		assertArrayEquals(new int[] { 0, 1, -1, 2, -1, 3, 0 }, Solver.generateWithOutEmpty(new int[] { 1, 2, 3 }));
	}

	@Test
	public void testEqualsOrder() {
		int[][] array = new int[][] { { 1, 2, 3 }, { 3, 2, 1 }, { 2 } };
		List<int[]> list = new ArrayList<>();
		for (int i = array.length - 1; i >= 0; i--) {
			list.add(array[i]);
		}
		assertTrue(Solver.equals(list, array));
	}

	@Test
	public void testEqualsArrayOrder() {
		int[][] array = new int[][] { { 1, 2, 3 }, { 3, 2, 1 }, { 2 } };
		int[][] array2 = new int[][] { { 2 }, { 1, 2, 3 }, { 3, 2, 1 } };
		assertTrue(Solver.equals(array, array2));
	}

	@Test
	public void testNotEqualsSize() {
		int[][] array = new int[][] { { 1, 2, 3 }, { 3, 2, 1 }, { 2 } };
		List<int[]> list = new ArrayList<>();
		for (int i = array.length - 1; i > 0; i--) {
			list.add(array[i]);
		}
		assertFalse(Solver.equals(list, array));
	}

	@Test
	public void testNotEqualsArraySize() {
		int[][] array = new int[][] { { 1, 2, 3 }, { 3, 2, 1 }, { 2 } };
		int[][] array2 = new int[][] { { 1, 2, 3 }, { 2 } };
		assertFalse(Solver.equals(array2, array));
	}

	@Test
	public void testNotEqualsSubOrder() {
		int[][] array = new int[][] { { 1, 3, 2 }, { 3, 2, 1 }, { 2 } };
		int[][] arrayToCheck = new int[][] { { 1, 2, 3 }, { 3, 2, 1 }, { 2 } };
		List<int[]> list = new ArrayList<>();
		for (int i = array.length - 1; i >= 0; i--) {
			list.add(array[i]);
		}
		assertFalse(Solver.equals(list, arrayToCheck));
	}

	@Test
	public void testNotEqualsArraySubOrder() {
		int[][] array = new int[][] { { 1, 2, 3 }, { 3, 2, 1 }, { 2 } };
		int[][] array2 = new int[][] { { 2 }, { 1, 3, 2 }, { 3, 2, 1 } };
		assertFalse(Solver.equals(array, array2));
	}

	@Test
	public void testGenerateCombinations5_5() {
		int[][] expected = new int[][] { { 0, 5, 0 } };
		assertTrue(Solver.equals(Solver.generateCombinations(new int[] { 5 }, 5), expected));
	}

	@Test
	public void testGenerateCombinations5_6() {
		int[][] expected = new int[][] { { -1, 5, 0 }, { 0, 5, -1 } };
		assertTrue(Solver.equals(Solver.generateCombinations(new int[] { 5 }, 6), expected));
	}

	@Test
	public void testGenerateCombinationsNULL_6() {
		int[][] expected = new int[][] { { -6 } };
		assertTrue(Solver.equals(Solver.generateCombinations(new int[] {}, 6), expected));
	}

	@Test
	public void testGenerateCombinations6_5() {
		assertThrows(RuntimeException.class, () -> Solver.generateCombinations(new int[] { 1, 2 }, 3));
	}

	@Test
	public void testGenerateCellLineFromNumericGroup() {
		int[] numericGroup = new int[] { -2, +1, -1, +2, -1, +3 };
		Boolean[] cellLine123 = new Boolean[] { false, false, true, false, true, true, false, true, true, true };
		assertArrayEquals(cellLine123, Solver.generateCellLineFromNumericGroup(numericGroup));
	}

	@Test
	public void testCheckCombinationTrue() {
		Boolean[] cellLine123 = new Boolean[] { false, false, true, false, true, true, false, true, true, true };
		Boolean[] line = Arrays.copyOf(cellLine123, cellLine123.length);
		line[3] = null;
		assertTrue(Solver.checkCombination(line, cellLine123));
	}

	@Test
	public void testCheckCombinationFalse() {
		Boolean[] cellLine123 = new Boolean[] { false, false, true, false, true, true, false, true, true, true };
		Boolean[] line = Arrays.copyOf(cellLine123, cellLine123.length);
		line[2] = false;
		assertFalse(Solver.checkCombination(line, cellLine123));
	}

	@Test
	public void testGenerateValidCombinations() {
		Boolean[][] expected = new Boolean[][] { { false, true, false, true }, { true, false, false, true } };
		Boolean[][] expected2 = new Boolean[][] { { true, false, false, true }, { false, true, false, true } };
		Boolean[][] allValidCombination = Solver
				.generateValidCombinations(new Boolean[] { null, null, null, true }, new int[] { 1, 1 });
		assertTrue(Solver.equals(allValidCombination, expected));
		assertTrue(Solver.equals(allValidCombination, expected2));
	}

	@Test
	public void testGenerateValidCombinationsFalse() {
		Boolean[][] expected = new Boolean[][] { { false, true, false, true }, { true, false, true, false } };
		Boolean[][] allValidCombination = Solver
				.generateValidCombinations(new Boolean[] { null, null, null, true }, new int[] { 1, 1 });
		assertFalse(Solver.equals(allValidCombination, expected));
	}

	@Test
	public void testDecide() {
		Boolean[][] combinations = new Boolean[][] { //
				{ false, true, false, true }, //
				{ true, false, false, true } };

		assertArrayEquals(new Boolean[] { null, null, false, true }, Solver.decide(combinations));
	}

	@Test
	public void testDecide3() {
		Boolean[][] combinations = new Boolean[][] { //
				{ false, true, false, true }, //
				{ true, false, false, true }, //
				{ true, false, true, false } };

		assertArrayEquals(new Boolean[] { null, null, null, null }, Solver.decide(combinations));

	}

	@Test
	public void testSolve() {
		Boolean[] line = new Boolean[] { null, null, null };
		int[] hint = new int[] { 2 };
		Boolean[] expected = new Boolean[] { null, true, null };
		assertArrayEquals(expected, Solver.solve(line, hint));
	}
}
