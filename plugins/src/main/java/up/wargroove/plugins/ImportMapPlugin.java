package up.wargroove.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ImportMapPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getTasks().create("importMap", ImportMap.class);
    }
}
