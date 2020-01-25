package com.github.hslls.plugin;

import com.android.build.api.transform.QualifiedContent;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.github.hlls.transform.EasyTransform;
import com.google.common.collect.ImmutableSet;

import org.gradle.api.Project;

import java.io.File;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class ClickTransform extends EasyTransform {

    public ClickTransform(Project project) {
        super(project);
    }

    @Override
    public String getName() {
        return "ClickTransform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT;
        return ImmutableSet.of(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.SUB_PROJECTS);
    }

    @Override
    protected boolean isJarFileNeedModify(File jarFile) {
        return true;
    }

    @Override
    protected boolean justModifyNotWriteBack(CtClass ctClass) {
        if (ctClass.isInterface()) {
            return false;
        }

        ClickTip ct = mProject.getExtensions().getByType(ClickTip.class);
        boolean hasModified = false;
        CtMethod[] methods = ctClass.getDeclaredMethods();
        for (CtMethod m : methods) {
            String methodName = m.getName();
            if ("onClick".equals(methodName)) {
                CtClass[] types;
                try {
                    types = m.getParameterTypes();
                } catch (NotFoundException e) {
                    types = null;
                }
                if ((types == null) || (types.length != 1)) {
                    continue;
                }

                if ("android.view.View".equals(types[0].getName())) {
                    try {
                        m.insertBefore(
                                "if ($1 != null) {" +
                                        "android.content.Context ctx = $1.getContext();" +
                                        "if (ctx != null) {" +
                                        "android.widget.Toast.makeText(ctx, \"" +
                                        ct.tip +
                                        "\", android.widget.Toast.LENGTH_SHORT).show();" +
                                        "}}");
                    } catch (CannotCompileException e) {

                    }
                    hasModified = true;
                }
            }
        }

        return hasModified;
    }
}