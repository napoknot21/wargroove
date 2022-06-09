package up.wargroove.plugins.tasks;

/**
 * This is the base of every plugin.
 */
public interface Plugin {

    /**
     * Inits the tasks parameters according to the given arguments.
     *
     * @param parameter the CLI arguments.
     */
    void initParameter(String... parameter);

    /**
     * Run the primary task.
     *
     * @throws Exception if an error occurred.
     */
    void run() throws Exception;
}
