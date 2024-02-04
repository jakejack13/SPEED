import json
import sqlite3
from sqlite3 import Error
from typing import Any, List, Optional

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
    conn = None
    try:
        conn = sqlite3.connect(self.db_file)
        return conn
    except Error as e:
        print(e)
    return conn

  def close_connection(self) -> None:
    """Close the database connection."""
    if self.conn:
        self.conn.close()

  def create_deployments_table(self) -> None:
    """Create the deployments table in the database."""
    try:
        c = self.conn.cursor()
        c.execute(
                  '''CREATE TABLE IF NOT EXISTS deployments (
                  id INTEGER PRIMARY KEY, 
                  repo_name TEXT, 
                  repo_branch TEXT, 
                  results TEXT)'''
                )
    except Error as e:
        print(e)

  def add_deployment(self, repo_name: str, repo_branch: str) -> int:
    """
    Add a new deployment to the database.
    
    param repo_name: The name of the repository for the deployment.
    param repo_branch: The branch of the repository for the deployment.
    """
    sql = '''INSERT INTO deployments(repo_name, repo_branch, results) VALUES(?,?,?)'''
    cur = self.conn.cursor()
    cur.execute(sql, (repo_name, repo_branch, json.dumps([])))
    self.conn.commit()
    return cur.lastrowid

  def update_deployment(self, deployment_id: int, new_results: List[Any]) -> None:
    """
    Update the results of an existing deployment.
    
    param deployment_id: The ID of the deployment to update.
    param new_results: A list of new results to append to the existing results.
    """
    cur = self.conn.cursor()
    cur.execute("SELECT results FROM deployments WHERE id=?", (deployment_id,))
    row = cur.fetchone()
    if row:
      total_results = json.loads(row[0])
      total_results.extend(new_results)
      
      sql = '''UPDATE deployments SET results = ? WHERE id = ?'''
      cur.execute(sql, (json.dumps(total_results), deployment_id))
      self.conn.commit()

  def get_deployment(self, deployment_id: int) -> Optional[List[Any]]:
    """
    Get a deployment by ID, including its results.

    param deployment_id: The ID of the deployment to retrieve.
    """
    cur = self.conn.cursor()
    cur.execute("SELECT * FROM deployments WHERE id=?", (deployment_id,))
    row = cur.fetchone()
    if row:
      row = list(row)
      row[3] = json.loads(row[3])  # Convert the results back to a Python list
      return row
    return None

