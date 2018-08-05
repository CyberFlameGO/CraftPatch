package me.hugmanrique.craftpatch.agent;

import java.lang.instrument.Instrumentation;

/**
 * Marks a failure to load the agent and get an instance of {@link Instrumentation}
 * that is able to redefine classes.
 *
 * @author Hugo Manrique
 * @since 05/08/2018
 */
public class AgentLoadException extends Exception {
    public AgentLoadException() {
    }

    public AgentLoadException(String message) {
        super(message);
    }
}
