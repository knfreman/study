#!/bin/bash

if [ $# -lt 3 ] ; then
	echo "This script is used for generating ssh key and upload it to the server."
	echo "Usage: sh $0 [filename] [port] [username] [hostname or IP Address], e.g., $0 id_rsa 22 Patrick 123.123.123.123."
	exit 0
fi

echo "$0: [filename] is $1, [port] is $2 [username] is $3 and [hostname or IP address] is $4."

####################
# Generate SSH Key #
####################
echo "$0: ssh-keygen -P '' -f ~/.ssh/$1"
ssh-keygen -P '' -f ~/.ssh/$1

############################
# Upload SSH Key to Server #
############################
echo "$0: ssh-copy-id -i ~/.ssh/$1.pub -p $2 $3@$4"
ssh-copy-id -i ~/.ssh/$1.pub -p $2 $3@$4

echo "$0: Complete!"