package io.github.md5sha256.messaging.bootstrapper;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.language.jvm.tasks.ProcessResources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;

public class BootstrapPlugin implements Plugin<Project> {

    public static File librariesListFile(Project project) {
        return new File(project.getBuildDir(), "libraries.list");
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (final byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void apply(final Project project) {
        project.getTasks().getByName("processResources")
                .doFirst(new Action<Task>() {
                    @Override
                    public void execute(final Task task) {
                        try {
                            final File file = generateLibrariesList(task);
                            ((ProcessResources) task).from(file);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getCause());
                        }
                    }
                });
    }

    private File generateLibrariesList(Task task) throws IOException, NoSuchAlgorithmException {
        Project project = task.getProject();
        final Collection<String> fileList = new ArrayList<>();
        final Configuration configuration = project.getConfigurations().getByName("runtimeClasspath");
        for (File file : configuration.getFiles()) {
            if (!file.exists()) {
                continue;
            }
            final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            final String name = file.getName();
            final byte[] hashed;
            try (InputStream fis = new FileInputStream(file);) {
                fis.transferTo(new DigestOutputStream(OutputStream.nullOutputStream(), digest));
                hashed = digest.digest();
            }
            final String msg = name + " " + bytesToHex(hashed) + System.lineSeparator();
            fileList.add(msg);
        }
        final File data = librariesListFile(project);
        if (!data.exists()) {
            data.createNewFile();
        }
        try (FileWriter writer = new FileWriter(data)) {
            for (String line : fileList) {
                writer.write(line);
            }
        }
        return data;
    }

}
