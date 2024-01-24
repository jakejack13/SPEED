import json

from flask import Flask, request

import utils

app = Flask(__name__)

@app.route('/start', methods=['POST'])
def start():
    """The `start` endpoint. Takes in the url and branch of the repository to 
    build and execute. Spawns a new leader in the form of a Docker container 
    and execute the SPEED deployment. Returns the id of the newly created 
    worker."""
    url = request.form['url']
    branch = request.form['branch']

    worker_id = "0" # TODO: Replace

    return worker_id, 200

@app.route('/info', methods=['GET'])
def info():
    """The `info` endpoint. Takes in the id of the SPEED build and returns 
    information about the deployment, including progress and results returned 
    so far."""
    worker_id = request.args.get('id', '')

    results = json.dumps( # TODO: Replace
        {
            'id': 1, 
            'results': [
                {
                    "name": "org.example.CalcTest$testOne()",
                    "result": "SUCCESS"
                }
            ]
        }
    )

    return results, 200

@app.route('/update', methods=['POST'])
def update():
    """The `update` endpoint. Takes in the id of the SPEED build and the new 
    results of the build. Used by leaders to update the web server on the 
    build's progress in order to inform users via the `info` endpoint."""
    worker_id = request.form['id']
    results = json.loads(request.form['results'])

    # TODO: Replace

    return "OK", 200
