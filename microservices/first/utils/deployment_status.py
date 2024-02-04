from enum import Enum, auto

class DeploymentStatus(Enum):
    STARTED = auto()
    IN_PROGRESS = auto()
    DONE = auto()
    FAILED = auto()