# Endpoint documentation: https://github.com/jakejack13/SPEED/blob/microservices/first_api_doc.md
import json
from utils import DBManager
import utils

from flask import Flask, request, jsonify

DATABASE_FILE: str = 'deployments.db'
app = Flask(__name__)
db = DBManager(DATABASE_FILE)

@app.before_first_request
def initialize():
    """Initialize the database and tables."""
    db.create_deployments_table()
    db.create_results_table()

@app.route('/start', methods=['POST'])
def start_deployment():
    """The `start` endpoint. Takes in the url and branch of the repository to 
    build and execute. Spawns a new leader in the form of a Docker container 
    and execute the SPEED deployment. Returns the id of the newly created 
    worker."""
    data = request.json
    url = request.form['url']
    branch = request.form['branch']
    leader_id = utils.run_docker_container(url, branch, 2, "ghcr.io/jakejack13/speed-leaders:latest")
    db.add_deployment(leader_id, data['url'], data['branch'])

    return jsonify({"id": leader_id}), 201

@app.route('/info/<int:leader_ID>', methods=['GET'])
def get_deployment_info(leader_ID):
    """
    The `info` endpoint. Takes in the id of the SPEED build and returns 
    information about the deployment, including repo_name, repo_branch, and status.
    
    param leader_ID: The ID of the deployment to get.
    """
    deployment = db.get_deployment(leader_ID)
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

@app.route('/update/<int:leader_ID>', methods=['POST'])
def update_deployment(leader_ID):
    """The `update` endpoint. Takes in the id of the SPEED build and the new 
    results of the build. Used by leaders to update the web server on the 
    build's progress in order to inform users via the `info` endpoint."""
    db.update_deployment_fields(leader_ID, json.loads(request.json))
    return jsonify({"message": "Deployment updated successfully"}), 200

@app.route('/add_results/<int:leader_ID>', methods=['POST'])
def add_results(leader_ID):
    """Endpoint to add results for a specific deployment."""
    data = request.json
    if not data or 'results' not in data:
        return jsonify({"error": "Missing results data"}), 400
    
    db.add_results(leader_ID, data['results'])
    return jsonify({"message": "Results added successfully"}), 200

@app.route('/results/<int:leader_ID>', methods=['GET'])
def get_results(leader_ID):
    """Endpoint to get all results for a specific deployment."""
    results = db.get_results(leader_ID)
    return jsonify({"results": results}), 200


if __name__ == '__main__':
    app.run(debug=True)
