"""
    Enum representing different partition methods.
"""

from enum import Enum, auto

class ParitionMethod(Enum):
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
