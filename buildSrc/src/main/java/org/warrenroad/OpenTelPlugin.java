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


public class OpenTelPlugin implements Plugin<Project> {

    public interface OpenTelExtension {
        RegularFileProperty getConfig();
        Property<String> getAppName();
    }

    @Override
    public void apply(final Project project) {

        final OpenTelExtension OpenTelExtension =
                project.getExtensions().create("openTel", OpenTelExtension.class);

        OpenTelExtension.getAppName().convention("WarrenRoad - Default");

        final Configuration OpenTelConfiguration =
                project.getConfigurations().create("OpenTel", configuration -> {
                    project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs")
                            .findLibrary("opentelemetry.javaagent")
                            .ifPresentOrElse(provider -> configuration.getDependencies().add(
                                    project.getDependencies().create(provider.get().toString())
                            ), () -> {
                                throw new GradleException(
                                        "You need to add an entry to the libs.versions.toml (or libs version catalog) for the openTel java agent. Example; opentelemetry-javaagent = { group = 'io.opentelemetry.javaagent', name = 'opentelemetry-javaagent', version = '1.31.0' }");
                            });
                });

        final TaskProvider<Copy> copyOpenTelArtifacts =
                project.getTasks().register("copyOpenTelArtifacts", Copy.class, config -> {
                    config.doFirst(getCopyOpenTelAgent(project));
                    config.into(project.getLayout().getBuildDirectory().dir("opentel"));
                    config.from(OpenTelConfiguration).rename(s -> "opentel.jar");
                });

        final TaskProvider<Copy> copyOpenTelConfiguration =
                project.getTasks().register("copyOpenTelConfiguration", Copy.class, config -> {
                    config.doFirst(doFirstCopyOpenTelConfiguration(project, OpenTelExtension, config));
                    config.into(project.getLayout().getBuildDirectory().dir("opentel"));
                    config.from(OpenTelExtension.getConfig().getAsFile().orElse(defaultConfigurationProvider()))
                            .rename(s -> "opentel.yml");
                });


        project.getTasks().stream().filter(t -> t.getName().equals("assemble"))
                .findFirst().ifPresent(t -> {
                    t.dependsOn(copyOpenTelArtifacts);
                    t.dependsOn(copyOpenTelConfiguration);
                });

    }

    @NotNull
    private static Action<Task> doFirstCopyOpenTelConfiguration(final Project project,
                                                                 final OpenTelExtension OpenTelExtension,
                                                                 final Copy config) {
        return new Action<Task>() {
            @Override
            public void execute(final Task task) {
                project.getLogger().info("Copy OpenTel Configuration");
                config.filter(Map.of(
                        "tokens", Map.of(
                                "license_key",
                                System.getenv().getOrDefault("OPENTEL_LICENSE_KEY", "MISSING"),
                                "app_name", OpenTelExtension.getAppName().get())
                ), ReplaceTokens.class);
            }
        };
    }

    private static Action<Task> getCopyOpenTelAgent(final Project project) {
        return new Action<Task>() {
            @Override
            public void execute(final Task task) {
                project.getLogger().info("Copy OpenTel Agent");
            }
        };
    }

    private File defaultConfigurationProvider() {
        try (InputStream is = OpenTelPlugin.class.getClassLoader().getResourceAsStream("opentel.yml")) {
            File file = File.createTempFile("openTel-", ".yml");
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (final IOException e) {
            throw new GradleException("Problem creating the default open telemetry configuration", e);
        }
    }
}
