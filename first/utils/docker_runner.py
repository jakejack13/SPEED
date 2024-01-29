"""Utility module for interacting with the docker daemon"""

import subprocess

def run_docker_container(repo, branch, num_workers, img_url):
    command = [
        "docker", "run", "-d",
        "-v", "/var/run/docker.sock:/var/run/docker.sock",
        "-e", f"SPEED_REPO_URL={repo}",
        "-e", f"SPEED_REPO_BRANCH={branch}",
        "-e", "SPEED_KAFKA_ADDRESS=kafka:9092",
        "-e", f"SPEED_NUM_WORKERS={str(num_workers)}",
        "--network", "kafka_default",
        img_url
    ]
    subprocess.run(command, check=True)
