"""
    Enum representing different partition methods.
"""

from enum import Enum
from .even_subset_split import even_split

class ParitionMethod(Enum):
    """
    Enum representing different partition methods.
    """

    EVEN_SPLIT = even_split
    """
    Represents the even split partition method.
    """

    TIME_OPTIMIZED = None
    """
    Represents the time-optimized partition method.
    """
