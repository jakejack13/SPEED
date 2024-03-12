"""Provides method that divides classes equally among a set number of partitions."""


def even_split(
        num_workers: int, testclasses: list[str]
) -> list[set[str]]:
    """
        Split the list of classes into an equal number of input partitions. If lists cannot
        be divided evenly among partitions, include one extra test in each test set up until the
        modulus of className size and partitions.
        :param class_names: List of the classes by package+name
        :param partitions: Number of partitions to create
        :return: A list of approximately evenly split test class name partitions stored as sets
    """

    classes = []

    list_size = len(testclasses)

    start_index = 0
    partition_size = list_size // num_workers
    overflow = list_size % num_workers
    end_index = 0

    for _ in range(num_workers):
        if overflow > 0:
            partition_size += 1
            overflow -= 1

        end_index += partition_size

        if end_index > list_size:
            end_index = list_size

        classes.append(set(testclasses[start_index:end_index]))

        start_index = end_index

    return classes
