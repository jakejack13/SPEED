"""Optimizer: the optimization microservice. More information can be found
in the documentation at `optimizer_api_doc.md`"""

from flask import Flask, jsonify, Response, request

from utils import optimize
from utils.partition import ParitionMethod

app = Flask(__name__)


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
    partitions = optimize(url, branch, num_workers,
                          testclasses, ParitionMethod.EVEN_SPLIT)
    print(partitions)
    return jsonify({"partitions": partitions}), 200


@app.route("/update", methods=["POST"])
def update_times() -> tuple[Response, int]:
    """The `update` endpoint. This endpoint takes in the url and branch
    associated with the repository along with the execution times of each test
    classes of a recent deployment. The optimizer service should add this
    execution time into the database for the given test classes."""
    app.logger.info("update endpoint invoked")
    return jsonify({"error": "unimplemented"}), 501


if __name__ == "__main__":
    app.run(debug=True, port=5002)
