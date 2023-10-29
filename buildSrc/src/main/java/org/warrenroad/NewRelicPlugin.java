package org.warrenroad;

import org.apache.tools.ant.filters.ReplaceTokens;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
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


public class NewRelicPlugin implements Plugin<Project> {

    interface NewRelicExtension {
        RegularFileProperty getConfig();

        Property<String> getAppName();
    }

    @Override
    public void apply(final Project project) {

        final NewRelicExtension newRelicExtension =
                project.getExtensions().create("newRelic", NewRelicExtension.class);

        newRelicExtension.getAppName().convention("Griddle - Default");

        final Configuration newRelicConfiguration =
                project.getConfigurations().create("newRelic", configuration -> {
                    project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs")
                            .findLibrary("com.newrelic.agent.java")
                            .ifPresentOrElse(provider -> configuration.getDependencies().add(
                                    project.getDependencies().create(provider.get().toString())
                            ), () -> {
                                throw new GradleException(
                                        "You need to add an entry to the libs.versions.toml (or libs version catalog) for the new relic java agent. Example; com-newrelic-agent-java = { group = 'com.newrelic.agent.java', name = 'newrelic-agent', version = '8.0.0' }");
                            });
                });

        final TaskProvider<Copy> copyNewRelicArtifacts =
                project.getTasks().register("copyNewRelicArtifacts", Copy.class, config -> {
                    config.doFirst(getCopyNewRelicAgent(project));
                    config.into(project.getLayout().getBuildDirectory().dir("new-relic"));
                    config.from(newRelicConfiguration).rename(s -> "newrelic.jar");
                });

        final TaskProvider<Copy> copyNewRelicConfiguration =
                project.getTasks().register("copyNewRelicConfiguration", Copy.class, config -> {
                    config.doFirst(doFirstCopyNewRelicConfiguration(project, newRelicExtension, config));
                    config.into(project.getLayout().getBuildDirectory().dir("new-relic"));
                    config.from(newRelicExtension.getConfig().getAsFile().orElse(defaultConfigurationProvider()))
                            .rename(s -> "newrelic.yml");
                });


        project.getTasks().stream().filter(t -> t.getName().equals("assemble"))
                .findFirst().ifPresent(t -> {
                    t.dependsOn(copyNewRelicArtifacts);
                    t.dependsOn(copyNewRelicConfiguration);
                });
    }

    @NotNull
    private static Action<Task> doFirstCopyNewRelicConfiguration(final Project project,
                                                                 final NewRelicExtension newRelicExtension,
                                                                 final Copy config) {
        return new Action<Task>() {
            @Override
            public void execute(final Task task) {
                project.getLogger().info("Copy New Relic Configuration");
                config.filter(Map.of(
                        "tokens", Map.of(
                                "license_key",
                                System.getenv().getOrDefault("NEW_RELIC_LICENSE_KEY", "MISSING"),
                                "app_name", newRelicExtension.getAppName().get())
                ), ReplaceTokens.class);
            }
        };
    }

    private static Action<Task> getCopyNewRelicAgent(final Project project) {
        return new Action<Task>() {
            @Override
            public void execute(final Task task) {
                project.getLogger().info("Copy New Relic Agent");
            }
        };
    }

    private File defaultConfigurationProvider() {
        try (InputStream is = NewRelicPlugin.class.getClassLoader().getResourceAsStream("newrelic.yml")) {
            File file = File.createTempFile("newrelic-", ".yml");
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (final IOException e) {
            throw new GradleException("Problem creating the default new relic configuration", e);
        }
    }
}
