package up.wargroove.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ExportMapPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getTasks().create("exportMap", ExportMap.class);
    }
}
