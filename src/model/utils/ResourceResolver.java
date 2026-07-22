package model.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

/** Opens game data both from an IDE checkout and from a packaged Gradle build. */
public final class ResourceResolver {
    private ResourceResolver() {
    }

    public static InputStream open(String requestedPath) throws IOException {
        if (requestedPath == null || requestedPath.isBlank()) {
            throw new IllegalArgumentException("resource path cannot be blank");
        }

        String normalized = requestedPath.replace('\\', '/');
        String fileName = normalized.substring(normalized.lastIndexOf('/') + 1);

        Set<String> classpathCandidates = new LinkedHashSet<>();
        classpathCandidates.add(normalized);
        classpathCandidates.add(stripLeadingSlash(normalized));
        classpathCandidates.add(fileName);
        classpathCandidates.add("resource/" + fileName);

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        for (String candidate : classpathCandidates) {
            InputStream stream = loader.getResourceAsStream(stripLeadingSlash(candidate));
            if (stream != null) return stream;
        }

        Set<String> fileCandidates = new LinkedHashSet<>();
        fileCandidates.add(normalized);
        fileCandidates.add(stripLeadingSlash(normalized));
        fileCandidates.add(fileName);
        fileCandidates.add("resource/" + fileName);
        fileCandidates.add("src/resource/" + fileName);

        for (String candidate : fileCandidates) {
            File file = new File(candidate);
            if (file.isFile()) return new FileInputStream(file);
        }
        return null;
    }

    private static String stripLeadingSlash(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }
}
