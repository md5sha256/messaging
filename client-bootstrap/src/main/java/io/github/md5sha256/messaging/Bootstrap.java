package io.github.md5sha256.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public final class Bootstrap {

    public static void main(String[] argv) {
        new Bootstrap().run(argv);
    }

    private static boolean checkIntegrity(Path file, String expectedHash) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        boolean valid;
        try (InputStream input = Files.newInputStream(file)) {
            input.transferTo(new DigestOutputStream(OutputStream.nullOutputStream(), digest));
            String actualHash = byteToHex(digest.digest());
            valid = actualHash.equalsIgnoreCase(expectedHash);
            if (!valid) {
                System.out.printf("Expected file %s to have hash %s, but got %s", file, expectedHash, actualHash);
            }
        }
        return valid;
    }

    private static String byteToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            result.append(Character.forDigit(b >> 4 & 15, 16));
            result.append(Character.forDigit(b & 15, 16));
        }
        return result.toString();
    }

    private InputStream getResourceAsStream(String name) {
        return Bootstrap.class.getResourceAsStream("/" + name);
    }

    private void run(String[] argv) {
        try (InputStream props = getResourceAsStream("bootstrap.properties")) {
            final Properties bootstrapProperties = new Properties();
            bootstrapProperties.load(props);
            final Path workingDirectory = Path.of(System.getProperty("user.dir"));
            final Path outputDir = workingDirectory.resolve(bootstrapProperties.getProperty("output-dir", "libraries/"));
            final String mainClassName = bootstrapProperties.getProperty("main-class").replace("'", "");
            if (mainClassName == null || mainClassName.isEmpty()) {
                System.out.println("Empty main class specified, exiting");
                System.exit(0);
            }
            Files.createDirectories(outputDir);
            List<URL> extractedUrls = readAndExtractJars(outputDir);
            ClassLoader maybePlatformClassLoader = this.getClass().getClassLoader().getParent();
            URLClassLoader classLoader = new URLClassLoader(extractedUrls.toArray(new URL[0]), maybePlatformClassLoader);
            System.out.println("Starting " + mainClassName);
            Thread runThread = new Thread(() -> {
                try {
                    Class<?> mainClass = Class.forName(mainClassName.replaceAll("\"", ""), true, classLoader);
                    MethodHandle mainHandle = MethodHandles
                            .lookup()
                            .findStatic(mainClass, "main", MethodType.methodType(Void.TYPE, String[].class))
                            .asFixedArity();
                    mainHandle.invoke(argv);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }

            }, "TraderMain");
            runThread.setContextClassLoader(classLoader);
            runThread.start();
        } catch (Exception exception) {
            exception.printStackTrace(System.out);
            System.out.println("Failed to extract libraries, exiting");
        }

    }

    private Map<String, String> resolveLibraryList() throws IOException {
        final Map<String, String> ret = new HashMap<>();
        try (InputStream is = getResourceAsStream("libraries.list");
             Scanner scanner = new Scanner(is)) {
            while (scanner.hasNext()) {
                final String[] split = scanner.nextLine().split(" ");
                final String name = split[0];
                final String hash = split[1];
                ret.put(name, hash);
            }
        }
        return ret;
    }

    private URL extractJar(String file, Path outputDir) throws IOException, IllegalStateException {
        final InputStream resource = getResourceAsStream(file);
        if (resource == null) {
            throw new IllegalStateException("Missing resource: " + file);
        }
        final Path outputPath = outputDir.resolve(file);
        Files.copy(resource, outputPath, StandardCopyOption.REPLACE_EXISTING);
        return outputPath.toUri().toURL();
    }

    private URL checkAndExtractJar(Map.Entry<String, String> entry, Path outputDir) throws IOException, NoSuchAlgorithmException {
        final String file = entry.getKey();
        final String hash = entry.getValue();
        final Path outputFile = outputDir.resolve(file);
        if (!Files.exists(outputFile) || !checkIntegrity(outputFile, hash)) {
            System.out.printf("Unpacking %s", file);
        }
        final URL url = extractJar(file, outputDir);
        checkIntegrity(outputFile, hash);
        return url;
    }

    private List<URL> readAndExtractJars(Path outputDir) throws IOException, NoSuchAlgorithmException {
        final Map<String, String> libraryList = resolveLibraryList();
        final List<URL> urls = new ArrayList<>(libraryList.size());
        for (Map.Entry<String, String> entry : libraryList.entrySet()) {
            urls.add(checkAndExtractJar(entry, outputDir));
        }
        return urls;
    }

}
