"""Utility module for interacting with the statuses of current first deployments"""
from enum import Enum, auto

class DeploymentStatus(Enum):
    """Represents the status of a deployment.

    STARTED - Deployment has been added to database but leader has not distributed work.
    IN_PROGRESS - Leader has start distributing work.
    DONE - Leader has ended its process.
    FAILED - Leader ran into an error
    """
    STARTED = auto()
    """The deployment has started but not yet built"""
    BUILDING = auto()
    """The deployment is currently building"""
    IN_PROGRESS = auto()
    """The deployment is currently running tests"""
    DONE = auto()
    """The deployment has completed successfully"""
    FAILED = auto()
    """The deployment has completed unsuccessfully"""
