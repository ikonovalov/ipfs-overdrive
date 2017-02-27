package ru.codeunited.ipfs.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Objects.nonNull;

/**
 * OSS codeunited.ru
 * Created by ikonovalov on 28/02/17.
 */
public abstract class Command {

    private Logger log = LoggerFactory.getLogger(Command.class);

    private final String name;

    private final List<String> requiredArguments;

    private Command nextDelegate;

    protected Command(String name, List<String> requiredArguments) {
        this.name = name;
        this.requiredArguments = Collections.unmodifiableList(requiredArguments);
    }

    protected Command(String name) {
        this(name, EMPTY_LIST);
    }

    public Command nextDelegateTo(Command next) {
        this.nextDelegate = next;
        return next;
    }

    public abstract void run(List<String> arguments);

    public final void perform(String[] arguments) {
        if (arguments.length > 0 && Objects.equals(name, arguments[0])) {
            List<String> cmdArgs = removeCurrentArguments(arguments);
            log.info("Incoming arguments: ", arguments);
            run(cmdArgs);
        } else {
            if (nonNull(nextDelegate)) {
                nextDelegate.perform(arguments);
            } else {
                log.error("Incorrect '{}' command with args {}", name, Arrays.asList(arguments).stream().reduce((s1, s2) -> s1 + ", " + s2).orElseGet(() -> "[EMPTY ARGS]"));
            }
        }
    }

    private List<String> removeCurrentArguments(String[] arguments) {
        List<String> cmdArgs = new ArrayList<>(Arrays.asList(arguments));
        cmdArgs.removeAll(requiredArguments);
        return cmdArgs;
    }
}
