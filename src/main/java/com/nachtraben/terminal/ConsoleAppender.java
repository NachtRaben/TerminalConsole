package com.nachtraben.terminal;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;

public class ConsoleAppender extends OutputStreamAppender<ILoggingEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleAppender.class);

    static final String PROPERTY_PREFIX = "terminal";
    public static final String JLINE_OVERRIDE_PROPERTY = PROPERTY_PREFIX + ".jline";
    public static final String ANSI_OVERRIDE_PROPERTY = PROPERTY_PREFIX + ".ansi";

    private static final Boolean ANSI_OVERRIDE = Boolean.getBoolean(ANSI_OVERRIDE_PROPERTY);

    private static final PrintStream stdout = System.out;

    private static ConsoleAppender instance;
    private static boolean initialized;
    private static Terminal terminal;
    private static LineReader reader;

    @Override
    public void start() {
        instance = this;
        initializeTerminal();
        if (terminal != null) {
            setOutputStream(terminal.output());
        } else {
            setOutputStream(stdout);
        }
        super.start();
    }

    public static synchronized Terminal getTerminal() {
        return terminal;
    }

    public static synchronized void setReader(LineReader reader) {
        if (reader != null && reader.getTerminal() != terminal) {
            throw new IllegalArgumentException("Reader was not created with TerminalConsole.getTerminal()");
        }
        ConsoleAppender.reader = reader;
    }

    public static synchronized LineReader getReader() {
        return reader;
    }

    public static boolean isAnsiSupported() {
        return ANSI_OVERRIDE;
    }

    private synchronized static void initializeTerminal() {
        if (!initialized) {
            initialized = true;
            boolean dumb = Boolean.getBoolean(JLINE_OVERRIDE_PROPERTY) || System.getProperty("java.class.path").contains("idea_rt.jar");

            if (!ANSI_OVERRIDE) {
                try {
                    terminal = TerminalBuilder.builder().dumb(dumb).build();
                } catch (IllegalStateException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.warn("Disabling terminal, you're running in an unsupported environment", e);
                    } else {
                        LOG.warn("Disabling terminal, you're running in an unsupported environment");
                    }
                } catch (IOException e) {
                    LOG.error("Failed to initialize a terminal. Falling back to stdout.");
                }
            }
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (terminal != null) {
            if (reader != null) {
                boolean reading = reader.isReading();
                if (reading) {
                    reader.callWidget("clear");
                    super.append(event);
                    reader.callWidget("redraw_line");
                    reader.callWidget("redisplay");
                }
            } else {
                super.append(event);
                terminal.flush();
            }
        } else {
            super.append(event);
        }
    }

    public static synchronized void close() throws IOException {
        if (initialized) {
            initialized = false;
            reader = null;
            if (terminal != null) {
                try {
                    terminal.close();
                } finally {
                    terminal = null;
                }
            }
        }
    }

    @Override
    public void stop() {
        if (initialized) {
            if (terminal != null) {
                try {
                    reader = null;
                    terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.stop();
    }
}
