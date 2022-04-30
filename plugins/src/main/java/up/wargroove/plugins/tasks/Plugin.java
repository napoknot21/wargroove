package up.wargroove.plugins.tasks;

/**
 * This is the base of every plugin.
 */
public abstract class Plugin {

    public Plugin(String... args) {
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] parameter = arg.substring(2).split("=");
                initParameter(parameter);
            }
        }
    }

    /**
     * Inits the tasks parameters according to the given arguments.
     *
     * @param parameter the CLI arguments.
     */
    abstract void initParameter(String... parameter);

    /**
     * Run the primary task.
     *
     * @throws Exception if an error occurred.
     */
    public abstract void run() throws Exception;
}
