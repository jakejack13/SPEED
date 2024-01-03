package edu.cornell;

import java.io.Closeable;
import java.io.Serial;
import java.util.HashSet;

public class CloseableSet<T extends AutoCloseable> extends HashSet<T> implements Closeable {

    @Serial
    private static final long serialVersionUID = 223344;

    @Override
    public void close() {
        Exception ex = null;
        for (T elm : this) {
            try {
                elm.close();
            } catch (Exception e) {
                ex = e;
            }
        }
        if (ex != null) {
            throw new RuntimeException(ex);
        }
    }
}
