package up.wargroove.plugins;

import org.gradle.api.plugins.UnknownPluginException;
import up.wargroove.utils.Log;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * CLI interpreter for the app plugins.
 */
public class PluginSelector {
    private static Map<String, Class<?>> plugins;

    static {
        try {
            plugins = initPlugins();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inits the plugins list. Its searched all the class in the tasks' repository.
     *
     * @return the plugins list.
     * @throws ClassNotFoundException if a class doesn't exist.
     */
    private static Map<String, Class<?>> initPlugins() throws ClassNotFoundException {
        String packageName = PluginSelector.class.getPackageName() + ".tasks";
        String path = "plugins/src/main/java/up/wargroove/plugins/tasks";
        File dir = new File(path);
        if (!dir.isDirectory() || dir.listFiles() == null) {
            return null;
        }
        plugins = new HashMap<>();
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            String name = f.getName();
            name = name.substring(0, name.length() - 5);
            plugins.put(name, Class.forName(packageName + '.' + name));
        }
        return plugins;
    }

    /**
     * Run the primary task.
     *
     * @throws Exception if an error occurred.
     */
    public static void run() throws Throwable {
        Log.print("Please enter the command : ");
        Scanner scanner = new Scanner(System.in);
        String[] args = scanner.nextLine().split(" ");
        scanner.close();
        checkCommand(args[0], args);
    }

    /**
     * Check if the command is valid. if true, run the selected plugin.
     *
     * @param arg  The plugin name.
     * @param args The plugin's arguments.
     * @throws Exception if the plugin is unknown.
     */
    private static void checkCommand(String arg, String[] args) throws Throwable {
        if (plugins == null) {
            throw new Exception("There is no plugins");
        }
        for (String name : plugins.keySet()) {
            if (name.equalsIgnoreCase(arg)) {
                Object obj = plugins.get(name).getConstructor(String[].class).newInstance((Object) args);
                Method method = plugins.get(name).getDeclaredMethod("run");
                try {
                    method.invoke(obj);
                } catch (Exception e) {
                    throw e.getCause();
                }
                return;
            }
        }
        throw new UnknownPluginException("Plugin (" + arg + ") is unknown");
    }
}
