from flask import Flask, request, jsonify
from db_manager import DBManager
import json

DATABASE_FILE: str = 'deployments.db'
app = Flask(__name__)
db = DBManager(DATABASE_FILE)

@app.before_first_request
def initialize():
    """Initialize the database and tables."""
    db.create_deployments_table()

@app.route('/start', methods=['POST'])
def start_deployment():
    """The `start` endpoint. Takes in the url and branch of the repository to 
    build and execute. Spawns a new leader in the form of a Docker container 
    and execute the SPEED deployment. Returns the id of the newly created 
    worker."""
    data = request.json
    deployment_id = db.add_deployment(data['url'], data['branch'])

    # worker_id = "0" #TODO: Replace

    return jsonify({"id": deployment_id}), 201

@app.route('/info/<int:deployment_id>', methods=['GET'])
def get_deployment_info(deployment_id):
    """
    The `info` endpoint. Takes in the id of the SPEED build and returns 
    information about the deployment, including progress and results returned 
    so far.
    
    param deployment_id: The ID of the deployment to get.
    """
    deployment = db.get_deployment(deployment_id)
    if deployment:
        return jsonify({
            "id": deployment[0],
            "results": json.loads(deployment[3])
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

if __name__ == '__main__':
    app.run(debug=True)
