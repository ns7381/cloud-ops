# Introduction

Cloud-Ops means application operation for cloud.It's a project started by Nathan in order to rise iop.It adopts TOSCA(Topology and Orchestration Specification for Cloud Applications)
to describe application structure, and It support Openstack and Docker

## Concepts

Let us introduce you to concepts in Cloud-Ops.It leverages the following concept:

* __Environment__: A set of application
* __Location__: Deployment target (cloud or set of local physical machines)
* __Applications__: Actual applications to deploy with _environments_ and _versions_ each of them being associated with a topology.
* __Topologies__: Description of multiple software components assembled together (to build an application).
* __Components__: Software components to deploy
* __TOSCA__: An emerging standard to describe service components and their relationships
