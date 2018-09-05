package me.hugmanrique.craftpatch.agent;

import me.hugmanrique.craftpatch.Patch;
import me.hugmanrique.craftpatch.PatchApplier;

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

/**
 * Agent in charge of redefining a class in runtime using {@link Instrumentation} as provided
 * by Java 1.6 or later. Class redefinition is the act of replacing a class' bytecode at runtime,
 * after that class has already been loaded.
 *
 * You only need to call {@link PatchApplierAgentLoader#applyPatches(PatchApplier, Patch...)}. The agent stuff will be done
 * automatically (and lazily).
 *
 * Note that patches must only contain modification transformations. The transformed class must
 * retain the same schema i.e. methods and fields cannot be added or removed.
 *
 * @see PatchApplierAgentLoader#applyPatches(PatchApplier, Patch...)
 * @author Hugo Manrique
 * @since 05/08/2018
 */
public class PatchApplierAgent {
    private static final Logger LOGGER = Logger.getLogger(PatchApplierAgent.class.getSimpleName());

    static volatile Instrumentation instrumentation;

    public static void agentmain(String agentArgs, Instrumentation inst) {
        if (!inst.isRedefineClassesSupported()) {
            LOGGER.severe("Aborting patch apply, class redefinition is not supported");
            return;
        }

        instrumentation = inst;
    }
}
