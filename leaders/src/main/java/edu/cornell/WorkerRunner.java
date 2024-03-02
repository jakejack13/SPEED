package edu.cornell;

import edu.cornell.worker.Worker;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;

/** 
 * The runner that spawns new workers to run test suites.
 */
@Slf4j
@ToString
public class WorkerRunner implements Runnable, Closeable {

    /**
     * The set of workers.
     */
    private final @NonNull Set<Worker> workers;
    /**
     * The set of threads running the workers.
     */
    private final @NonNull Set<Thread> threads = new HashSet<>();

    /**
     * Creates a new WorkerRunner that runs the specified workers.
     * @param workers the workers to run, is modifiable to add more workers
     *                before executing this runner
     */
    WorkerRunner(@NonNull CloseableSet<Worker> workers) {
        this.workers = workers;
    }

    @Override
    public void run() {
        threads.addAll(workers.stream().map(Thread::new).toList());
        LOGGER.info("Executing WorkerRunner: " + this);
        threads.forEach(Thread::start);
        LOGGER.info("Started all threads");
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Finished WorkerRunner execution");
    }

    @Override
    public void close() {
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
