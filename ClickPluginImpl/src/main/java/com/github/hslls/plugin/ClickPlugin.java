package com.github.hslls.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ClickPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("mapleleaf_clicktip", ClickTip.class);
        AppExtension app = project.getExtensions().getByType(AppExtension.class);
        app.registerTransform(new ClickTransform(project));
    }

}