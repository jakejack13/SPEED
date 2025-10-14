"""Optimizer: the optimization microservice. More information can be found
in the documentation at `optimizer_api_doc.md`"""

from flask import Flask, jsonify, Response, request, g

from utils import DBManager, optimize
from utils.partition import PartitionMethod

app = Flask(__name__)


@app.before_request
def before_request() -> None:
    """Adds the database manager to request context"""
    g.db_manager = DBManager()


@app.route("/partition", methods=["POST"])
def partition_tests() -> tuple[Response, int]:
    """The `partition` endpoint. This endpoint takes in the url and branch
    associated with the repository along with the number of workers and the
    test classes to execute and returns a payload representing the testclasses
    partitioned optimally."""
    app.logger.info("partition endpoint invoked")
    data = request.json
    if data is None:
        app.logger.warning("internal endpoint `partition` made bad request")
        return jsonify({"error": "missing necessary json body data"}), 400
    try:
        url: str = data["url"]
        branch: str = data["branch"]
        num_workers: int = data["num_workers"]
        testclasses_dict: list[dict[str, str]] = data["testclasses"]
    except KeyError:
        app.logger.warning("internal endpoint `partition` made bad request")
        return jsonify({"error": "missing necessary json body data"}), 400
    testclasses = list(map(lambda d: d["name"], testclasses_dict))
    partitions = optimize(
        url, branch, num_workers, testclasses, PartitionMethod.EVEN_SPLIT
    )
    return jsonify({"partitions": partitions}), 200


# pylint: disable=too-many-return-statements
@app.route("/update", methods=["POST"])
def update_times() -> tuple[Response, int]:
    """The `update` endpoint. This endpoint takes in the url and branch
    associated with the repository along with the execution times of each test
    classes of a recent deployment. The optimizer service should add this
    execution time into the database for the given test classes."""
    data = request.json
    if data is None:
        return jsonify({"error": "missing request body"}), 400

    url = data.get("url")
    branch = data.get("branch")
    times = data.get("times")

    if not url or not branch or times is None:
        return jsonify({"error": "missing or invalid parameters"}), 400

    try:
        for test_class in times:
            name = test_class.get("name")
            time = test_class.get("time")
            if name is None or time is None:
                return (
                    jsonify({"error": "missing test class name or execution time"}),
                    400,
                )

            app.logger.info(
                "Updated %s in %s on branch %s with time %s", name, url, branch, time
            )

            db_manager: DBManager | None = getattr(g, "db_manager", None)
            if db_manager is not None:
                db_manager.update_execution_time(url, branch, name, time)
            else:
                return jsonify({"Can not connect to optDB"}), 400
        return (
            jsonify({"message": "Test class execution times updated successfully"}),
            200,
        )
    except KeyError:
        return jsonify({"error": "Key error - missing data in request"}), 400
    except ValueError:
        return jsonify({"error": "Value error - invalid data type"}), 400


if __name__ == "__main__":
    app.run(port=5002)
