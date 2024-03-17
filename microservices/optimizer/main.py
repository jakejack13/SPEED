"""Optimizer: the optimization microservice. More information can be found
in the documentation at `optimizer_api_doc.md`"""

from flask import Flask, jsonify, Response, request

from utils import optimize

app = Flask(__name__)


@app.route("/partition", methods=["POST"])
def partition_tests() -> tuple[Response, int]:
    """The `partition` endpoint. This endpoint takes in the url and branch
    associated with the repository along with the number of workers and the
    test classes to execute and returns a payload representing the testclasses
    partitioned optimally."""
    data = request.json
    if data is None:
        return jsonify({"error": "missing request body"}), 400
    url: str = data["url"]
    branch: str = data["branch"]
    num_workers: int = data["num_workers"]
    testclasses_dict: list[dict[str, str]] = data["testclasses"]
    testclasses = list(map(lambda d: d["name"], testclasses_dict))
    partitions = optimize(url, branch, num_workers, testclasses)
    return jsonify({"partitions": partitions}), 200


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
                return jsonify({"error": "missing test class name or execution time"}), 400
            
            #Update database
            
            print(f"Updated {name} in {url} on branch {branch} with time {time}")
        return jsonify({"message": "Test class execution times updated successfully"}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    app.run(debug=True)
