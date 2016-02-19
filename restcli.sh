#!/bin/sh

serveradress="http://localhost:8181/api"

usage=' 
	Usage:\n
		--help -h	\t\t\t: display more help\n
		--getnodes	\t\t\t:	shows the node-parameters\n
		--startnode	<nodeid> \t\t:	bring a given node online \n
		--stopnode <nodeid>	 \t\t:	bring a online given node offline \n
		--deletenode <cluster> <nodeid>: delete a given cluster -  node\n
		--newnode <cluster>	\t\t: create a new Node \n\n
		
		--gettasks	\t\t\t:	shows all tasks since server-start\n\n
		
		--newproject <name> <giturl> <jiratracker> \t:  \n \t create a new project with given name, giturl, jiratracker\n
		--getprojects\t\t\t: shows all projects \n 
		--getproject <projekt> \t: show a single project\n
		--syncproject <project> \t: synchronize a project\n
		--mapcommits <project> \t: show map-commits \n
		--buildrevision <project> <revision> <node>\t : \n \t build a code-revision of a project on a specific node\n\n
		
		--getclusters \t\t\t: get all clusters\n
		--getcluster <cluster> \t: get data from a specific cluster \n
		--addcluster <clustername> <providername> :\n\t adds a new cluster \n
		--renamecluster <oldcluster> <newname>\t:\n\t rename a cluster \n
		--deletecluster <cluster> \t: delete a cluster\n\n
		
		--getbugs <project> \t\t: get bug from a project\n
		--getbug <project> <issuetrackername> <key> :\n\t get a specific bug\n
		--getdiffbug <project> <issuetrackername> <key> :\n\t get a diff for the specific bug\n\n
		
		--getclassifications <project> <issuetrackername> <key> :\n\t get classifications for a given Bug\n
 		--getclassification <project> <issuetrackername> <key> <id> :\n\t get the classification with a specific ID for a bug\n
		--addclassification <project> <issuetrackername> <key> <classification> :*\n\t  add a new classification for a bug\n
		\n
		\* not ready now... working
		\n
'
helptext=' 
	Usage:\n
		--getnodes	:\n\t	
			shows the node-parameters , in a xml-format. not really human-readable\n\t
			esspecially by tons of DATA\n
		--startnode	<nodeid> :\n\t	
			bring a given node online - required his node-id. you can get this id\n\t
			with the --getnodes command. zhe ids are complex \n
		--stopnode <nodeid>	:\n\t	
			bring a online given node offline - same by startnode \n
		--deletenode <cluster> <nodeid>:\n\t
			delete a given node - requires the node-id  and deletes the node from the Database.\n\t
			you could clean the node with your own hands - and mysql-skills but it is not so\n\t
			clean. better you use this command and Delete the VM with virtualbox\n
		--newnode <cluster>	: create a new Node in a specific cluster-environment. \n\t
			at the moment they are 1 possible Cluster-option: vagrant, but in the Future maybe\n\t
			docker-container or other stuff will follow. \n\t
			note: the curl-command needs a requestbody, also the -d flag with an xml-objekt\n\n
		
		--gettasks :	shows all tasks since server-start. you can see ALL \n\t
			tasks they are tasked from the beginning of the Server-start. That can be the\n\t
			Overkill but luckly the last and important task will be at the end of this \n\t
			non-humanreadable xml stuff\n\n
		
		--newproject <name> <giturl> <jiratracker> :\n\t 
			create a new project with given name, giturl, jiratracker  - note on the curl-command\n\t
			here is a xml-objekt in use. \n
		--getprojects:\n\t
			shows all projects usefull to get Names, build-strategys from your projects\n 
		--getproject <projekt> :\n\t
			show a single project for more informations of a Single specific project. \n
		--syncproject <project> :\n\t
			synchronize a project first step to get bugs into the Database.\n\t
			By great projects can works awhile\n
		--mapcommits <project> \t: show map-commits \n
		--buildrevision <project> <revision> <node>\t : \n \t build a code-revision of a project on a specific node\n\n
		
		--getclusters \t\t\t: get all clusters\n
		--getcluster <cluster> \t: get data from a specific cluster \n
		--addcluster <clustername> <providername> :\n\t adds a new cluster \n
		--renamecluster <oldcluster> <newname>\t:\n\t rename a cluster \n
		--deletecluster <cluster> \t: delete a cluster\n\n
		
		--getbugs <project> \t\t: get bug from a project\n
		--getbug <project> <issuetrackername> <key> :\n\t get a specific bug\n
		--getdiffbug <project> <issuetrackername> <key> :\n\t get a diff for the specific bug\n\n
		
		--getclassifications <project> <issuetrackername> <key> :\n\t get classifications for a given Bug\n
 		--getclassification <project> <issuetrackername> <key> <id> :\n\t get the classification with a specific ID for a bug\n
		--addclassification <project> <issuetrackername> <key> <classification> :*\n\t  add a new classification for a bug\n
