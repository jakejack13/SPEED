package edu.cornell.workers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import lombok.NonNull;

/** A representation of a worker running on a Docker container */
public class DockerWorker implements Worker {

    /** The name to the Docker image of the workers */
    private static final String IMAGE_NAME = "jakejack13/speed-workers";

    /** The Docker configuration settings */
    private static final @NonNull DockerClientConfig config = DefaultDockerClientConfig
            .createDefaultConfigBuilder()
            .build();

    /** The Docker HTTP client */
    private static final @NonNull DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();

    /** The Docker client */
    private static final @NonNull DockerClient client =
            DockerClientImpl.getInstance(config, httpClient);

    /** The id of this Docker container */
    private final String id;

    DockerWorker() throws WorkerStartupException {
        try {
            id = client.createContainerCmd(IMAGE_NAME)
                    .withEnv("") // TODO
                    .exec()
                    .getId();
        } catch (NotFoundException | ConflictException e) {
            throw new WorkerStartupException();
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void start() {
        client.startContainerCmd(id)
                .exec();
    }

    @Override
    public boolean isDone() {
        return false; // TODO
    }

    @Override
    public void cleanup() {
        client.removeContainerCmd(id)
                .withForce(true)
                .exec();
    }
}
