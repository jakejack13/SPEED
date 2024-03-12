"""Utility module for optimizing job partitions"""

from .partition import *

def optimize(
    url: str, branch: str, num_workers: int, testclasses: list[str], method: PARTITION_METHOD
) -> list[dict[str, list[dict[str, str]]]]:
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

    partitions = None

    match method:
        case PARTITION_METHOD.TIME_OPTIMIZED:
            raise NotImplementedError
        case PARTITION_METHOD.EVEN_SPLIT:
            partitions = even_split(num_workers, testclasses)

    if not partitions:
        raise Exception("Partitioning Returned None")

    formatted_partitions = [{"testclasses": [{"name": test_class}
                                             for test_class in partition]} for partition in partitions]

    return formatted_partitions
