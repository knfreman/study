#!/bin/bash

if [ $# -lt 2 ] ; then
	echo "Usage: $0 [port] [file path]."
	exit 0
fi

echo "$0: [port] is $1 and [file path] is $2."

echo "$0: cp -f $2 $2.bak"
cp -f $2 $2.bak

echo "" >> $2
echo "#$0" >> $2
#Change ssh port number
echo "Port $1" >> $2
#Disable ssh for root user 
echo "PermitRootLogin no" >> $2
echo "$0: $2 is modified."

#Install cmd "semanage"
echo "$0: yum -y install policycoreutils-python"
yum -y install policycoreutils-python
echo "$0: \"policycoreutils-python\" is installed."
semanage port -a -t ssh_port_t -p tcp $1
echo "$0: semanage port -l | grep ssh"
semanage port -l | grep ssh
echo "$0: systemctl restart sshd.service"
systemctl restart sshd.service
echo "$0: systemctl status sshd.service"
systemctl status sshd.service
echo "$0: Complete!"
