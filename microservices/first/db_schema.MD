# Deployment Table Schema:
id: Integer, Primary key identifying each row
leader_ID: Integer, Indentifier of deployment
repo_name: TEXT, Name of repo the deployment is working on
repo_branch: TEXT, Branch of the repo being worked on
status: TEXT, (DeploymentStatus in text form) Status of the deployment.

# Results Table Schema:
id: Integer, Primary key identifying each row
leader_ID: Integer, Foreign Key pointing to Deployment Table "leader_id". Deployment the result is associated with.
result: TEXT, one test result object.