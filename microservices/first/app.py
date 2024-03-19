"""First: the first-tier microservice to SPEED. More information can be found
in the documentation at `first_api_doc.md`"""

from flask import Flask, request, jsonify, Response, g

from psycopg import OperationalError

from utils import DBManager
import utils

app = Flask(__name__)

# Set up database manager
DATABASE_FILE: str = "deployments.db"


def initialize() -> None:
    """Initialize the database and tables."""
    try:
        with app.app_context():
            db = DBManager(DATABASE_FILE)
            db.create_deployments_table()
            db.create_results_table()
            db.close_connection()
            app.logger.info("Database initialized")
    except OperationalError:
        initialize()


@app.before_request
def before_request() -> None:
    """Adds the database manager to request context"""
    g.db_manager = DBManager(DATABASE_FILE)


@app.teardown_request
def teardown_request(_: BaseException | None = None) -> None:
    """Closes the connection to the database at the end of the request"""
    db_manager = getattr(g, "db_manager", None)
    if db_manager is not None:
        db_manager.close_connection()


@app.route("/start", methods=["POST"])
def start_deployment() -> tuple[Response, int]:
    """The `start` endpoint. Takes in the url and branch of the repository to
    build and execute. Spawns a new leader in the form of a Docker container
    and execute the SPEED deployment. Returns the id of the newly created
    worker."""
    app.logger.info("start endpoint invoked")
    data = request.json
    if data is None:
        return jsonify({"error": "missing necessary json body data"}), 400
    try:
        url: str = data["url"]
        branch: str = data["branch"]
    except KeyError:
        return jsonify({"error": "missing necessary json body data"}), 400
    db_manager: DBManager | None = getattr(g, "db_manager", None)
    if db_manager is None:
        app.logger.error("unable to get DBManager")
        return jsonify({"error": "no database manager"}), 500
    deployment_id = db_manager.add_deployment(url, branch)
    leader_id = utils.run_docker_container(
        url, branch, 2, "ghcr.io/jakejack13/speed-leaders:latest", deployment_id
    )
    db_manager.add_leader_id(leader_id, deployment_id)
    return jsonify({"id": deployment_id}), 201


@app.route("/info/<int:deployment_id>", methods=["GET"])
def get_deployment_info(deployment_id: int) -> tuple[Response, int]:
    """
    The `info` endpoint. Takes in the id of the SPEED build and returns
    information about the deployment, including repo_name, repo_branch, and status.

    param deployment_ID: The ID of the deployment to get.
    """
    app.logger.info("info endpoint invoked")
    db_manager: DBManager | None = getattr(g, "db_manager", None)
    if db_manager is None:
        app.logger.error("unable to get DBManager")
        return jsonify({"error": "no database manager"}), 500
    deployment = db_manager.get_deployment(deployment_id)
    if deployment:
        return (
            jsonify(
                {
                    "id": deployment["id"],
                    "leader_ID": deployment["leader_ID"],
                    "repo_name": deployment["repo_name"],
                    "repo_branch": deployment["repo_branch"],
                    "status": deployment["status"],
                }
            ),
            200,
        )
    return jsonify({"error": "Deployment not found"}), 404


@app.route("/update/<int:deployment_id>", methods=["POST"])
def update_deployment(deployment_id: int) -> tuple[Response, int]:
    """The `update` endpoint. Takes in the id of the SPEED build and the new
    results of the build. Used by leaders to update the web server on the
    build's progress in order to inform users via the `info` endpoint."""
    app.logger.info("update endpoint invoked")
    data = request.json
    if not data:
        app.logger.warning("internal endpoint `update` made bad request")
        return jsonify({"error": "Missing update data"}), 400
    db_manager: DBManager | None = getattr(g, "db_manager", None)
    if db_manager is None:
        app.logger.error("unable to get DBManager")
        return jsonify({"error": "no database manager"}), 500
    db_manager.update_deployment_fields(deployment_id, data)
    return jsonify({"message": "Deployment updated successfully"}), 200


@app.route("/add_results/<int:deployment_id>", methods=["POST"])
def add_results(deployment_id: int) -> tuple[Response, int]:
    """Endpoint to add results for a specific deployment."""
    app.logger.info("add_results endpoint invoked")
    data = request.json
    if not data or "results" not in data:
        app.logger.warning("internal endpoint `add_results` made bad request")
        return jsonify({"error": "Missing results data"}), 400
    db_manager: DBManager | None = getattr(g, "db_manager", None)
    if db_manager is None:
        app.logger.error("unable to get DBManager")
        return jsonify({"error": "no database manager"}), 500
    db_manager.add_results(deployment_id, data["results"])
    return jsonify({"message": "Results added successfully"}), 200


@app.route("/results/<int:deployment_id>", methods=["GET"])
def get_results(deployment_id: int) -> tuple[Response, int]:
    """Endpoint to get all results for a specific deployment."""
    app.logger.info("results endpoint invoked")
    db_manager: DBManager | None = getattr(g, "db_manager", None)
    if db_manager is None:
        app.logger.error("unable to get DBManager")
        return jsonify({"error": "no database manager"}), 500
    results = db_manager.get_results(deployment_id)
    return jsonify({"results": results}), 200


initialize()
if __name__ == "__main__":
    app.run(debug=True, port=5001)
