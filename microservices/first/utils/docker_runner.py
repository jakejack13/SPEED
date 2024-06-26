"""Utility module for interacting with the docker daemon"""

import subprocess


def run_docker_container(
    repo: str, branch: str, num_workers: int, img_url: str, deployment_id: int
) -> str:
    """
    Run a Docker Leader container with specified parameters and capture the docker leader ID.

    Args:
        repo (str): The URL of the repository containing the application code.
        branch (str): The branch of the repository to use.
        num_workers (int): The number of worker instances to run.
        img_url (str): The URL of the Docker image to use.
        deployment_id (str): The id of the deployment

    Returns:
        str: The output of the Docker command.

    Raises:
        subprocess.CalledProcessError: If the Docker command fails.

    Note:
        This function assumes that Docker is installed and the Docker daemon is running.

        Environment Variables:
        - SPEED_REPO_URL: URL of the repository.
        - SPEED_REPO_BRANCH: Branch of the repository.
        - SPEED_KAFKA_ADDRESS: Kafka address (default is kafka:9092).
        - SPEED_NUM_WORKERS: Number of worker instances.
        - Docker socket volume mounted: Required for Docker commands within the container.

    Example:
        output = run_docker_container(
            repo="https://github.com/example/repo.git",
            branch="main",
            num_workers=4,
            img_url="example/image:latest"
        )
        print("Output:", output)
    """
    command = [
        "docker",
        "run",
        "-d",
        "-v",
        "/var/run/docker.sock:/var/run/docker.sock",
        "-e",
        f"SPEED_REPO_URL={repo}",
        "-e",
        f"SPEED_REPO_BRANCH={branch}",
        "-e",
        "SPEED_KAFKA_ADDRESS=host.docker.internal:9092",
        "-e",
        f"SPEED_NUM_WORKERS={num_workers}",
        "-e",
        f"DEPLOYMENT_ID={deployment_id}",
        img_url,
    ]

    result = subprocess.run(command, check=True, text=True, capture_output=True)
    return result.stdout.strip()[:12]
