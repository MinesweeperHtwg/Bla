package minesweeper.aview.tui;

import static java.lang.Integer.parseInt;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import minesweeper.controller.IMinesweeperController;
import minesweeper.util.observer.Event;
import minesweeper.util.observer.IObserver;

public class TextUI implements IObserver {
	private static final String COMMANDS = "Commands: q|quit; n|new|newGame; s|set HEIGHT WIDTH MINES; o|open ROW COL; a|around ROW COL; f|flag ROW COL";

	private static final String NEWLINE = System.getProperty("line.separator");

	private static final Logger LOGGER = Logger.getLogger(TextUI.class);

	private IMinesweeperController controller;

	@Inject
	public TextUI(IMinesweeperController controller) {
		this.controller = controller;
		controller.addObserver(this);
		printTUI();
	}

	public boolean processLine(String line) {
		boolean cont = true;
		switch (line) {
		case "q":
		case "quit":
			cont = false;
			break;
		case "n":
		case "new":
		case "newGame":
			controller.newGame();
			break;
		default:
			processCordLine(line);
			break;
		}
		return cont;
	}

	private void processCordLine(String line) {
		try {
			String[] args = line.split(" ");
			if (line.matches("(o|open) \\d+ \\d+")) {
				controller.openCell(parseInt(args[1]), parseInt(args[2]));
			} else if (line.matches("(a|around) \\d+ \\d+")) {
				controller.openAround(parseInt(args[1]), parseInt(args[2]));
			} else if (line.matches("(f|flag) \\d+ \\d+")) {
				controller.toggleFlag(parseInt(args[1]), parseInt(args[2]));
			} else if (line.matches("(s|set) \\d+ \\d+ \\d+")) {
				controller.changeSettings(parseInt(args[1]), parseInt(args[2]), parseInt(args[3]));
			} else {
				illegalCommand("Illegal Command");
			}
		} catch (IllegalArgumentException e) {
			handleError(e);
		}
	}

	private void handleError(IllegalArgumentException e) {
		String message = e.getMessage();
		if ("Cell does not exist at this location".equals(message)) {
			// Illegal location
			illegalCommand(message);
		} else if ("Cant construct a grid with more mines than cells".equals(message)
				|| "Dimensions must be bigger than 0".equals(message)) {
			// Illegal set
			illegalCommand(message);
		} else {
			// Unknown exception
			throw e;
		}
	}

	@Override
	public void update(Event e) {
		printTUI();
	}

	private void printTUI() {
		LOGGER.info(NEWLINE + controller.getGameStats() + NEWLINE + controller.getGridString() + NEWLINE
				+ controller.getStatusLine() + NEWLINE + COMMANDS);
	}

	private void illegalCommand(String message) {
		LOGGER.info(NEWLINE + message + NEWLINE + COMMANDS);
	}
}
