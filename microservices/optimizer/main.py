from utils import optimize

from flask import Flask, jsonify, Response, request

app = Flask(__name__)

@app.route('/partition', methods=['POST'])
def partition_tests() -> tuple[Response, int]:
    """The `partition` endpoint. This endpoint takes in the url and branch 
    associated with the repository along with the number of workers and the 
    test classes to execute and returns a payload representing the testclasses 
    partitioned optimally."""
    data = request.json
    if data is None:
        return jsonify({"error": "missing request body"}), 400
    url: str = data['url']
    branch: str = data['branch']
    num_workers: int = data['num_workers']
    testclasses_dict: list[dict[str,str]] = data['testclasses']
    testclasses = list(map(lambda d: d['name'], testclasses_dict))
    partitions = optimize(url, branch, num_workers, testclasses)
    return jsonify({"partitions": partitions}), 200

@app.route('/update', methods=['POST'])
def update_times() -> tuple[Response, int]:
    """The `update` endpoint. This endpoint takes in the url and branch 
    associated with the repository along with the execution times of each test
    classes of a recent deployment. The optimizer service should add this
    execution time into the database for the given test classes."""
    return jsonify({"error": "unimplemented"}), 500

if __name__ == '__main__':
    app.run(debug=True)
