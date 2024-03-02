"""Utility module for optimizing job partitions"""


def optimize(
    url: str, branch: str, num_workers: int, testclasses: list[str]
) -> list[list[str]]:
    """Partitions the given test classes into different clusters of test to
    minimize total execution time based on previous execution times of tests

    Arguments
    ---------
    url: str
        the url of the repository
    branch: str
        the branch of the repository
    num_workers: int
        the number of workers to partition for
    testclasses: list[str]
        the list of test classes to partition

    Returns
    -------
    list[list[str]]
        a list of partitions, which are a list of test classes in each partition"""
    raise NotImplementedError
