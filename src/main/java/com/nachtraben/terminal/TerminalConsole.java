package com.nachtraben.terminal;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class TerminalConsole {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract boolean isRunning();

    protected abstract void runCommand(String command);

    protected abstract void shutdown();

    protected void processInput(String input) {
        String command = input.trim();
        if(!command.isEmpty()) {
            runCommand(command);
        }
    }

    protected LineReader buildReader(LineReaderBuilder builder) {
        LineReader reader = builder.build();
        reader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);
        reader.unsetOpt(LineReader.Option.INSERT_TAB);
        return reader;
    }

    public void start() {
        if(!Thread.currentThread().isDaemon()) {
            logger.warn("Console thread must be started in a daemon thread to allow the JVM to shutdown gracefully", new IllegalStateException("Console thread is not marked as daemon"));
        }
        try {
            final Terminal terminal = ConsoleAppender.getTerminal();
            if(terminal != null) {
                readCommands(terminal);
            } else {
                readCommands(System.in);
            }
        } catch (Exception e) {
            logger.warn("Failed to read console input", e);
        }
    }

    private void readCommands(Terminal terminal) {
        LineReader reader = buildReader(LineReaderBuilder.builder().terminal(terminal));
        ConsoleAppender.setReader(reader);
        try {
            String line;
            while(isRunning()) {
                try {
                    line = reader.readLine("> ");
                } catch (EndOfFileException ignored) {
                    continue;
                }
                if(line == null) {
                    break;
                }
                processInput(line);
            }
        } catch (UserInterruptException e) {
            shutdown();
        } finally {
            ConsoleAppender.setReader(null);
        }
    }

    private void readCommands(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while (isRunning() && (line = reader.readLine()) != null) {
                processInput(line);
            }
        }
    }
}
