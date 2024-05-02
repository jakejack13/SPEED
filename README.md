# SPEED: Scalable Platform for Efficient Execution of Distributed Testing
![Speed Logo](https://github.com/jakejack13/SPEED/assets/67762738/e29e7da0-4ef4-468c-a0c0-acb479460da5)

## License
This repository is made public for educational and research visibility. Any use of the software, including copying, modifying, merging, or distributing, is prohibited unless expressly permitted by the license found in the [LICENSE.md](./LICENSE.md) file.

## About
SPEED is the Scalable Platform for Efficient Execution of Distributed Testing. It is a distributed test execution system and platform designed to speed up the execution of unit testing in very large projects. It uses a dynamic partitioning system to split different groups of unit tests onto different machines in a given cluster, resulting in parallel execution. 

Many test execution systems automatically parallelize execution onto different threads. However, the number of threads that can be run concurrently on a system is bound by the number of cores. Virtual machines that regression testing is done on in industry are usually assigned few cores with small specifications. However, the number of VMs is usually quite large. We propose that, instead of using a threading system, test execution is done on a distributed system, where several computers, more than the number of cores of one computer, each run a set of tests from the project it is testing.

## Installation and Usage
SPEED can be run either as a Docker Compose application on a single machine or as a Kubernetes application on a cluster. 
### Docker Compose
Simply run 
```sh
docker compose up
```
from the root directory of the SPEED project. This will bring up all of the services as separate Docker containers. You can then access **first** at `localhost:5001`.
### Kubernetes
Run 
```sh
cd kubernetes && ./apply.sh
```
from the root directory of the SPEED project. This will bring up all of the services as separate Kubernetes pods. You can then access **first** at port `5001` from the IP address of the machine in the cluser running **first**. Please query the Kubernetes API via `kubectl` to find out the IP address. 

## Limitations
Currently, we only support Java projects with JUnit 5, but we hope to expand these offerings in the future.

## System Specification
### Components
- **first**: the first-tier microservice. This is the main component that the client will query to begin deployments on SPEED and request information about current depoyments. **first** records all progress made by deployments, starts new deployments, and is the client interface to SPEED.
- **firstDB**: the database for **first**. It stores all intermediate information about deployment progress to serve clients via the **first** API.
- **optimizer**: the optimizer service. This component generates partitions of tests to run based on previous execution times and number of **workers** to run. It also responds to new execution time logs of previously run tests.
- **optDB**: the database for **optimizer**. It stores previous execution times of all tests ran.
- **leaders**: the leader of deployments. It determines the number of tests to run, queries **optimizer** for partition information, creates new **workers** to serve these partitions, and notifies **first** of the progress of deployments. One **leader** is created per deployment by **first**.
- **workers**: the executors of tests. Each **worker** builds the project to run and executes a given subset of tests, called partitions. It then notifies the **leader** of the results of these tests via the **result bus**.
- **result bus**: the message bus through which **workers** notify their **leaders** of testing results. This allows for asynchronous communication of test results back to **leaders** and, later, **first**.
### Architecture
SPEED as a platform is split into various services. To begin a deployment, which we define as a build and execution of a repository’s tests, a user sends a request to **first**, the first-tier microservice. **first** receives requests for new deployments, keeps track of progress of current deployments, and responds to progress requests from users. The information about current and previous deployments is stored in **firstDB**, the database for first. Upon a new deployment request, **first** spins up a new **leader**, which is the service in charge of a specific deployment. The **leader** is given information about the repository to build and test as well as the number of **workers** to deploy for test execution. The **leader** then contacts **optimizer**, the optimizer service. **optimizer** keeps track of the previous execution times of each grouping of tests (in the case of Java, this would be test classes). This information is stored in **optDB**, the database for **optimizer**. It is then in charge of calculating, given the list of tests to run and their previous execution times, partitions of tests to assign to different machines to run. The **leader** then assigns the partitions from **optimizer** to different **workers**, which are the services that execute the workload. The **workers** build and execute their given partition of tests, sending the result of each test through the **result bus**. The **leader** then receives the results from the **result bus**, sending result information to first and execution time information to **optimizer**. The user can then query **first** periodically to see if the deployment has finished and receive the results.
### Implementation
Both the **first** and **optimizer** microservices are implemented as Flask applications containerized via Docker images. **firstDB** is implemented as a PostgreSQL database and runs in its own container separate from first; while, **optDB** is implemented as a MongoDB database and also runs in its own container separate from optimizer. MongoDB utilizes NoSQL which allows us to have higher availability and scalability. The **leaders** and **workers** are implemented as containerized Java applications that run to completion. The result bus is implemented using Apache Kafka. This allows **workers** and **leaders** to communicate with each other without needing direct communication and knowing IP addresses. Our entire system is currently deployed and ran via Docker Compose for simplicity. When a user requests a new deployment, **first** connects to the Docker socket to start a new **leader** container. The **leader** will then similarly communicate with the Docker socket when spinning up new **worker** instances. 

For more information on the implementation of each component of the SPEED system, please see documentation found in each subdirectory.

## Contact
- Jacob Kerr (jck268@cornell.edu, jacob.the.kerr@gmail.com)
- Owen Ralbovsky (oxr2@cornell.edu, oe.aoy1@gmail.com)
- Mitchell Gray (meg346@cornell.edu, mitchellgray100@gmail.com)

## More Info
![Our BOOM 2024 Poster](https://github.com/jakejack13/SPEED/assets/67762738/8018a006-026d-4f2f-bdcc-0704f5527e38)
