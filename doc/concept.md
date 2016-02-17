# Bugminer Concept

The concept of bugminer is as follows:

## Overview
### Server
- has all the information about the project
- collects the data
- manages the nodes

### Nodes
- pulls the projects
- builds the projects
- generates coverage data

## Server
The server is the mode the user works on. It provides the frontend and the
backend and is mainly managing bugminer.

It controls the Nodes, by creating, starting, stopping and deleting them.
The server also provides all the informations the nodes need to perform the computing.

## Nodes
The nodes are vagrant machines with a minimal environment. They are supposed to
run without interference from the user so optimally the user doesn't even know
that they're running in the background.

They are managed via ssh by the server.
