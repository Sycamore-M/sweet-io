# sweet-io

A better and safer way to autoclose resources.

Usually, we use try-with-resources to securely close resources. But when resources need to be returned, there is no
suitable way to ensure that the resources are not leaked:

```java
public InputStream getInputStream(String filename) throws IOException {
    // Can be a network input stream.
    FileInputStream inputStream = new FileInputStream(filename);
    // 1. Complex logic causes the resource to be discarded.
    // 2. Uncaught exceptions occur and the resource is leaked.
    // ......
    return inputStream;
}
```

`CloseLock` can solve this problem with minimal changes.

If you need to return a resource:

```java
public InputStream getInputStream(String filename) throws IOException {
    // Or a network input stream.
    try (CloseLock<InputStream> lock = CloseLock.lock(new FileInputStream(filename))) {
        // The resource will never be leaked.
        // Unlocked resource must be returned.
        return lock.unlock();
    }
}
```

If you need to read a resource:

```java
public void readInputStream(String filename, OutputStream out) throws IOException {
    try (CloseLock<InputStream> lock = CloseLock.lock(getInputStream(filename))) {
        // The resource will be closed automatically.
        // Never call unlock if the resource is not returned.
        lock.get().transferTo(out);
    }
}
```
