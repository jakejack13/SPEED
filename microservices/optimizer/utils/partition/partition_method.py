from enum import Enum, auto


class PARTITION_METHOD(Enum):
    """
    Enum representing different partition methods.
    """

    EVEN_SPLIT = auto()
    """
    Represents the even split partition method.
    """

    TIME_OPTIMIZED = auto()
    """
    Represents the time-optimized partition method.
    """
