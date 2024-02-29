package edu.cornell;

import java.io.Closeable;
import java.io.Serial;
import java.util.HashSet;

/**
 * A set which can be closed, which closes all of its contained elements.
 * @param <T> the type of objects contained in this set
 */
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
