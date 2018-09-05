package me.hugmanrique.craftpatch.agent;

import com.sun.tools.attach.VirtualMachine;
import me.hugmanrique.craftpatch.Patch;
import me.hugmanrique.craftpatch.PatchApplier;
import me.hugmanrique.craftpatch.PatchApplyException;
import me.hugmanrique.craftpatch.util.ClassUtil;
import me.hugmanrique.craftpatch.util.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;
import static me.hugmanrique.craftpatch.agent.PatchApplierAgent.instrumentation;

/**
 * @author Hugo Manrique
 * @since 05/09/2018
 */
public class PatchApplierAgentLoader {
    private static final int LOAD_ATTEMPTS = 20;
    private static final long ATTEMPT_WAIT_TIME = Duration.ofMillis(500).toMillis();

    /**
     * Attempts to apply a patch to redefine the target class bytecode.
     *
     * On first call, this method will attempt to load an agent into the JVM to obtain
     * an instance of {@link Instrumentation}. This agent load can introduce a pause
     * (in practice 1 to 2 seconds).
     *
     * @param applier The patcher that will compile the patch
     * @param patches Patches that need to be applied
     * @return an array of all the transformed classes
     * @throws PatchApplyException if the agent either failed to load or if the agent wasn't able to get
     *                             an instance of {@link Instrumentation} that allows class redefinitions.
     */
    public static Class[] applyPatches(PatchApplier applier, Patch... patches) throws PatchApplyException {
        try {
            ensureAgentLoaded();
        } catch (AgentLoadException e) {
            throw new PatchApplyException(e);
        }

        ClassDefinition[] definitions = Arrays.stream(patches)
                .map(patch -> {
                    try {
                        return applier.getDefinition(patch);
                    } catch (PatchApplyException e) {
                        e.printStackTrace();
                    }

                    return null;
                })
                .filter(Objects::nonNull)
                .toArray(ClassDefinition[]::new);

        try {
            instrumentation.redefineClasses(definitions);
        } catch (ClassNotFoundException | UnmodifiableClassException e) {
            throw new PatchApplyException(e);
        }

        return Arrays.stream(definitions)
                .map(ClassDefinition::getDefinitionClass)
                .toArray(Class[]::new);
    }

    private static void ensureAgentLoaded() throws AgentLoadException {
        if (instrumentation != null) {
            // Already loaded
            return;
        }

        // The JDK doesn't load lib/tools.jar by default
        loadToolsJar();

        try {
            File agentJar = createAgentJarFile();

            // Loading an agent requires the PID of the JVM to load the agent to
            String runningVMName = ManagementFactory.getRuntimeMXBean().getName();
            String pid = runningVMName.substring(0, runningVMName.indexOf('@'));

            // Load the agent
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(agentJar.getAbsolutePath());
            vm.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }

        awaitAgentLoad();
    }

    private static void loadToolsJar() {
        URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();

        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);

            File file = new File(new File(System.getProperty("java.home")).getParent(), "lib/tools.jar");

            if (!file.exists()) {
                throw new RuntimeException("Not running with JDK");
            }

            method.invoke(loader, file.toURI().toURL());
        } catch (NoSuchMethodException | IllegalAccessException | MalformedURLException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void awaitAgentLoad() throws AgentLoadException {
        for (int attempt = 0; attempt < LOAD_ATTEMPTS; attempt++) {
            if (instrumentation != null) {
                // Agent loaded successfully
                return;
            }

            try {
                Thread.sleep(ATTEMPT_WAIT_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AgentLoadException();
            }
        }

        // Agent didn't load in time
        throw new AgentLoadException();
    }

    /**
     * An agent is a .jar file where the manifest has an Agent-Class attribute. Additionally, in order
     * to be able to redefine classes, the Can-Redefine-Classes attribute must be true.
     *
     * This class creates such an agent Jar as a temporary file. The Agent-Class is this same class.
     * If the returned Jar is loaded as an agent, then {@link PatchApplierAgent#agentmain(String, Instrumentation)}
     * will get called by the JVM.
     *
     * @return a temporary {@link File} that points at at Jar that packages this class.
     * @throws IOException if the agent Jar file creation failed
     */
    private static File createAgentJarFile() throws IOException {
        final Class<?> agentClass = PatchApplierAgent.class;
        final File jarFile = File.createTempFile("patcher", ".jar");

        jarFile.deleteOnExit();
        Manifest manifest = createManifest();

        try (JarOutputStream out = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
            String resourceName = ClassUtil.getClassResourceName(agentClass);
            JarEntry agent = new JarEntry(resourceName);

            // Dump this class bytecode into the entry
            out.putNextEntry(agent);
            out.write(StreamUtil.getResource(agentClass));
            out.closeEntry();
        }

        return jarFile;
    }

    private static Manifest createManifest() {
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();

        mainAttributes.put(MANIFEST_VERSION, "1.0");
        mainAttributes.put(new Attributes.Name("Agent-Class"), PatchApplierAgent.class.getName());
        mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
        mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");

        return manifest;
    }
}
