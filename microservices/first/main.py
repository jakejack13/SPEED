# Endpoint documentation: https://github.com/jakejack13/SPEED/blob/microservices/first_api_doc.md
from utils import DBManager
import utils

from flask import Flask, request, jsonify, Response, g
import json

app = Flask(__name__)

# Set up database manager
DATABASE_FILE: str = 'deployments.db'
db = DBManager(DATABASE_FILE)

def initialize() -> None:
    """Initialize the database and tables."""
    with app.app_context():
        db.create_deployments_table()
        db.create_results_table()

@app.before_request
def before_request() -> None:
    g.db_manager = DBManager(DATABASE_FILE)
    g.db = g.db_manager.get_db()

@app.teardown_request
def teardown_request(exception=None) -> None:
    db_manager = getattr(g, 'db_manager', None)
    if db_manager is not None:
        db_manager.close_connection()

@app.route('/start', methods=['POST'])
def start_deployment() -> tuple[Response, int]:
    """The `start` endpoint. Takes in the url and branch of the repository to 
    build and execute. Spawns a new leader in the form of a Docker container 
    and execute the SPEED deployment. Returns the id of the newly created 
    worker."""
    data = request.form
    if data is None:
        return jsonify({'error': 'Missing request body'}), 400
    url = request.form['url']
    branch = request.form['branch']
    deployment_ID = db.add_deployment(url, branch)
    leader_id = utils.run_docker_container(url, branch, 2, "ghcr.io/jakejack13/speed-leaders:latest", deployment_ID)
    db.add_leader_ID(leader_id, deployment_ID)

    return jsonify({"id": deployment_ID}), 201

@app.route('/info/<int:deployment_ID>', methods=['GET'])
def get_deployment_info(deployment_ID):
    """
    The `info` endpoint. Takes in the id of the SPEED build and returns 
    information about the deployment, including repo_name, repo_branch, and status.
    
    param deployment_ID: The ID of the deployment to get.
    """
    deployment = db.get_deployment(deployment_ID)
    if deployment:
        return jsonify({
            "id": deployment["id"],
            "leader_ID": deployment["leader_ID"],
            "repo_name": deployment["repo_name"],
            "repo_branch": deployment["repo_branch"],
            "status": deployment["status"]
        }), 200
    else:
        return jsonify({"error": "Deployment not found"}), 404

@app.route('/update/<int:deployment_id>', methods=['POST'])
def update_deployment(deployment_id):
    """The `update` endpoint. Takes in the id of the SPEED build and the new 
    results of the build. Used by leaders to update the web server on the 
    build's progress in order to inform users via the `info` endpoint."""
    db.update_deployment_fields(deployment_id, request.json)
    return jsonify({"message": "Deployment updated successfully"}), 200

@app.route('/add_results/<int:deployment_id>', methods=['POST'])
def add_results(deployment_id):
    """Endpoint to add results for a specific deployment."""
    data = request.json
    if not data or 'results' not in data:
        return jsonify({"error": "Missing results data"}), 400
    
    db.add_results(deployment_id, data['results'])
    return jsonify({"message": "Results added successfully"}), 200

@app.route('/results/<int:deployment_id>', methods=['GET'])
def get_results(deployment_id):
    """Endpoint to get all results for a specific deployment."""
    results = db.get_results(deployment_id)
    return jsonify({"results": results}), 200


if __name__ == '__main__':
    initialize()
    app.run(debug=True)
