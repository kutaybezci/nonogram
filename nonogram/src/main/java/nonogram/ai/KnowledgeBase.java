package nonogram.ai;

import java.io.BufferedWriter;
import java.io.File;
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

	public KnowledgeBase(File disk, int lineSize, int[][][] hints) {
		this.disk = disk;
		this.lineSize = lineSize;
		if (!disk.exists()) {
			try {
				disk.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		remember(hints);
		learn();
	}

	private void learn() {
		try (FileWriter fr = new FileWriter(disk, true); BufferedWriter br = new BufferedWriter(fr)) {
			Gson gson = new Gson();
			for (Knowledge knowledge : memory.values()) {
				if (knowledge.getSolutions() == null) {
					knowledge.setSolutions(
							Solver.generateValidCombinations(new Boolean[knowledge.getSize()], knowledge.getHint()));
					String strKnowledge = gson.toJson(new LitteKnowledge(knowledge));
					br.write(strKnowledge);
					br.newLine();
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
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
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public Knowledge get(int[] hint) {
		return memory.get(new Gson().toJson(hint));
	}

}
