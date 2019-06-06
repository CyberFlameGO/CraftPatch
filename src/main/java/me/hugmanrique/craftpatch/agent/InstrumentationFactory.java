package me.hugmanrique.craftpatch.agent;

import com.sun.tools.attach.VirtualMachine;
import me.hugmanrique.craftpatch.util.ClassUtil;
import me.hugmanrique.craftpatch.util.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static java.util.jar.Attributes.Name.MANIFEST_VERSION;

/**
 * Factory for obtaining an {@link Instrumentation} instance required
 * for runtime class redefinition.
 *
 * @author Hugo Manrique
 * @since 05/09/2018
 */
public class InstrumentationFactory {
    private static Instrumentation instance;

    /**
     * This method is not synchronized because when the agent is loaded from
     * {@link #getInstrumentation()} that method will cause {@link #agentmain(String, Instrumentation)}
     * to be called.
     *
     * Synchronizing this method would cause a deadlock.
     */
    public static void setInstrumentation(Instrumentation instrumentation) {
        instance = instrumentation;
    }

    public static synchronized Instrumentation getInstrumentation() throws Exception {
        if (instance != null) {
            return instance;
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

        // If the agent was loaded successfully, this variable
        // will no longer be null.
        return instance;
    }

    /**
     * This method gets called when a JAR is added as an agent at runtime.
     * All this method does is store the {@link Instrumentation} instance
     * for later use.
     */
    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("Initialized");
        InstrumentationFactory.setInstrumentation(instrumentation);
        System.out.println("Set instrumentation to " + instrumentation);
    }

    private static void loadToolsJar() throws Exception {
        URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();

        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);

        File toolsJar = new File(new File(System.getProperty("java.home")).getParent(), "lib/tools.jar");

        if (!toolsJar.exists()) {
            throw new RuntimeException("Couldn't find tools.jar, make sure you run this with JDK");
        }

        method.invoke(loader, toolsJar.toURI().toURL());
    }

    /**
     * An agent is a .jar file where the manifest has an Agent-Class attribute. Additionally, in order
     * to be able to redefine classes, the Can-Redefine-Classes attribute must be true.
     *
     * This class creates such an agent Jar as a temporary file. The Agent-Class is this same class.
     * If the returned Jar is loaded as an agent, then {@link #agentmain(String, Instrumentation)}
     * will get called by the JVM.
     *
     * @return a temporary {@link File} that points at at Jar that packages this class.
     * @throws IOException if the agent Jar file creation failed
     */
    private static File createAgentJarFile() throws IOException {
        final Class<?> agentClass = InstrumentationFactory.class;

        final File jarFile = File.createTempFile("craftPatch", ".jar");
        jarFile.deleteOnExit();

        Manifest manifest = createManifest(agentClass);

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

    private static Manifest createManifest(Class<?> agentClass) {
        Manifest manifest = new Manifest();
        Attributes mainAttributes = manifest.getMainAttributes();

        mainAttributes.put(MANIFEST_VERSION, "1.0");
        mainAttributes.put(new Attributes.Name("Agent-Class"), agentClass.getName());
        mainAttributes.put(new Attributes.Name("Can-Retransform-Classes"), "true");
        mainAttributes.put(new Attributes.Name("Can-Redefine-Classes"), "true");

        return manifest;
    }
}
