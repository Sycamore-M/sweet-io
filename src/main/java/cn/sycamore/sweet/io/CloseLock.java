package cn.sycamore.sweet.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * A better and safer way to autoclose resources.
 * <p>Return a resource like this:
 * <pre>{@code
 * public InputStream getInputStream() throws IOException {
 *     InputStream input = download();
 *     // Lock the resource within the current block.
 *     try (CloseLock<InputStream> closeLock = CloseLock.lock(input)) {
 *         // Write your code, uncaught exceptions are allowed here.
 *         // Unlock and return the resource.
 *         return closeLock.unlock();
 *     }
 *     // The resource will be closed while leaving the block in an unlocked state.
 * }
 * }</pre>
 * <p>Read a resource like this:
 * <pre>{@code
 * public void readInputStream(OutputStream output) throws IOException {
 *     InputStream input = getInputStream();
 *     // Lock the resource within the current block.
 *     try (CloseLock<InputStream> closeLock = CloseLock.lock(input)) {
 *         // Write your code, uncaught exceptions are allowed here.
 *         // Get and read the resource.
 *         closeLock.get().transferTo(output);
 *     }
 *     // The resource will be closed while leaving the block in an unlocked state.
 * }
 * }</pre>
 *
 * @author Sycamore
 * created on 2025/1/6
 */
public class CloseLock<T extends Closeable> implements Closeable {
    private final T resource;
    private boolean locked;

    private CloseLock(T resource) {
        this.resource = resource;
        this.locked = true;
    }

    /**
     * Create a CloseLock with a resource.
     *
     * @param resource the resource to be locked
     * @param <T>      implementation of {@link Closeable}
     * @return the lock
     */
    public static <T extends Closeable> CloseLock<T> lock(T resource) {
        return new CloseLock<>(resource);
    }

    /**
     * Access the resource.
     * <p>You should never return it.
     *
     * @return the resource
     */
    public T get() {
        return resource;
    }

    /**
     * Unlock the resource.
     * <p>You should always return it.
     *
     * @return the unlocked resource
     * @throws IllegalStateException can only be unlocked once
     */
    public T unlock() {
        if (locked) {
            locked = false;
            return resource;
        } else {
            throw new IllegalStateException("the resource has bean unlocked");
        }
    }

    /**
     * Close the resource only if it is locked.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (locked) {
            resource.close();
        }
    }
}
