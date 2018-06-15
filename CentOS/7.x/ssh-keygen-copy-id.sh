#!/bin/bash

if [ $# -eq 0 ] ; then
	echo "This script is used for generating ssh key and upload it to the server."
	echo "Usage: sh $0 -f [filename] -p [port] -u [username] -h [hostname or IP Address]." 
	echo "e.g., sh $0 -f id_rsa -p 33333 -u Patrick -h 123.123.123.123"
	exit 0
fi

while getopts "f:p:u:h:" opt  
do  
    case $opt in  
        f)  
        filename=$OPTARG
        ;;  
        p)  
        port=$OPTARG
        ;;  
        u)  
        username=$OPTARG
        ;;  
        h)  
        hostname=$OPTARG
        ;;  
        ?)   
        ;;  
    esac  
done  

echo "[hostname] is $hostname"

if [ ! $hostname ] ; then
	echo "[hostname] is unspecified!"
	exit 0
fi

if [ ! $username ] ; then
	username=$(whoami)
fi

if [ ! $filename ] ; then
	filename=$hostname
fi

if [ ! $port ] ; then
	port=22
fi

echo "$0: filename=$filename"
echo "$0: port=$port"
echo "$0: username=$username"
echo "$0: hostname=$hostname"

####################
# Generate SSH Key #
####################
echo "$0: ssh-keygen -P '' -f ~/.ssh/$filename"
ssh-keygen -P '' -f ~/.ssh/$filename

############################
# Upload SSH Key to Server #
############################
echo "$0: ssh-copy-id -i ~/.ssh/$filename.pub -p $port $username@$hostname"
ssh-copy-id -i ~/.ssh/$filename.pub -p $port $username@$hostname

echo "$0: Complete!"