import json
import sqlite3
from sqlite3 import Error
from typing import Any, Dict, List, Optional, Union

import .deployment_status

"""
Deployment Table Schema:
id: Integer, Primary key identifying each row
deployment_id: Integer, Indentifier of deployment
repo_name: TEXT, Name of repo the deployment is working on
repo_branch: TEXT, Branch of the repo being worked on
status: TEXT, (DeploymentStatus in text form) Status of the deployment.

Results Table Schema:
id: Integer, Primary key identifying each row
deployment_id: Integer, Foreign Key pointing to Deployment Table "deployment_id". Deployment the result is associated with.
result: TEXT, one test result object.
"""
class DBManager:
  def __init__(self, db_file: str) -> None:
    """
    Initialize the database manager with the path to the database file.
    
    param db_file: The file path of the SQLite database.
    """
    self.db_file = db_file
    self.conn = self.create_connection()

  def create_connection(self) -> Optional[sqlite3.Connection]:
    """Create a database connection to the SQLite database."""
    conn = sqlite3.connect(self.db_file)
    return conn

  def close_connection(self) -> None:
    """Close the database connection."""
    if self.conn:
        self.conn.close()

  def create_deployments_table(self) -> None:
    """Create the deployments table in the database."""
    c = self.conn.cursor()
    c.execute(
              '''CREATE TABLE IF NOT EXISTS deployments (
              id INTEGER PRIMARY KEY, 
              repo_name TEXT, 
              repo_branch TEXT,
              status TEXT DEFAULT 'STARTED')'''
            )

  def create_results_table(self) -> None:
    """Create the results table in the database."""
    c = self.conn.cursor()
    c.execute(
        '''CREATE TABLE IF NOT EXISTS results (
            id INTEGER PRIMARY KEY,
            deployment_id INTEGER,
            result TEXT,
            FOREIGN KEY(deployment_id) REFERENCES deployments(id)
        )'''
    )

  def add_result(self, deployment_id: int, result: str) -> int:
    """
    Add a new result to a specific deployment.

    Returns: Row number of result.
    """
    sql = '''INSERT INTO results(deployment_id, result) VALUES(?, ?)'''
    cur = self.conn.cursor()
    cur.execute(sql, (deployment_id, result))
    self.conn.commit()
    return cur.lastrowid
  
  def add_results(self, deployment_id: int, results: list) -> None:
    """Add multiple results to a specific deployment."""
    sql = '''INSERT INTO results(deployment_id, result) VALUES(?, ?)'''
    cur = self.conn.cursor()
    for result in results:
        cur.execute(sql, (deployment_id, result))
    self.conn.commit()

  def get_results(self, deployment_id: int) -> list[str]:
    """Retrieve all results for a specific deployment."""
    cur = self.conn.cursor()
    cur.execute("SELECT result FROM results WHERE deployment_id=?", (deployment_id,))
    results = [row[0] for row in cur.fetchall()]
    return results

  def add_deployment(self, repo_name: str, repo_branch: str) -> int:
    """
    Add a new deployment to the database.
    
    param repo_name: The name of the repository for the deployment.
    param repo_branch: The branch of the repository for the deployment.
    """
    sql = '''INSERT INTO deployments(repo_name, repo_branch) VALUES(?,?)'''
    cur = self.conn.cursor()
    cur.execute(sql, (repo_name, repo_branch))
    self.conn.commit()
    return cur.lastrowid

  def update_deployment_fields(self, deployment_id: int, updates: dict[str, str]) -> None:
    """
    Update specified fields of an existing deployment.

    :param deployment_id: The ID of the deployment to update.
    :param updates: A dictionary where keys are column names and values are the new values for those columns.
    """
    parameters = [f"{key} = ?" for key in updates.keys()]
    sql = f"UPDATE deployments SET {', '.join(parameters)} WHERE id = ?"
    values = list(updates.values()) + [deployment_id]
    
    cur = self.conn.cursor()
    cur.execute(sql, values)
    self.conn.commit()

  def get_deployment(self, deployment_id: int) -> Dict[str, Union[str, int, deployment_status.DeploymentStatus]]:
    """
    Get a deployment's information by ID.

    param deployment_id: The ID of the deployment to retrieve.
    """
    cur = self.conn.cursor()
    cur.execute("SELECT * FROM deployments WHERE id=?", (deployment_id,))
    row = cur.fetchone()
    if row:
        columns = ['id', 'repo_name', 'repo_branch', 'status']
        return dict(zip(columns, row))
    return None

