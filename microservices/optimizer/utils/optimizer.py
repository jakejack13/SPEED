"""Utility module for optimizing job partitions"""

from .partition import PartitionMethod

# TODO: Remove pylint statement
# pylint: disable=unused-argument
def optimize(
    url: str,
    branch: str,
    num_workers: int,
    testclasses: list[str],
    method: PartitionMethod,
) -> list[dict[str, list[dict[str, str]]]]:
    """
    Partitions the given test classes into different clusters of tests to
    minimize total execution time based on previous execution times of tests.

    Arguments
    ---------
    url: str
        The URL of the repository.
    branch: str
        The branch of the repository.
    num_workers: int
        The number of workers to partition for.
    testclasses: list[str]
        The list of test classes to partition.
    method: PARTITION_METHOD
        The method to use for partitioning.

    Returns
    -------
    list[dict[str, list[dict[str, str]]]]
        A list of partitions, each containing a list of test classes in each partition.
    """

    partition_func = method.value
    if not partition_func:
        raise NotImplementedError("Selected partition method is not implemented.")

    partitions = partition_func(num_workers, testclasses)
    if not partitions:
        raise NotImplementedError

    formatted_partitions = [
        {"testclasses": [{"name": test_class} for test_class in partition]}
        for partition in partitions
    ]

    return formatted_partitions
