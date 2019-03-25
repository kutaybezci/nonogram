package nonogram.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import nonogram.solver.Table;

public class NonogramTableConsole extends Thread {
	private String info = "";
	private String input = "";
	private int x, y;
	private boolean exit = false;
	private TerminalScreen terminalScreen;
	private int size;
	private Table table;
	private boolean help = true;
	private boolean solving = false;

	public NonogramTableConsole(Terminal terminal) throws IOException {
		this.terminalScreen = new TerminalScreen(terminal);
		this.terminalScreen.startScreen();
		this.size = 10;
		this.table = new Table(this.size);
	}

	private void move() {
		if (this.x >= this.size) {
			this.x = 0;
		}
		if (this.y >= this.size) {
			this.y = 0;
		}
		if (this.x < 0) {
			this.x = this.size - 1;
		}
		if (this.y < 0) {
			this.y = this.size - 1;
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
			this.table.setCell(this.y, this.x, Boolean.TRUE);
		} else if (keyStroke.getKeyType() == KeyType.Delete) {
			this.table.setCell(this.y, this.x, Boolean.FALSE);
		} else if (keyStroke.getKeyType() == KeyType.End) {
			this.table.setCell(this.y, this.x, null);
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
			this.size = Integer.parseInt(commandParts[1]);
			this.table = new Table(this.size);
		} else if (StringUtils.equalsIgnoreCase("clear", commandParts[0])) {
			this.table.clearTable();
		} else if (StringUtils.equalsIgnoreCase("calc", commandParts[0])) {
			this.table.calculateHintFromTable();
		} else if (StringUtils.equalsIgnoreCase("help", commandParts[0])) {
			this.help = true;
		} else if (StringUtils.equalsIgnoreCase("solve", commandParts[0])) {
			this.solving = true;
		} else if (StringUtils.equalsIgnoreCase("done", commandParts[0])) {
			this.table.checkDone();
			this.info = "WELL DONE";
		} else if (StringUtils.equalsIgnoreCase("save", commandParts[0])) {
			save(commandParts[1], this.table);
		} else if (StringUtils.equalsIgnoreCase("load", commandParts[0])) {
			this.table = load(commandParts[1]);
			this.size = this.table.getRowHint().length;
		} else {
			this.info = "commands: r/c/resize/clear/calc/solve/help/done";
		}
	}

	private static Table load(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists() || !file.isFile()) {
				throw new RuntimeException("File not found");
			}
			StringBuilder sb = new StringBuilder();
			try (FileInputStream fis = new FileInputStream(file)) {
				while (fis.available() > 0) {
					sb.append((char) fis.read());
				}
			}
			Gson gson = new Gson();
			return gson.fromJson(sb.toString(), Table.class);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void save(String filePath, Table table) {
		try {
			Gson gson = new Gson();
			String tableJson = gson.toJson(table);
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			try (FileOutputStream fos = new FileOutputStream(file)) {
				fos.write(tableJson.getBytes());
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
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
		this.table.getRowHint()[this.y] = hint;
	}

	private void setColumnHint(String[] commandParts) {
		int hint[] = getHintFromCommand(commandParts);
		this.table.getColumnHint()[this.x] = hint;
	}

	private void drawNonogramTable() {
		for (int r = 0; r < this.size; r++) {
			for (int c = 0; c < this.size; c++) {
				Boolean filled = this.table.getCell(r, c);
				TextColor backcolor = filled == null ? TextColor.ANSI.CYAN
						: filled ? TextColor.ANSI.RED : TextColor.ANSI.WHITE;
				this.terminalScreen.setCharacter(c, r, new TextCharacter(' ', TextColor.ANSI.RED, backcolor));
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
			int[] rowHint = table.getRowHint()[this.y];
			int[] columnHint = table.getColumnHint()[this.x];
			textGraphics.putString(0, this.size, String.format("RH: %s", hintToString(rowHint)));
			textGraphics.putString(0, this.size + 1, String.format("CH: %s", hintToString(columnHint)));
			textGraphics.putString(0, this.size + 2, String.format("> %s", this.input));
			textGraphics.putString(0, this.size + 3, this.info);
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
				} else {
					if (solving) {
						int unsolved = this.table.solveStep();
						if (unsolved > 0) {
							this.info = String.format("Solving %d unsolved remaining", table.getUnsolvedCount());
						} else {
							this.info = "SOLVED";
							this.solving = false;
						}
						draw();
					}
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
			NonogramTableConsole nonogramTableConsole = new NonogramTableConsole(terminal);
			nonogramTableConsole.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
