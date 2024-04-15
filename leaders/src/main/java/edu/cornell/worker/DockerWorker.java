package edu.cornell.worker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import static edu.cornell.Main.ENV_KAFKA_ADDRESS;
import static edu.cornell.Main.ENV_REPO_BRANCH;
import static edu.cornell.Main.ENV_REPO_TESTS;
import static edu.cornell.Main.ENV_REPO_URL;

/** 
 * A worker that runs inside a Docker container.
 */
@Slf4j
@EqualsAndHashCode
@ToString
public class DockerWorker implements Worker {

    /**
     * The link to the workers Docker image.
     */
    private static final String IMAGE_URL = "ghcr.io/jakejack13/speed-workers:latest";

    /**
     * The Docker http client.
     */
    private final DockerHttpClient httpClient;

    /**
     * The Docker client.
     */
    private final DockerClient dockerClient;
    /**
     * The id of the container running the worker.
     */
    private final String id;

    /**
     * true if the worker has finished running, false otherwise.
     */
    private boolean done = false;

    /**
     * Creates a new DockerWorker.
     * @param repoUrl the url of the repository
     * @param repoBranch the branch of the repository to test
     * @param tests the set of tests to run
     * @param kafkaAddress the address of the Kafka message bus to send test results to
     */
    DockerWorker(@NonNull String repoUrl, @NonNull String repoBranch, @NonNull Set<String> tests,
            @NonNull String kafkaAddress) {
        DockerClientConfig config =
                DefaultDockerClientConfig.createDefaultConfigBuilder()
                        .build();

        httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        dockerClient = DockerClientImpl.getInstance(config, httpClient);

        id = dockerClient.createContainerCmd(IMAGE_URL)
                .withEnv(ENV_REPO_URL + '=' + repoUrl,
                        ENV_REPO_BRANCH + '=' + repoBranch,
                        ENV_REPO_TESTS + '=' + String.join(",", tests),
                        ENV_KAFKA_ADDRESS + '=' + kafkaAddress)
                .exec().getId();
    }

    @Override
    public @NonNull String getId() {
        return id;
    }

    @Override
    public synchronized void run() {
        LOGGER.info("Executing DockerWorker: " + id);
        dockerClient.startContainerCmd(id).exec();
        dockerClient.waitContainerCmd(id).exec(new WaitCallback<>());
        while (!done) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        LOGGER.info("Finished executing DockerWorker: " + id);
    }

    @Override
    public void close() {
        try {
            dockerClient.close();
            httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets done to true.
     */
    private synchronized void done() {
        done = true;
        notifyAll();
    }

    /**
     * A callback to the Docker wait command.
     * @param <T> the type of objects returned by this callback
     */
    private class WaitCallback<T> implements ResultCallback<T> {

        @Override
        public void onStart(Closeable closeable) {
            // No-op
        }

        @Override
        public void onNext(T object) {
            // No-op
        }

        @Override
        public void onError(Throwable throwable) {
            LOGGER.error("Error with Docker daemon", throwable);
            done();
        }

        @Override
        public void onComplete() {
            LOGGER.info("Finished executing wait command");
            done();
        }

        @Override
        public void close() throws IOException {
            // No-op
        }
    }
}
