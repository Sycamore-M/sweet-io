package cn.sycamore.sweet.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Sycamore
 * created on 2025/1/6
 */
class CloseLockTests {

    private static InputStream getInputStream(Path path) throws IOException {
        try (CloseLock<InputStream> closeLock = CloseLock.lock(Files.newInputStream(path))) {
            // Your code.
            return closeLock.unlock();
        }
    }

    private void copy(InputStream in, OutputStream out) throws IOException {
        final int bufferSize = 8192;
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = in.read(buffer, 0, bufferSize)) >= 0) {
            out.write(buffer, 0, read);
        }
    }

    @Test
    void fileInputStream() throws IOException {
        String userHome = System.getProperty("user.home");
        Path from = Paths.get(userHome, "test.txt");
        Path to = Paths.get(userHome, "test_copy.txt");
        Files.write(from, "test".getBytes(StandardCharsets.UTF_8));
        try (CloseLock<InputStream> inLock = CloseLock.lock(getInputStream(from));
             CloseLock<OutputStream> outLock = CloseLock.lock(Files.newOutputStream(to))) {
            copy(inLock.get(), outLock.get());
        }
        Assertions.assertTrue(Files.deleteIfExists(from));
        Assertions.assertTrue(Files.deleteIfExists(to));
    }

}