'
if [ $# -lt 1 ] 
then
	echo $usage
	exit 0
fi

case $1 in
	'--getnodes') curl -v -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/vagrant/nodes
		;;
	'--startnode') 
		if [ $# -lt 2 ]
		then
			echo 'expect parameter : <nodeid>\n'
			exit 0
		fi
		nodeid=$2
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/vagrant/nodes/$nodeid/start
		;;
	'--deletenode') 
		if [ $# -lt 3 ]
		then
			echo 'expect parameter : <cluster> <nodeid>\n'
			exit 0
		fi
		curl -v -X DELETE -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/$2/nodes/$3
		;;
	'--stopnode') 
		if [ $# -lt 2 ]
		then
			echo 'expect parameter : <nodeid>\n'
			exit 0
		fi
		nodeid=$2
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/vagrant/nodes/$nodeid/stop
		;;
	'--gettasks') curl -v -H "Content-Type: application/json;charset=UTF-8"  $serveradress/tasks 
		;;
	'--newproject')
		if [ $# -lt 2 ]
		then
			echo 'expect parameter : <projectname> <giturl> <jiraurl>\nminimal\trestcli.sh --newproject NeuesProjekt\nbetter\trestcli.sh --newproject NeuesProjekt <gitURL> <jira>\n'
			exit 0
		fi
		jsono="{\"projectName\":\""${2}"\",\"git\":\""${3}"\",\"jira\":\""${4}"\"}" # build a projectName-json object 
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8" $serveradress/projects -d $jsono
		echo "Wait 5 seconds to end the task by the server - not done"
		sleep 5
		echo "create sql-file"
		filename="${2}.sql"
		echo "update project set build_provider = 'maven' where name = '${2}';"
		echo "update project set build_provider = 'maven' where name = '${2}';" > $filename
		echo "exec mysql -u bugminer -p bugminer < ${filename}"
		mysql -u bugminer -p bugminer < ${filename}
		echo "rm ${filename}"
		rm $filename
		echo "done"
		;;
	'--getprojects') 
		curl -v -H "Content-Type: application/json;charset=UTF-8" $serveradress/projects
		;;
	'--getproject') 
		if [ $# -lt 2 ]
		then
			echo 'expect parameter : <projectname>'
			exit 0
		fi
		curl -v -H "Content-Type: application/json;charset=UTF-8" $serveradress/projects/$2
		;;
	'--newnode')
		if [ $# -lt 2 ]
		then
			echo 'expect parameter :<cluster> '
			echo 'possible clusters: "vagrant" or "manual"'
			exit 0
		fi
		nodeparams="{ \"sshConfig\":{\"host\":\"localhost\",\"port\":\"2222\",\"user\":\"vagrant\",\"password\":\"vagrant\",\"keyfile\":null,\"verifyHostKey\":false}}" #currently not in use
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/$2/nodes -d '{ "sshConfig":{"host":"localhost","port":"2222","user":"vagrant","password":"vagrant","keyfile":null,"verifyHostKey":false}}'
		echo "create sql-file"
		filename="${2}.sql"
		echo "update node set password = 'vagrant';" > $filename
		echo "Wait 1 minute to end the task by the server - not done and not end this\nif you end the script before they ready you must manualy set up the file"
		sleep 60
		echo "exec mysql -u bugminer -p bugminer < ${filename}"
		mysql -u bugminer -p bugminer < ${filename}
		echo "rm ${filename}"
		rm $filename
		echo "done"	
		;;
	'--syncproject') 
		if [ $# -lt 2 ]
		then
			echo 'expect parameter :<project> note: the real name of the project by example: bugminer '
			exit 0
		fi
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8"  $serveradress/projects/$2/synchronize 
		;;
	'--mapcommits')
		if [ $# -lt 2 ]
		then
			echo 'expect parameter :<project> note: the real name of the project by example: bugminer '
			exit 0
		fi
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8"  $serveradress/projects/$2/map-commits  
		;;
	'--buildrevision')
		if [ $# -lt 4 ]
		then
			echo 'expect parameter :<project> <revision>'
			echo 'revision is the commit'
			exit 0
		fi
		node=$4
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8"  $serveradress/projects/$2/build/$3?node=$node 
		;;
	'--getclusters')
		curl -v -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters 
		;;
	'--getcluster')
		if [ $# -lt 2 ]
		then
			echo 'expect parameter :<cluster>'
			exit 0
		fi
		curl -v -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/$2 
		;;
	'--addcluster')
		if [ $# -lt 3 ]
		then
			echo 'expect parameter :<name> <providername>'
			exit 0
		fi
		curl -v -X POST -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters?name=$2\&vagrant=$3 		
		;;
	'--renamecluster')
		if [ $# -lt 3 ]
		then
			echo 'expect parameter :<oldcluster> <newname> '
			exit 0
		fi
		curl -v -X PUT -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/$2?name=$3 		

		;;
	'--deletecluster')
		if [ $# -lt 2 ]
		then
			echo 'expect parameter :<cluster> '
			exit 0
		fi
		curl -v -X DELETE -H "Content-Type: application/json;charset=UTF-8"  $serveradress/clusters/$2 		
		;;
	'--getbugs')
		if [ $# -lt 2 ]
		then
			echo 'expect parameter :<project> '
			exit 0
		fi
		curl -v -H "Content-Type: application/json;charset=UTF-8"  $serveradress/projects/$2/bugs 		
		;;
	'--getbug')
		if [ $# -lt 4 ]
		then
			echo 'expect parameter :<project> <issuetrackername> <key>'
			echo 'the issuetackername is the "name" of the tracker from the projekt. per default y can try "main" \n f you choose another name for your project you must show --getbugs and find out the name for your choosen bug.'
			exit 0
		fi
		curl -v  -H "Content-Type: application/json;charset=UTF-8"  $serveradress//projects/$2/bugs/$3/$4 		
		;;
	'--getdiffbug')
		if [ $# -lt 4 ]
		then
			echo 'expect parameter :<project> <issuetrackername> <key>'
			echo 'the issuetackername is the "name" of the tracker from the projekt. per default y can try "main" \n if you choose another name for your project you must show --getbugs and find out the name for your choosen bug.'
			exit 0
		fi
		curl -v  -H "Content-Type: application/json;charset=UTF-8"  $serveradress//projects/$2/bugs/$3/$4/diff 		
		;;
	'--getclassifications')
		if [ $# -lt 4 ]
		then
			echo 'expect parameter :<project> <issuetrackername> <key>'
			echo 'the issuetackername is the "name" of the tracker from the projekt. per default y can try "main"\n  if you choose another name for your project you must show --getbugs and find out the name for your choosen bug.'
			exit 0
		fi
		curl -v  -H "Content-Type: application/json;charset=UTF-8"  $serveradress//projects/$2/bugs/$3/$4/classifications 		
		;;
	'--getclassification')
		if [ $# -lt 5 ]
		then
			echo 'expect parameter :<project> <issuetrackername> <key> <id>'
			echo 'the issuetackername is the "name" of the tracker from the projekt. per default y can try "main" \n if you choose another name for your project you must show --getbugs and find out the name for your choosen bug.'
			exit 0
		fi
		curl -v  -H "Content-Type: application/json;charset=UTF-8"  $serveradress//projects/$2/bugs/$3/$4/classifications/$5 		
		;;
	'--addclassification')
		if [ $# -lt 7 ]
		then
			echo 'expect parameter :<project> <issuetrackername> <key> <user> <Bug> <jsonlineclassifications>'
			echo 'the issuetackername is the "name" of the tracker from the projekt. per default y can try "main" \n if you choose another name for your project you must show --getbugs and find out the name for your choosen bug.'
			exit 0
		fi
		requestbody= "{\"user\":\""${5}"\",\"bug\":\""${6}"\",\"lineChangeClassifications\":"${7}"\"}"
		curl -v -X PUT -H "Content-Type: application/json;charset=UTF-8"  $serveradress//projects/$2/bugs/$3/$4/classifications?user=$5 		
		;;
	'')
		;;
	'')
		;;
	'')
		;;
	'')
		;;
	'')
		;;
	'')
		;;
	'--help'| '-h') echo $helptext ;; 
esac
exit 0
