package nonogram.ai;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

import nonogram.data.Knowledge;
import nonogram.data.LitteKnowledge;
import nonogram.solver.Solver;

public class KnowledgeBase {
	private int lineSize;
	private File disk;
	private Map<String, Knowledge> memory = new HashMap<>();

	public KnowledgeBase(int lineSize, int[][][] hints) {
		this.lineSize = lineSize;
		this.disk = new File(String.format("kb/%d.txt", this.lineSize));
		try {
			this.disk.getParentFile().mkdirs();
			if (!this.disk.exists()) {
				this.disk.createNewFile();
			}
		} catch (IOException ex) {
			System.out.println("AI cannot learn or remember");
			ex.printStackTrace();
			this.disk = null;
		}
		remember(hints);
		learn();
	}

	private void learn() {
		FileWriter fr = null;
		BufferedWriter br = null;
		try {
			if (this.disk != null) {
				try {
					fr = new FileWriter(disk, true);
					br = new BufferedWriter(fr);
				} catch (IOException ex) {
					this.disk = null;
					ex.printStackTrace();
				}
			}

			Gson gson = new Gson();
			for (Knowledge knowledge : memory.values()) {
				if (knowledge.getSolutions() == null) {
					knowledge.setSolutions(
							Solver.generateValidCombinations(new Boolean[knowledge.getSize()], knowledge.getHint()));
					String strKnowledge = gson.toJson(new LitteKnowledge(knowledge));
					if (this.disk != null && br != null) {
						try {
							br.write(strKnowledge);
							br.newLine();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}
			}
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void remember(int[][][] hints) {
		Gson gson = new Gson();
		for (int rc = 0; rc < 2; rc++) {
			for (int i = 0; i < lineSize; i++) {
				int[] hint = hints[rc][i];
				String key = gson.toJson(hint);
				memory.put(key, new Knowledge(key, this.lineSize, hint, null));
			}
		}
		if (this.disk != null) {
			try (Scanner s = new Scanner(this.disk)) {
				while (s.hasNextLine()) {
					String strKnowledge = s.nextLine();
					LitteKnowledge litteKnowledge = gson.fromJson(strKnowledge, LitteKnowledge.class);
					String knowledgeKey = gson.toJson(litteKnowledge.getHint());
					if (memory.containsKey(knowledgeKey)) {
						Knowledge knowledge = litteKnowledge.toKnowledge();
						memory.put(knowledgeKey, knowledge);
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public Knowledge get(int[] hint) {
		return memory.get(new Gson().toJson(hint));
	}

}
