package moe.karla.maven.publishing;

import moe.karla.maven.publishing.advtask.UploadToMavenCentral;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Zip;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MavenPublishingPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        Project rootProject = target.getRootProject();
        if (target != rootProject) {
            target.getLogger().warn("maven-publish-publish requires be applied on root project.");
            rootProject.apply(it -> it.plugin(MavenPublishingPlugin.class));
            return;
        } else {
            rootProject.apply(it -> it.plugin("java-base"));
        }

        File cacheRepoLocation = rootProject.getLayout().getBuildDirectory().get()
                .dir("maven-publishing-stage")
                .getAsFile();

        MavenPublishingExtension ext = rootProject.getExtensions().create("mavenPublishing", MavenPublishingExtension.class);
        rootProject.afterEvaluate(_p1 -> {
            rootProject.allprojects(project -> {
                project.getPluginManager().withPlugin("maven-publish", publish -> {
                    PublishingExtension publishing = (PublishingExtension) project.getExtensions().findByName("publishing");
                    if (publishing == null) {
                        return;
                    }
                    publishing.repositories(repositories -> {
                        repositories.maven(it -> {
                            it.setName("MavenStage");
                            it.setUrl(cacheRepoLocation.toURI());
                        });
                        String user = System.getenv("MAVEN_SNAPSHOTS_USERNAME");
                        String token = System.getenv("MAVEN_SNAPSHOTS_TOKEN");
                        if (user != null && token != null) {
                            repositories.maven(it -> {
                                it.setName("CentralSnapshots");
                                it.setUrl("https://central.sonatype.com/repository/maven-snapshots/");
                                it.credentials(c -> {
                                    c.setUsername(user);
                                    c.setPassword(token);
                                });
                            });
                        }
                    });
                });
            });
        });


        TaskProvider<Task> cleanTask = rootProject.getTasks().register("cleanMavenPublishingStage", task -> {
            task.doLast(it -> {
                deleteDir(cacheRepoLocation);
            });
        });
        rootProject.getTasks().getByName("clean").dependsOn(cleanTask);

        TaskProvider<Zip> packBundleTask = rootProject.getTasks().register("packMavenPublishingStage", Zip.class, task -> {
            task.getDestinationDirectory().set(rootProject.getLayout().getBuildDirectory().dir("tmp"));
            task.getArchiveFileName().set("bundle.zip");

            task.from(cacheRepoLocation);
        });

        List<String> dependencies = new ArrayList<>();
        dependencies.add("org.apache.httpcomponents:httpclient:4.5.13");
        dependencies.add("org.apache.httpcomponents:httpmime:4.5.13");
        Configuration externalTaskConfiguration = rootProject.getConfigurations().create("mavenPublishingExternalModuleClasspath");
        for (String it : dependencies) {
            externalTaskConfiguration.getDependencies().add(rootProject.getDependencies().create(it));
        }

        rootProject.getTasks().register("publishToMavenCentral", task -> {
            task.setGroup("publishing");
            task.dependsOn(packBundleTask);
            task.getInputs().files(packBundleTask.get().getOutputs().getFiles());
            task.doFirst(it -> {
                try {
                    UploadToMavenCentral.execute(rootProject.getName(), ext.publishingType.name(), packBundleTask.get().getOutputs().getFiles().getSingleFile());
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            });
        });
    }

    public static boolean deleteDir(File self) {
        if (!self.exists()) {
            return true;
        } else if (!self.isDirectory()) {
            return false;
        } else {
            File[] files = self.listFiles();
            if (files == null) {
                return false;
            } else {
                boolean result = true;

                for(File file : files) {
                    if (file.isDirectory()) {
                        if (!deleteDir(file)) {
                            result = false;
                        }
                    } else if (!file.delete()) {
                        result = false;
                    }
                }

                if (!self.delete()) {
                    result = false;
                }

                return result;
            }
        }
    }
}
