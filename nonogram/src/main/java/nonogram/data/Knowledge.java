package nonogram.data;

public class Knowledge {
	private String key;
	private int size;
	private int[] hint;
	private Boolean[][] solutions;

	public Knowledge(String key, int size, int[] hint, Boolean[][] solutions) {
		this.key = key;
		this.size = size;
		this.hint = hint;
		this.solutions = solutions;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int[] getHint() {
		return hint;
	}

	public void setHint(int[] hint) {
		this.hint = hint;
	}

	public Boolean[][] getSolutions() {
		return solutions;
	}

	public void setSolutions(Boolean[][] solutions) {
		this.solutions = solutions;
	}

}
