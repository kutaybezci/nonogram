package nonogram.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseNumberTotal {

	private int[] number;
	private int total;
	private int operandCount;

	public BaseNumberTotal(int total, int operandCount) {
		this.total = total;
		this.operandCount = operandCount;
		this.number = new int[operandCount];
		this.number[0] = total;
	}

	public BaseNumberTotal(int number[]) {
		this.number = number;
		this.total = Arrays.stream(this.number).sum();
		this.operandCount = number.length;
	}

	public int[] getNumber() {
		return number;
	}

	public int[] increment() {
		if (this.operandCount == 1) {
			return null;
		}
		if (this.number[1] == total) {
			return null;
		} else {
			boolean added = false;
			for (int i = operandCount - 1; i > 0 && !added; i--) {
				if (number[i] == total) {
					number[i] = 0;
				} else {
					number[i] = number[i] + 1;
					added = true;
					int t = 0;
					for (int k = 1; k < operandCount; k++) {
						t += number[k];
					}
					number[0] = total - t;
					if (number[0] < 0) {
						return number;// increment();
					}
				}
			}
		}
		return number;
	}

	public static List<int[]> getAllCombinationOfSum(int total, int operandCount) {
		List<int[]> allCombinationOfSum = new ArrayList<>();
		BaseNumberTotal baseNumberTotal = new BaseNumberTotal(total, operandCount);
		boolean exit = false;
		do {
			int[] a = Arrays.copyOf(baseNumberTotal.getNumber(), operandCount);
			if (a[0] >= 0) {
				allCombinationOfSum.add(a);
			}
			int[] c = baseNumberTotal.increment();
			if (c == null) {
				exit = true;
			}
		} while (!exit);
		return allCombinationOfSum;
	}

}
