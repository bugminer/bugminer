# FAQ

## I'm told that there is no node in my path
If there is a message, that there is no programm called node, you have to create
a symlink to nodejs called node. (E.g. 'ln -s /usr/bin/nodejs
/usr/local/bin/node'

## bugminer is unable to connect to the vagrant node
 0. find informations on the nodes
	'use bugminer;'
	'select port from node;'

	important are <id> and <port>

1. add to .bugminer/vagrant/vagrant/<id>/Vagrantfile
	config.vm.network "forwarded_port", guest: 22, host: <port>

2. edit mysql database:
	'use bugminer;'
	'update node set password = 'vagrant' where id = <id>;'

