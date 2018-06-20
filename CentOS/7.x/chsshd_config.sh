#!/bin/bash

if [ $# -lt 2 ] ; then
	echo "This script is used for updating OpenSSH, changing SSH port and disabling root access through SSH."
	echo "Usage: sh $0 [port] [file path]."
	echo "e.g., sh $0 33333 /etc/ssh/sshd_config."
	exit 0
fi

echo "$0: [port] is $1 and [file path] is $2."

#########################
# Update OpenSSH Server #
#########################
echo "$0: yum -y install openssh-server"
yum -y install openssh-server

##########
# Backup #
##########
echo "$0: cp -f $2 $2.bak_\`date +\"%Y-%m-%d_%H:%M:%S\"\`"
cp -f $2 $2.bak_`date +"%Y-%m-%d_%H:%M:%S"`

echo "" >> $2
echo "#$0" >> $2

###################
# Change SSH Port #
###################
echo "Port $1" >> $2

###################################
# Disable Root Access through SSH #
################################### 
echo "$0: sed -i \"s/PermitRootLogin yes/#PermitRootLogin yes/g\" $2"
sed -i "s/PermitRootLogin yes/#PermitRootLogin yes/g" $2
echo "PermitRootLogin no" >> $2
echo "$0: $2 is modified."

###########
# SELinux #
###########
isSELinuxEnabled(){
	local str=$(sestatus)

	if [[ $str =~ .*SELinux[[:space:]]status:[[:space:]]*disabled* ]] ; then
		false
	else
		true
	fi
}

if isSELinuxEnabled ; then
	echo "$0: SELinux is enabled."
	# Install cmd "semanage"
	echo "$0: yum -y install policycoreutils-python"
	yum -y install policycoreutils-python
	echo "$0: \"policycoreutils-python\" is installed."
	semanage port -a -t ssh_port_t -p tcp $1
	echo "$0: semanage port -l | grep ssh"
	semanage port -l | grep ssh
else
	echo "$0: SELinux is disabled."
fi

############
# Firewall #
############
isFirewallActive(){
	local str=$(systemctl status firewalld)

	if [[ $str =~ .*Active:[[:space:]]active[[:space:]]\(running\) ]]; then
		true
	else
		false
	fi
}

if isFirewallActive ; then
	echo "$0: firewall is active"
	echo "$0: firewall-cmd --zone=public --add-port=$1/tcp --permanent"
	firewall-cmd --zone=public --add-port=$1/tcp --permanent
	echo "$0: systemctl restart firewalld"
	systemctl restart firewalld
	echo "$0: firewall-cmd --list-ports"
	firewall-cmd --list-ports
else
	echo "$0: firewall is inactive"
fi

################
# Restart SSHD #
################
echo "$0: systemctl restart sshd.service"
systemctl restart sshd.service
echo "$0: systemctl status sshd.service"
systemctl status sshd.service
echo "$0: Complete!"
