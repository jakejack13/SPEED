"""Utility module for managing optDB"""

from pymongo import MongoClient
from datetime import datetime

# Number of execution times to keep track of for each test class
NUM_ENTRIES: int = 20


class DBManager:
    def __init__(
        self, uri: str = "mongodb://localhost:27017/", db_name: str = "optDB"
    ) -> None:
        self.client = MongoClient(uri)
        self.db = self.client[db_name]
        self.collection = self.db["execution_times"]

    def update_execution_time(
        self,
        repository_url: str,
        branch: str,
        test_class_name: str,
        execution_time: float,
    ) -> None:
        """
        Updates the execution time for a given test class in the database.
        Keeps only the last NUM_ENTRIES execution times for each test class.
        """
        query = {
            "repository_url": repository_url,
            "branch": branch,
            "test_classes.name": test_class_name,
        }
        update = {
            "$push": {
                "test_classes.$.times": {
                    "$each": [
                        {"execution_time": execution_time, "timestamp": datetime.now()}
                    ],
                    "$slice": -NUM_ENTRIES,
                }
            }
        }
        self.collection.update_one(query, update, upsert=True)
