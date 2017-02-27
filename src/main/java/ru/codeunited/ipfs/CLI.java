package ru.codeunited.ipfs;

import ru.codeunited.ipfs.cli.AddCommand;
import ru.codeunited.ipfs.cli.Command;
import ru.codeunited.ipfs.cli.RootCommand;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 28/02/17.
 */
public class CLI {

    public static void main(String[] args) {
        application().run(args);
    }

    private final Command root;

    private CLI() {
        root = new RootCommand();
        root.nextDelegateTo(new AddCommand());
    }

    private static CLI application() {
        return new CLI();
    }

    private void run(String[] args) {
        root.perform(args);
    }
}
