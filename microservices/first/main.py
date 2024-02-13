# Endpoint documentation: https://github.com/jakejack13/SPEED/blob/SPEED-26-python-database-library/microservices/first_api_doc.pdf
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
    deployment_id = db.add_deployment(leader_id, data['url'], data['branch'])

    return jsonify({"id": deployment_id}), 201

@app.route('/info/<int:deployment_id>', methods=['GET'])
def get_deployment_info(deployment_id):
    """
    The `info` endpoint. Takes in the id of the SPEED build and returns 
    information about the deployment, including repo_name, repo_branch, and status.
    
    param deployment_id: The ID of the deployment to get.
    """
    deployment = db.get_deployment(deployment_id)
    if deployment:
        return jsonify({
            "id": deployment[0],
            "repo_name": deployment[1],
            "repo_branch": deployment[2],
            "status": deployment[3]
        }), 200
    else:
        return jsonify({"error": "Deployment not found"}), 404

@app.route('/update', methods=['POST'])
def update_deployment():
    """The `update` endpoint. Takes in the id of the SPEED build and the new 
    results of the build. Used by leaders to update the web server on the 
    build's progress in order to inform users via the `info` endpoint."""
    data = request.json
    db.update_deployment(data['id'], data['new_results'])
    return jsonify({"message": "Deployment updated successfully"}), 200

@app.route('/add_results/<int:deployment_id>', methods=['POST'])
def add_deployment(deployment_id):
    """Endpoint to add results for a specific deployment."""
    data = request.json
    if not data or 'results' not in data:
        return jsonify({"error": "Missing results data"}), 400
    
    db.add_results(deployment_id, data['results'])
    return jsonify({"message": "Results added successfully"}), 200

@app.route('/results/<int:deployment_id>', methods=['GET'])
def get_results(deployment_id):
    """Endpoint to get all results for a specific deployment."""
    results = db.get_results_deployment(deployment_id)
    return jsonify({"results": results}), 200


if __name__ == '__main__':
    app.run(debug=True)
