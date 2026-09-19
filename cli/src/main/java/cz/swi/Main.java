package cz.swi;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class Main {
    static void main() {

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();

        try (Terminal terminal = defaultTerminalFactory.createTerminal()) {
            terminal.enterPrivateMode();
            terminal.clearScreen();
            terminal.setCursorPosition(new TerminalPosition(terminal.getTerminalSize().getColumns() / 2 - 3, terminal.getTerminalSize().getRows() / 2));

            terminal.putString("Hello");
            terminal.flush();

            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
