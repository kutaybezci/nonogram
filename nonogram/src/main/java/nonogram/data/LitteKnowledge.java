package nonogram.data;

public class LitteKnowledge {

	private String key;
	private int size;
	private int[] hint;
	private String[] solutions;

	public LitteKnowledge(Knowledge knowledge) {
		this.key = knowledge.getKey();
		this.size = knowledge.getSize();
		this.hint = knowledge.getHint();
		this.solutions = convert(knowledge.getSolutions());
	}

	public static String[] convert(Boolean[][] solutions) {
		String[] zipped = new String[solutions.length];
		for (int i = 0; i < solutions.length; i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < solutions[i].length; j++) {
				Boolean filled = solutions[i][j];
				sb.append(filled == null ? 'N' : filled == true ? 'T' : 'F');
			}
			zipped[i] = sb.toString();
		}
		return zipped;
	}

	public Knowledge toKnowledge() {
		Boolean[][] knowledgeSolutions = new Boolean[this.solutions.length][this.size];
		for (int i = 0; i < knowledgeSolutions.length; i++) {
			for (int j = 0; j < knowledgeSolutions[i].length; j++) {
				char tfn = this.solutions[i].charAt(j);
				knowledgeSolutions[i][j] = tfn == 'T' ? true : false;
			}
		}
		Knowledge knowledge = new Knowledge(key, size, hint, knowledgeSolutions);
		return knowledge;
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

	public String[] getSolutions() {
		return solutions;
	}

	public void setSolutions(String[] solutions) {
		this.solutions = solutions;
	}
}
