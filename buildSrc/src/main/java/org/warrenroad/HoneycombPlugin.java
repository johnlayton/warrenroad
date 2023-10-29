package org.warrenroad;

import org.apache.tools.ant.filters.ReplaceTokens;
import org.gradle.api.*;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;


public class HoneycombPlugin implements Plugin<Project> {

    interface HoneycombExtension {
        RegularFileProperty getConfig();

        Property<String> getAppName();
    }

    @Override
    public void apply(final Project project) {

        final HoneycombExtension HoneycombExtension =
                project.getExtensions().create("honeycomb", HoneycombExtension.class);

        HoneycombExtension.getAppName().convention("WarrenRoad - Default");

        final Configuration HoneycombConfiguration =
                project.getConfigurations().create("Honeycomb", configuration -> {
                    project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs")
                            .findLibrary("io.honeycomb.opentelemetry.javaagent")
                            .ifPresentOrElse(provider -> configuration.getDependencies().add(
                                    project.getDependencies().create(provider.get().toString())
                            ), () -> {
                                throw new GradleException(
                                        "You need to add an entry to the libs.versions.toml (or libs version catalog) for the honeycomb java agent. Example; io-honeycomb-opentelemetry-javaagent = { group = 'io.honeycomb', name = 'honeycomb-opentelemetry-javaagent', version = '1.5.2' }");
                            });
                });

        final TaskProvider<Copy> copyHoneycombArtifacts =
                project.getTasks().register("copyHoneycombArtifacts", Copy.class, config -> {
                    config.doFirst(getCopyHoneycombAgent(project));
                    config.into(project.getLayout().getBuildDirectory().dir("honeycomb"));
                    config.from(HoneycombConfiguration).rename(s -> "honeycomb.jar");
                });

        final TaskProvider<Copy> copyHoneycombConfiguration =
                project.getTasks().register("copyHoneycombConfiguration", Copy.class, config -> {
                    config.doFirst(doFirstCopyHoneycombConfiguration(project, HoneycombExtension, config));
                    config.into(project.getLayout().getBuildDirectory().dir("honeycomb"));
                    config.from(HoneycombExtension.getConfig().getAsFile().orElse(defaultConfigurationProvider()))
                            .rename(s -> "honeycomb.yml");
                });


        project.getTasks().stream().filter(t -> t.getName().equals("assemble"))
                .findFirst().ifPresent(t -> {
                    t.dependsOn(copyHoneycombArtifacts);
                    t.dependsOn(copyHoneycombConfiguration);
                });
    }

    @NotNull
    private static Action<Task> doFirstCopyHoneycombConfiguration(final Project project,
                                                                 final HoneycombExtension HoneycombExtension,
                                                                 final Copy config) {
        return new Action<Task>() {
            @Override
            public void execute(final Task task) {
                project.getLogger().info("Copy Honeycomb Configuration");
                config.filter(Map.of(
                        "tokens", Map.of(
                                "license_key",
                                System.getenv().getOrDefault("HONEYCOMB_LICENSE_KEY", "MISSING"),
                                "app_name", HoneycombExtension.getAppName().get())
                ), ReplaceTokens.class);
            }
        };
    }

    private static Action<Task> getCopyHoneycombAgent(final Project project) {
        return new Action<Task>() {
            @Override
            public void execute(final Task task) {
                project.getLogger().info("Copy Honeycomb Agent");
            }
        };
    }

    private File defaultConfigurationProvider() {
        try (InputStream is = HoneycombPlugin.class.getClassLoader().getResourceAsStream("Honeycomb.yml")) {
            File file = File.createTempFile("Honeycomb-", ".yml");
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (final IOException e) {
            throw new GradleException("Problem creating the default honeycomb configuration", e);
        }
    }
}
