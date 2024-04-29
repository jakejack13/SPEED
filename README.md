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

## System Specification
SPEED as a platform is split into various services. To begin a deployment, which we define as a build and execution of a repository’s tests, a user sends a request to **first**, the first-tier microservice. **first** receives requests for new deployments, keeps track of progress of current deployments, and responds to progress requests from users. The information about current and previous deployments is stored in **firstDB**, the database for first. Upon a new deployment request, **first** spins up a new **leader**, which is the service in charge of a specific deployment. The **leader** is given information about the repository to build and test as well as the number of **workers** to deploy for test execution. The **leader** then contacts **optimizer**, the optimizer service. **optimizer** keeps track of the previous execution times of each grouping of tests (in the case of Java, this would be test classes). This information is stored in **optDB**, the database for **optimizer**. It is then in charge of calculating, given the list of tests to run and their previous execution times, partitions of tests to assign to different machines to run. The **leader** then assigns the partitions from **optimizer** to different **workers**, which are the services that execute the workload. The **workers** build and execute their given partition of tests, sending the result of each test through the **result bus**. The **leader** then receives the results from the **result bus**, sending result information to first and execution time information to **optimizer**. The user can then query **first** periodically to see if the deployment has finished and receive the results.

## Contact
- Jacob Kerr (jck268@cornell.edu, jacob.the.kerr@gmail.com)
- Owen Ralbovsky (oxr2@cornell.edu, oe.aoy1@gmail.com)
- Mitchell Gray (meg346@cornell.edu, mitchellgray100@gmail.com)

## More Info
![Our BOOM 2024 Poster](https://github.com/jakejack13/SPEED/assets/67762738/8018a006-026d-4f2f-bdcc-0704f5527e38)
