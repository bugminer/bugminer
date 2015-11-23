bugminer
========

A framework for collecting test data about software projects

How to Build
------------

Run `mvn install`

Development Environment
-----------------------

We suggest using IntelliJ IDEA for development. Node.js is required
to build the front end. Additionally, a data base has to be set up.

### Prerequisites
1. Download and install Node.js
2. Install MySQL (e.g. with XAMPP)
3. Run `npm install -g grunt-cli`

### Set up
1. Import the project in IDEA
2. Create a database called `bugminer` and a user called `bugminer` with the passsword
   `bugminer` (these values are defined in `AppConfig`)
3. Open a console in `bugminer-server/src/main/frontend` and run `grunt` in this directory

### Development

Run/Debug the `Application` class.

Run `grunt` every time you change front end files and afterwads recompile the
project in IDEA so that the files are copied to the `target` directory.
