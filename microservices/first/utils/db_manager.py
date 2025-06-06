"""Utility module for managing FirstDB, the database for First

Deployment Table Schema:
id: Integer, Primary key identifying each row
leader_ID: TEXT, Indentifier of deployment
repo_name: TEXT, Name of repo the deployment is working on
repo_branch: TEXT, Branch of the repo being worked on
status: TEXT, (DeploymentStatus in text form) Status of the deployment.

Results Table Schema:
id: Integer, Primary key identifying each row
deployment_ID: Integer, Foreign Key pointing to Deployment Table "id".
    Deployment the result is associated with.
result: TEXT, one test result object.
"""

import json
import os

from flask import g
import psycopg
from psycopg.rows import TupleRow

from . import deployment_status

# pylint: disable=no-member

DEPLOYMENT_FIELDS = ["leader_ID", "repo_name", "repo_branch", "status"]
RESULTS_FIELDS = ["deployment_ID", "result"]


class DBManager:
    """A class that allows manipulation of FirstDB"""

    def _get_db(self) -> psycopg.Connection[TupleRow]:
        """Returns the current database connection"""
        db = getattr(g, "_database", None)
        if db is None:
            db = g._database = psycopg.connect(
                dbname=os.environ["POSTGRES_DB"],
                host="firstdb",
                user=os.environ["POSTGRES_USER"],
                password=os.environ["POSTGRES_PASSWORD"],
                port="5432",
            )
        return db

    def close_connection(self) -> None:
        """Closes the database connection"""
        db = getattr(g, "_database", None)
        if db is not None:
            db.close()

    def __init__(self, db_file: str) -> None:
        """Initialize the database manager with the path to the database file.

        param db_file: The file path of the SQLite database.
        """
        self.db_file = db_file

    def create_deployments_table(self) -> None:
        """Create the deployments table in the database."""
        with self._get_db().cursor() as c:
            c.execute(
                """CREATE TABLE IF NOT EXISTS deployments (
                    id SERIAL PRIMARY KEY, 
                    leader_ID TEXT,
                    repo_name TEXT, 
                    repo_branch TEXT,
                    status TEXT DEFAULT 'STARTED')"""
            )
            self._get_db().commit()

    def create_results_table(self) -> None:
        """Create the results table in the database."""
        with self._get_db().cursor() as c:
            c.execute(
                """CREATE TABLE IF NOT EXISTS results (
                    id SERIAL PRIMARY KEY,
                    deployment_ID INTEGER,
                    result TEXT,
                    FOREIGN KEY(deployment_ID) REFERENCES deployments(id)
                )"""
            )
            self._get_db().commit()

    def add_results(
        self, deployment_id: int, results: dict[str, dict[str, int | str]]
    ) -> None:
        """Add multiple results to a specific deployment."""
        sql = """INSERT INTO results(deployment_id, result) VALUES(%s, %s)"""
        with self._get_db().cursor() as c:
            for k, v in results.items():
                result = json.dumps({k: v})
                c.execute(sql, (deployment_id, result))
            self._get_db().commit()

    def get_results(self, deployment_id: int) -> list[str]:
        """Retrieve all results for a specific deployment."""
        with self._get_db().cursor() as c:
            c.execute(
                "SELECT result FROM results WHERE deployment_ID=%s", (deployment_id,)
            )
            results = [row[0] for row in c.fetchall()]
            return results

    def add_deployment(self, repo_name: str, repo_branch: str) -> int:
        """
        Add a new deployment to the database.

        param repo_name: The name of the repository for the deployment.
        param repo_branch: The branch of the repository for the deployment.
        """
        sql = """INSERT INTO deployments(leader_ID, repo_name, repo_branch) VALUES(%s,%s,%s)
        RETURNING id"""
        with self._get_db().cursor() as c:
            c.execute(sql, ("Unassigned", repo_name, repo_branch))
            id_tuple = c.fetchone()
            if id_tuple is None:
                raise ValueError()
            self._get_db().commit()
            deployment_id = id_tuple[0]
            return int(deployment_id)

    def add_leader_id(self, leader_id: str, deployment_id: int) -> None:
        """
        Add a new leader_id to the deployment in the database.

        param leader_id: The leader's ID
        param deployment_id: The ID of the deployment to update
        """
        sql = """UPDATE deployments SET leader_ID = %s WHERE id = %s"""
        with self._get_db().cursor() as c:
            c.execute(sql, (leader_id, deployment_id))
            self._get_db().commit()

    def update_deployment_fields(
        self, deployment_id: int, updates: dict[str, str]
    ) -> None:
        """
        Update specified fields of an existing deployment.

        :param deployment_id: The ID of the deployment to update.
        :param updates: A dictionary where keys are column names and values are
            the new values for those columns.
        """
        parameters = [f"{key} = %s" for key in DEPLOYMENT_FIELDS]
        sql = f"UPDATE deployments SET {', '.join(parameters)} WHERE id = %s"
        values = list(updates[key] for key in DEPLOYMENT_FIELDS if key in updates) + [
            str(deployment_id)
        ]
        if len(values) == 0:
            return
        with self._get_db().cursor() as c:
            for value in values:
                c.execute(sql.encode(), value)
            self._get_db().commit()

    def get_deployment(
        self, deployment_id: int
    ) -> dict[str, str | int | deployment_status.DeploymentStatus] | None:
        """
        Get a deployment's information by ID.

        param deployment_id: The ID of the deployment to retrieve.
        """
        with self._get_db().cursor() as c:
            c.execute("SELECT * FROM deployments WHERE id=%s", (deployment_id,))
            row = c.fetchone()
            if row:
                columns = ["id", "leader_ID", "repo_name", "repo_branch", "status"]
                return dict(zip(columns, row))
        return None
