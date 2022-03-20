package up.wargroove.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ExportTexturesPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getTasks().create("ExportTextures", ExportTextures.class);
    }
}
