package nonogram;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import nonogram.solver.Table;

public class TableTest {
	@Test
	public void testPlus() {
		Boolean[][] plus = new Boolean[][] { //
				{ false, true, false }, //
				{ true, true, true }, //
				{ false, true, false } };
		Table table = new Table(plus);
		Table solved = new Table(table.getRowHint(), table.getColumnHint());
		solved.solve();
		assertArrayEquals(plus, solved.getFilled());
	}

	@Test
	public void testQ() {
		Boolean[][] plus = new Boolean[][] { //
				{ true, true, true }, //
				{ true, false, true }, //
				{ true, true, true } };
		Table table = new Table(plus);
		Table solved = new Table(table.getRowHint(), table.getColumnHint());
		solved.solve();
		assertArrayEquals(plus, solved.getFilled());
	}

	@Test
	public void testTriangle() {
		Boolean[][] plus = new Boolean[][] { //
				{ true, false, false }, //
				{ true, true, false }, //
				{ true, true, true } };
		Table table = new Table(plus);
		Table solved = new Table(table.getRowHint(), table.getColumnHint());
		solved.solve();
		assertArrayEquals(plus, solved.getFilled());
	}

	@Test
	public void testT() {
		Boolean[][] plus = new Boolean[][] { //
				{ true, true, true }, //
				{ false, true, false }, //
				{ false, true, false } };
		Table table = new Table(plus);
		Table solved = new Table(table.getRowHint(), table.getColumnHint());
		solved.solve();
		assertArrayEquals(plus, solved.getFilled());
	}

	@Test
	public void testL() {
		Boolean[][] plus = new Boolean[][] { //
				{ false, true, false }, //
				{ false, true, false }, //
				{ false, true, true } };
		Table table = new Table(plus);
		Table solved = new Table(table.getRowHint(), table.getColumnHint());
		solved.solve();
		assertArrayEquals(plus, solved.getFilled());
	}

}
