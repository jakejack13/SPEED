import sqlite3
from flask import g
import json

from . import deployment_status

"""
Deployment Table Schema:
id: Integer, Primary key identifying each row
leader_ID: TEXT, Indentifier of deployment
repo_name: TEXT, Name of repo the deployment is working on
repo_branch: TEXT, Branch of the repo being worked on
status: TEXT, (DeploymentStatus in text form) Status of the deployment.

Results Table Schema:
id: Integer, Primary key identifying each row
deployment_ID: Integer, Foreign Key pointing to Deployment Table "id". Deployment the result is associated with.
result: TEXT, one test result object.
"""
class DBManager:

  def _get_db(self) -> sqlite3.Connection:
        db = getattr(g, '_database', None)
        if db is None:
            db = g._database = sqlite3.connect(self.db_file)
        return db

  def close_connection(self) -> None:
      db = getattr(g, '_database', None)
      if db is not None:
          db.close()

  def __init__(self, db_file: str) -> None:
    """
    Initialize the database manager with the path to the database file.
    
    param db_file: The file path of the SQLite database.
    """
    self.db_file = db_file

  def create_deployments_table(self) -> None:
    """Create the deployments table in the database."""
    c = self._get_db().cursor()
    c.execute(
              '''CREATE TABLE IF NOT EXISTS deployments (
              id INTEGER PRIMARY KEY, 
              leader_ID TEXT,
              repo_name TEXT, 
              repo_branch TEXT,
              status TEXT DEFAULT 'STARTED')'''
            )

  def create_results_table(self) -> None:
    """Create the results table in the database."""
    c = self._get_db().cursor()
    c.execute(
        '''CREATE TABLE IF NOT EXISTS results (
            id INTEGER PRIMARY KEY,
            deployment_ID INTEGER,
            result TEXT,
            FOREIGN KEY(deployment_ID) REFERENCES deployments(id)
        )'''
    )

  def add_results(self, deployment_id: int, results: dict[str, dict[str, int | str]]) -> None:
    """Add multiple results to a specific deployment."""
    sql = '''INSERT INTO results(deployment_id, result) VALUES(?, ?)'''
    cur = self._get_db().cursor()
    for k, v in results.items():
        result = json.dumps({k: v})
        cur.execute(sql, (deployment_id, result))
    self._get_db().commit()

  def get_results(self, deployment_id: int) -> list[str]:
    """Retrieve all results for a specific deployment."""
    cur = self._get_db().cursor()
    cur.execute("SELECT result FROM results WHERE deployment_ID=?", (deployment_id,))
    results = [row[0] for row in cur.fetchall()]
    return results

  def add_deployment(self, repo_name: str, repo_branch: str) -> int:
    """
    Add a new deployment to the database.

    param repo_name: The name of the repository for the deployment.
    param repo_branch: The branch of the repository for the deployment.
    """
    sql = '''INSERT INTO deployments(leader_ID, repo_name, repo_branch) VALUES(?,?,?)'''
    cur = self._get_db().cursor()
    cur.execute(sql, ("Unassigned", repo_name, repo_branch))
    self._get_db().commit()
    return cur.lastrowid or -1

  def add_leader_ID(self, leader_ID: str, deployment_ID: int) -> None:
    """
    Add a new leader_ID to the deployment in the database.
    
    param leader_ID: The leader's ID
    param deployment_ID: The ID of the deployment to update
    """
    sql = '''UPDATE deployments SET leader_ID = ? WHERE id = ?'''
    cur = self._get_db().cursor()
    cur.execute(sql, (leader_ID, deployment_ID))
    self._get_db().commit()

  def update_deployment_fields(self, deployment_id: int, updates: dict[str, str]) -> None:
    """
    Update specified fields of an existing deployment.

    :param leader_ID: The ID of the leader to update.
    :param updates: A dictionary where keys are column names and values are the new values for those columns.
    """
    parameters = [f"{key} = ?" for key in updates.keys()]
    sql = f"UPDATE deployments SET {', '.join(parameters)} WHERE id = ?"
    values = list(updates.values()) + [deployment_id]
    
    cur = self._get_db().cursor()
    cur.execute(sql, values)
    self._get_db().commit()

  def get_deployment(self, deployment_id: int) -> dict[str, str | int | deployment_status.DeploymentStatus] | None:
    """
    Get a deployment's information by ID.

    param deployment_id: The ID of the deployment to retrieve.
    """
    cur = self._get_db().cursor()
    cur.execute("SELECT * FROM deployments WHERE id=?", (deployment_id,))
    row = cur.fetchone()
    if row:
        columns = ['id', 'leader_ID', 'repo_name', 'repo_branch', 'status']
        return dict(zip(columns, row))
    
    return None
