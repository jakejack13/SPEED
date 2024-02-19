# Deployment Table Schema:
id: Integer, Primary key identifying each row<br>
leader_ID: TEXT, Indentifier of deployment<br>
repo_name: TEXT, Name of repo the deployment is working on<br>
repo_branch: TEXT, Branch of the repo being worked on<br>
status: TEXT, (DeploymentStatus in text form) Status of the deployment.<br>

# Results Table Schema:
id: Integer, Primary key identifying each row<br>
deployment_ID: Integer, Foreign Key pointing to Deployment Table "id". Deployment the result is associated with.<br>
result: TEXT, one test result object.<br>