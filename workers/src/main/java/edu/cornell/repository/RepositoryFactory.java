package edu.cornell.repository;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ProgressMonitor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A factory class to create Projects from repositories and artifact storage.
 */
@Slf4j
public final class RepositoryFactory {

    /** 
     * The directory to clone the workplace to.
     */
    private static final @NonNull File WORKSPACE = generateWorkplace();

    private RepositoryFactory() {
    }

    /**
     * Generates the temporary directory to clone the repository into.
     * @return the location of the temporary directory
     */
    private static @NonNull File generateWorkplace() {
        try {
            return Files.createTempDirectory("speed_").toFile();
        } catch (IOException e) {
            LOGGER.error("Unable to make cloning directory", e);
            System.exit(1);
            return null; // Unreachable statement
        }
    }

    /**
     * Clones a project from a git repository. Assumes you have already set up your git
     * credentials if cloning from a private or otherwise protected repository.
     * @param url the url to clone the repo from
     * @param branch the branch to clone
     * @return a Project object representing the clone project
     * @throws RepositoryCloneException when a problem arises with cloning the repository
     * @throws IOException when a problem arises with reading the repository on disk
     */
    public static @NonNull Repository fromGitRepo(@NonNull String url, @NonNull String branch)
            throws RepositoryCloneException, IOException {
        try {
            String[] split = url.split("/");
            String name = split[split.length - 1].replaceAll(".git", "");
            File topDir = Paths.get(WORKSPACE.getAbsolutePath() + name).toFile();
            @Cleanup Git repo = Git.cloneRepository()
                    .setURI(url)
                    .setBranch(branch)
                    .setDirectory(topDir)
                    .setDepth(1)
                    .setProgressMonitor(new GitProgressLog())
                    .call();
            return new JUnit5RepositoryImpl(topDir);
        } catch (GitAPIException | ConfigSyntaxException e) {
            LOGGER.error("Error while cloning repository " + url, e);
            throw new RepositoryCloneException("Error while cloning repository " + url, e);
        }
    }

    @Slf4j
    private static final class GitProgressLog implements ProgressMonitor {

        /** 
         * The total number of tasks to run.
         */
        private int totalTasks;
        /** 
         * The number of completed tasks.
         */
        private int currentTasks;
        /** 
         * The name of the current task.
         */
        private @NonNull String currentTaskName;
        /** 
         * The total number of work units to be done on the current task.
         */
        private int totalWork;
        /** 
         * The number of completed work units.
         */
        private int currentWork;

        private GitProgressLog() {
            totalTasks = 0;
            currentTasks = 0;
            totalWork = 0;
            currentWork = 0;
            currentTaskName = "";
        }

        @Override
        public void start(int totalTasks) {
            this.totalTasks = totalTasks;
            LOGGER.info("GIT TASK START");
        }

        @Override
        public void beginTask(String title, int totalWork) {
            this.currentWork = 0;
            this.totalWork = totalWork;
            this.currentTaskName = title;
            LOGGER.info("GIT SUBTASK START: " + title +
                    " (" + currentTasks + "/" + totalTasks + ")");
        }

        @Override
        public void update(int completed) {
            currentWork += completed;
            LOGGER.info(currentTaskName + ": " + currentWork + "/" + totalWork);
        }

        @Override
        public void endTask() {
            currentTasks++;
            LOGGER.info("GIT SUBTASK END: " + currentTaskName);
        }

        @Override
        public boolean isCancelled() {
            return false; // Should never be cancelled by user
        }

        @Override
        public void showDuration(boolean enabled) {
            // Do nothing
        } 
    }
}
