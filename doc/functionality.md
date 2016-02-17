# Frontend

## Projects
Here is the list of projects that have been added to the bugminer database.
Each of these projects can have it's bugs stored in the database and has the
options to be built against a git commit in the repository.

## Tasks
This is a list of all running, failed and completed tasks including output and
error messages.

## Clusters
All the Clusters and the corresponding nodes are listed here. There is also the
option to start/stop/delete the nodes.

### Bugs
The option to delete a node only deletes the node from the database, but all the
data remains.
The new node button does nothing.



# Backend
The backend is the bugminer-server which manages the nodes and provides a
REST-API

## Node management
Bugminer has the possibility to create new nodes and configure them. The
configuration is stored in the bugminer database and contains:
- the port of the ssh-daemon
- the username
- the password
- the machine provider
- the host-ip
- operating system information
- machine information

When nodes are deleted, they are removed from the database but their files stay
on the host.

## REST API
Bugminer can be managed via the REST-API. You can find further instructions in
the restapi.md file.
