package nonogram;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nonogram.solver.BaseNumberTotal;
import nonogram.solver.Solver;

public class BaseNumberTotalTest {
	private static final int[][] c1_0 = new int[][] { { 0 } };
	private static final int[][] c2_0 = new int[][] { { 0, 0 } };
	private static final int[][] c1_1 = new int[][] { { 1 } };
	private static final int[][] c1_8 = new int[][] { { 8 } };
	private static final int[][] c2_2 = new int[][] { { 2, 0 }, { 1, 1 }, { 0, 2 } };
	private static final int[][] c3_1 = new int[][] { { 1, 0, 0 }, { 0, 0, 1 }, { 0, 1, 0 } };

	@Test
	public void test1_0() {
		assertTrue(Solver.equals(BaseNumberTotal.getAllCombinationOfSum(0, 1), c1_0));
	}

	@Test
	public void test2_0() {
		assertTrue(Solver.equals(BaseNumberTotal.getAllCombinationOfSum(0, 2), c2_0));
	}

	@Test
	public void test1_1() {
		assertTrue(Solver.equals(BaseNumberTotal.getAllCombinationOfSum(1, 1), c1_1));
	}

	@Test
	public void test8_1() {
		assertTrue(Solver.equals(BaseNumberTotal.getAllCombinationOfSum(8, 1), c1_8));
	}

	@Test
	public void test2_2() {
		assertTrue(Solver.equals(BaseNumberTotal.getAllCombinationOfSum(2, 2), c2_2));
	}

	@Test
	public void test1_3() {
		assertTrue(Solver.equals(BaseNumberTotal.getAllCombinationOfSum(1, 3), c3_1));
	}

	@Test
	public void test12_6() {
		BaseNumberTotal.getAllCombinationOfSum(12, 6);
	}
}
