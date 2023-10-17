package edu.cornell.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A factory class to create Projects from repositories and artifact storage
 * @author Jacob Kerr
 */
@Slf4j
public final class ProjectFactory {

    /** The directory to clone the workplace to */
    private static final File WORKSPACE;

    static {
        try {
            WORKSPACE = Files.createTempDirectory("tmpDirPrefix").toFile();
        } catch (IOException e) {
            LOGGER.error("Unable to make cloning directory");
            throw new RuntimeException(e);
        }
    }

    /**
     * Clones a project from a GitLab repository
     * @param url the url to clone the repo from
     * @param branch the branch to clone
     * @param credentials the GitLab credentials to use
     * @return a Project object representing the clone project
     */
    public static @NotNull Project fromGitLab(@NotNull String url, @NotNull String branch, @Nullable Object credentials)
            throws GitAPIException {
        String[] split = url.split("/");
        String name = split[split.length-1].replaceAll(".git","");
        try (@NotNull Repository repo = Git.cloneRepository()
                .setURI(url)
                .setDirectory(Paths.get(WORKSPACE.getAbsolutePath() + name).toFile())
                .call().getRepository()) {
            return new ProjectImpl(repo.getDirectory().getParentFile());
        }
    }
}
