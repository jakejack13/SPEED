import sqlite3
from typing import Union

from . import deployment_status

"""
Deployment Table Schema:
id: Integer, Primary key identifying each row
leader_ID: Integer, Indentifier of deployment
repo_name: TEXT, Name of repo the deployment is working on
repo_branch: TEXT, Branch of the repo being worked on
status: TEXT, (DeploymentStatus in text form) Status of the deployment.

Results Table Schema:
id: Integer, Primary key identifying each row
leader_ID: Integer, Foreign Key pointing to Deployment Table "leader_id". Deployment the result is associated with.
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

  def create_connection(self) -> sqlite3.Connection:
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
              leader_ID INTEGER UNIQUE,
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
            leader_ID INTEGER,
            result TEXT,
            FOREIGN KEY(leader_id) REFERENCES deployments(id)
        )'''
    )
  
  def add_results(self, leader_id: int, results: list[str]) -> None:
    """Add multiple results to a specific deployment."""
    sql = '''INSERT INTO results(leader_id, result) VALUES(?, ?)'''
    cur = self.conn.cursor()
    for result in results:
        cur.execute(sql, (leader_id, result))
    self.conn.commit()

  def get_results(self, leader_id: int) -> list[str]:
    """Retrieve all results for a specific deployment."""
    cur = self.conn.cursor()
    cur.execute("SELECT result FROM results WHERE leader_id=?", (leader_id,))
    results = [row[0] for row in cur.fetchall()]
    return results

  def add_deployment(self, leader_id: str, repo_name: str, repo_branch: str) -> int:
    """
    Add a new deployment to the database.
    
    param leader_id: The leader's ID
    param repo_name: The name of the repository for the deployment.
    param repo_branch: The branch of the repository for the deployment.
    """
    sql = '''INSERT INTO deployments(leader_id, repo_name, repo_branch) VALUES(?,?,?)'''
    cur = self.conn.cursor()
    cur.execute(sql, (int(leader_id), repo_name, repo_branch))
    self.conn.commit()
    return cur.lastrowid or -1

  def update_deployment_fields(self, leader_id: int, updates: dict[str, Union[str,int]]) -> None:
    """
    Update specified fields of an existing deployment.

    :param leader_id: The ID of the leader to update.
    :param updates: A dictionary where keys are column names and values are the new values for those columns.
    """
    parameters = [f"{key} = ?" for key in updates.keys()]
    sql = f"UPDATE deployments SET {', '.join(parameters)} WHERE id = ?"
    values = list(updates.values()) + [leader_id]
    
    cur = self.conn.cursor()
    cur.execute(sql, values)
    self.conn.commit()

  def get_deployment(self, leader_id: int) -> dict[str, Union[str, int, deployment_status.DeploymentStatus]] | None:
    """
    Get a deployment's information by ID.

    param leader_id: The ID of the leader to retrieve.
    """
    cur = self.conn.cursor()
    cur.execute("SELECT * FROM deployments WHERE id=?", (leader_id,))
    row = cur.fetchone()
    if row:
        columns = ['id', 'leader_ID', 'repo_name', 'repo_branch', 'status']
        return dict(zip(columns, row))
    return None

