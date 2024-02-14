```markdown
# SPEED API Documentation


This documentation outlines the API endpoints for the SPEED deployment system. The SPEED system is designed to build and execute code from repositories using Docker containers. Below are the available endpoints for interacting with the SPEED system.


## API Endpoints


### POST /start


Starts a new deployment by building and executing code from the specified repository URL and branch. This endpoint spawns a new Docker container as a leader to execute the SPEED deployment.


#### Request


- **URL**: The URL of the repository to deploy.
- **Branch**: The branch of the repository to build and execute.


#### Response


- **201 Created**: Returns the ID of the newly created worker.


```json
{
  "id": "leader_id"
}
```


### GET /info/{leader_ID}


Retrieves information about a specific deployment using its ID.


#### Parameters


- **leader_ID** (path): The ID of the deployment to retrieve information for.


#### Response


- **200 OK**: Returns information about the deployment.


```json
{
  "id": "deployment_id",
  "leader_ID": "leader_id",
  "repo_name": "repository_name",
  "repo_branch": "repository_branch",
  "status": "deployment_status"
}
```


- **404 Not Found**: If the deployment with the specified ID does not exist.


```json
{
  "error": "Deployment not found"
}
```


### POST /update


Updates the status of a deployment. This endpoint is typically used by leaders to inform the web server of the build's progress.


#### Request


- **JSON Payload**: Contains the updated fields for the deployment.


#### Response


- **200 OK**: Indicates the deployment was updated successfully.


```json
{
  "message": "Deployment updated successfully"
}
```


### POST /add_results/{leader_ID}


Adds results for a specific deployment.


#### Parameters


- **leader_ID** (path): The ID of the deployment to add results for.


#### Request


- **JSON Payload**: Contains the results data to be added.


#### Response


- **200 OK**: Indicates the results were added successfully.


```json
{
  "message": "Results added successfully"
}
```


- **400 Bad Request**: If the results data is missing from the request.


```json
{
  "error": "Missing results data"
}
```


### GET /results/{leader_ID}


Retrieves all results for a specific deployment.


#### Parameters


- **leader_ID** (path): The ID of the deployment to retrieve results for.


#### Response


- **200 OK**: Returns all results for the specified deployment.


```json
{
  "results": "deployment_results"
}
```


## Initialization


On the first request to the API, the database and tables are initialized to store deployment and results data.


## Database


The API uses a SQLite database (`deployments.db`) to store information about deployments and their results. The `DBManager` class is utilized to interact with the database.


## Deployment


Deployments are managed through Docker containers, with each deployment spawning a new container to execute the build and run tasks.


For more information or to report issues, please visit the [GitHub repository](https://github.com/jakejack13/SPEED).
```


This markdown documentation provides a comprehensive guide to the API endpoints, including request and response formats, for the SPEED deployment system.