package nonogram.console;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import nonogram.data.Nonogram;
import nonogram.solver.NonogramSolver;
import nonogram.solver.Solver;

public class NonogramConsole extends Thread {
	private String info = "";
	private String input = "";
	private int x, y;
	private boolean exit = false;
	private TerminalScreen terminalScreen;
	private Nonogram nonogram;
	private boolean help = true;

	public NonogramConsole(Terminal terminal) throws IOException {
		this.terminalScreen = new TerminalScreen(terminal);
		this.terminalScreen.startScreen();
		this.nonogram = new Nonogram(10);
	}

	private void move() {
		if (this.x >= this.nonogram.getSize()) {
			this.x = 0;
		}
		if (this.y >= this.nonogram.getSize()) {
			this.y = 0;
		}
		if (this.x < 0) {
			this.x = this.nonogram.getSize() - 1;
		}
		if (this.y < 0) {
			this.y = this.nonogram.getSize() - 1;
		}
	}

	private void drawHelp() {
		ClassLoader classLoader = getClass().getClassLoader();
		File helpFile = new File(classLoader.getResource("help.txt").getFile());
		try (Scanner s = new Scanner(helpFile)) {
			TextGraphics helpText = this.terminalScreen.newTextGraphics();
			helpText.setForegroundColor(TextColor.ANSI.GREEN);
			for (int i = 0; s.hasNextLine(); i++) {
				helpText.putString(0, i, s.nextLine());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void processInput(KeyStroke keyStroke) {
		if (keyStroke.getKeyType() == KeyType.Escape || keyStroke.getKeyType() == KeyType.EOF) {
			this.exit = true;
		} else if (keyStroke.getKeyType() == KeyType.ArrowDown) {
			this.y++;
		} else if (keyStroke.getKeyType() == KeyType.ArrowUp) {
			this.y--;
		} else if (keyStroke.getKeyType() == KeyType.ArrowLeft) {
			this.x--;
		} else if (keyStroke.getKeyType() == KeyType.ArrowRight) {
			this.x++;
		} else if (keyStroke.getKeyType() == KeyType.Enter) {
			processCommand(this.input);
			this.input = "";
		} else if (keyStroke.getKeyType() == KeyType.Character) {
			this.input += keyStroke.getCharacter();
		} else if (keyStroke.getKeyType() == KeyType.Backspace) {
			this.input = "";
		} else if (keyStroke.getKeyType() == KeyType.Insert) {
			this.nonogram.setCell(this.y, this.x, Boolean.TRUE);
		} else if (keyStroke.getKeyType() == KeyType.Delete) {
			this.nonogram.setCell(this.y, this.x, Boolean.FALSE);
		} else if (keyStroke.getKeyType() == KeyType.End) {
			this.nonogram.setCell(this.y, this.x, null);
		}
		move();
	}

	private void processCommand(String command) {
		if (StringUtils.isBlank(command)) {
			return;
		}
		String[] commandParts = StringUtils.split(command);
		if (StringUtils.equalsIgnoreCase("r", commandParts[0])) {
			setRowHint(commandParts);
		} else if (StringUtils.equalsIgnoreCase("c", commandParts[0])) {
			setColumnHint(commandParts);
		} else if (StringUtils.equalsIgnoreCase("resize", commandParts[0])) {
			int size = Integer.parseInt(commandParts[1]);
			this.nonogram = new Nonogram(size);
		} else if (StringUtils.equalsIgnoreCase("clear", commandParts[0])) {
			this.nonogram.clear();
		} else if (StringUtils.equalsIgnoreCase("calc", commandParts[0])) {
			this.nonogram.setHints(Solver.getRow0AndColumn1Hint(this.nonogram.getFilled()));
		} else if (StringUtils.equalsIgnoreCase("help", commandParts[0])) {
			this.help = true;
		} else if (StringUtils.equalsIgnoreCase("solve", commandParts[0])) {
			solve();
		} else if (StringUtils.equalsIgnoreCase("done", commandParts[0])) {
			Solver.checkDone(nonogram.getSize(), nonogram.getFilled(), nonogram.getHints());
			this.info = "WELL DONE";
		} else if (StringUtils.equalsIgnoreCase("save", commandParts[0])) {
			Solver.save(commandParts[1], nonogram);
		} else if (StringUtils.equalsIgnoreCase("load", commandParts[0])) {
			this.nonogram = Solver.load(commandParts[1]);
		} else {
			this.info = "commands: r/c/resize/clear/calc/solve/help/done";
		}
	}

	private void solve() {
		Solver.checkHints(this.nonogram.getSize(), this.nonogram.getHints());
		File file = new File(String.format("%s/%d.txt", Solver.knowledgeFolder, this.nonogram.getSize()));
		NonogramSolver solver = new NonogramSolver(this.nonogram.getSize(), this.nonogram.getHints(), file);
		solver.solve();
		this.nonogram.setFilled(solver.getFilled());
	}

	private static int[] getHintFromCommand(String[] commandParts) {
		int[] hint = new int[commandParts.length - 1];
		for (int i = 1; i < commandParts.length; i++) {
			hint[i - 1] = Integer.parseInt(commandParts[i]);
		}
		return hint;
	}

	private void setRowHint(String[] commandParts) {
		int hint[] = getHintFromCommand(commandParts);
		this.nonogram.getHints()[0][this.y] = hint;
	}

	private void setColumnHint(String[] commandParts) {
		int hint[] = getHintFromCommand(commandParts);
		this.nonogram.getHints()[1][this.x] = hint;
	}

	private void drawNonogramTable() {
		Boolean[][] table = this.nonogram.getFilled();
		for (int r = 0; r < this.nonogram.getSize(); r++) {
			for (int c = 0; c < this.nonogram.getSize(); c++) {
				Boolean filled = table[r][c];
				TextColor backcolor = filled == null ? TextColor.ANSI.CYAN
						: filled ? TextColor.ANSI.RED : TextColor.ANSI.WHITE;
				this.terminalScreen.setCharacter(c, r, new TextCharacter(' ', TextColor.ANSI.YELLOW, backcolor));
			}
		}
	}

	private void draw() {
		this.terminalScreen.clear();
		if (this.help) {
			this.terminalScreen.setCursorPosition(new TerminalPosition(0, 0));
			drawHelp();
			this.help = false;
		} else {
			this.terminalScreen.setCursorPosition(new TerminalPosition(this.x, this.y));
			drawNonogramTable();
			TextGraphics textGraphics = this.terminalScreen.newTextGraphics();
			textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
			textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
			int[] rowHint = nonogram.getHints()[0][this.y];
			int[] columnHint = nonogram.getHints()[1][this.x];
			textGraphics.putString(0, this.nonogram.getSize(), String.format("RH: %s", hintToString(rowHint)));
			textGraphics.putString(0, this.nonogram.getSize() + 1, String.format("CH: %s", hintToString(columnHint)));
			textGraphics.putString(0, this.nonogram.getSize() + 2, String.format("> %s", this.input));
			textGraphics.putString(0, this.nonogram.getSize() + 3, this.info);
		}
		try {
			this.terminalScreen.refresh();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String hintToString(int hint[]) {
		StringBuilder sb = new StringBuilder();
		if (hint != null && hint.length > 0) {
			for (int i = 0; i < hint.length; i++) {
				sb.append(hint[i]);
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	@Override
	public void run() {
		draw();
		while (!this.exit) {
			try {
				KeyStroke keyStroke = this.terminalScreen.pollInput();
				if (keyStroke != null) {
					processInput(keyStroke);
					draw();
				}
				this.terminalScreen.doResizeIfNecessary();
				yield();
			} catch (Exception ex) {
				ex.printStackTrace();
				this.info = ex.getMessage();
			}
		}
		try {
			this.terminalScreen.stopScreen();
			this.terminalScreen.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String arg[]) {
		try {
			DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
			Terminal terminal = defaultTerminalFactory.createTerminal();
			NonogramConsole nonogramTableConsole = new NonogramConsole(terminal);
			nonogramTableConsole.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
