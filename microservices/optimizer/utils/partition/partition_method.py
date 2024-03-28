"""
    Enum representing different partition methods.
"""

from enum import Enum
from functools import partial
from .even_subset_split import even_split


class PartitionMethod(Enum):
    """
    Enum representing different partition methods.
    """

    EVEN_SPLIT = partial(even_split)
    """
    Represents the even split partition method.
    """

    TIME_OPTIMIZED = None
    """
    Represents the time-optimized partition method.
    """
