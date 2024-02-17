from enum import Enum, auto

'''
Represents the status of a deployment.

STARTED - Deployment has been added to database but leader has not distributed work.
IN_PROGRESS - Leader has start distributing work.
DONE - Leader has ended its process.
FAILED - Leader ran into an error
'''
class DeploymentStatus(Enum):
    STARTED = auto()
    IN_PROGRESS = auto()
    DONE = auto()
    FAILED = auto()