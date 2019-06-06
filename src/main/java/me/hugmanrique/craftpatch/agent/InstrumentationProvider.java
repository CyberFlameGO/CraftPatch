package me.hugmanrique.craftpatch.agent;

import java.lang.instrument.Instrumentation;

/**
 * @author Hugo Manrique
 * @since 06/09/2018
 */
public interface InstrumentationProvider {
    Instrumentation getInstrumentation();
}
