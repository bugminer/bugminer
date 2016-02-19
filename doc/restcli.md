# How to use the restcli.sh

This is the reference to the current REST-API on the server.
So there is a way to use the REST-interface over CLI.

All options are like

	./restcli command [params...]

The params based on the command.
If you run the script without command you get a usage with all commands instead.

## Commands

### Nodes 

	--getnodes 

Shows you all nodes the server knows in a JSON format. not really human readable but sufficient for work.
You can get the nodeid, and the state(online,offline) of the nodes

---

	--startnode <nodeid>

Start a given node by nodeid. 

---

	--stopnode <nodeid>

Stop a given node by nodeid.

---

	--deletenode <cluster> <nodeid>

Delete a node in a cluster. You must give the cluster e.g. vagrant and the id of the node.

---

	--newnode <cluster>

Create and starts a new Node in the cluster. 
At the moment only works for the vagrant cluster.
There is a waiting time for changes in the database.
After creating the node you have to change the password in the database.
The script also does it, but you must permit these changes.

---

### Tasks

	--gettasks

Show all tasks in a JSON format, ordered by starting-time. 

---

### Projects

	--newproject <name> <giturl> <jiratracker>

Create a new project in bugminer.
There is no task for delete a project, so you don't create many useless projects.

---

	--getprojects

Show all projects in JSON format. 

---

	--getproject <projectname>

Get the project-properties from a specific project.

---

	--syncproject <projectname>

Synchronize the project. 

---

	--mapcommits <project> 

Mapped commits with bugs.

---

	--buildrevision <project> <revision> <node>

Builds a specific commit on a node, run unit tests and create coverage-data.

---

### Clusters

	--getclusters

Show all clusters in JSON format.

---

	--getcluster <cluster>

Shows cluster-properties from a specific cluster.

---

	--addcluster <clustername> <providername>

Add a new Cluster with name and set the provider(e.g. vagrant if it will be a vagrant servercluster).

---

	--renamecluster <oldcluster> <newname>

Renames a cluster.

--- 

	--deletecluster <cluster>

Delete the cluster.

### Bugs

	--getbugs <project>

Get all known bugs from the project.

---

	--getbug <project> <issuetrackername> <key>

Get information about a specific bug.

---

	--getdiffbug <> <issuetrackername> <key>

Get a diff for a specific bug. 

---

### Classifications

	--getclassifications <project> <issuetrackername> <key> 

Get classifications for a given bug.

---

	 --getclassification <project> <issuetrackername> <key> <id> 

Get the classification with a specific ID for a bug.

---

	 --addclassification <project> <issuetrackername> <key> <classification> 
	
Add a new classification for a bug.
 




