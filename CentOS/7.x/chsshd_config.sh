#!/bin/bash

if [ $# -lt 2 ] ; then
	echo "This script is used for updating OpenSSH, changing SSH port and disabling root access through SSH."
	echo ""
	echo "Usage: sh $0 -p [port] -f [file path]."
	echo "e.g., sh $0 -p 33333 -f /etc/ssh/sshd_config"
	echo ""
	echo "If [file path] is unpsecified, '/etc/ssh/sshd_config' will be used."
	exit 0
fi

while getopts "f:p:" opt  
do  
    case $opt in  
        f)  
        filepath=$OPTARG
        ;;  
        p)  
        port=$OPTARG
        ;;   
        ?)   
        ;;  
    esac  
done

if [ ! $filepath ] ; then
	filepath="/etc/ssh/sshd_config"
fi

echo "$0: filepath=$filepath"
echo "$0: port=$port"

#########################
# Update OpenSSH Server #
#########################
echo "$0: yum -y install openssh-server"
yum -y install openssh-server

##########
# Backup #
##########
echo "$0: cp -f $filepath $filepath.bak_\`date +\"%Y-%m-%d_%H:%M:%S\"\`"
cp -f $filepath $filepath.bak_`date +"%Y-%m-%d_%H:%M:%S"`

echo "" >> $filepath
echo "#$0" >> $filepath

###################
# Change SSH Port #
###################
echo "Port $port" >> $filepath

###################################
# Disable Root Access through SSH #
################################### 
echo "$0: sed -i \"s/PermitRootLogin yes/#PermitRootLogin yes/g\" $filepath"
sed -i "s/PermitRootLogin yes/#PermitRootLogin yes/g" $filepath
echo "PermitRootLogin no" >> $filepath
echo "$0: $filepath is modified."

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
	echo "$0: semanage port -a -t ssh_port_t -p tcp $port"
	semanage port -a -t ssh_port_t -p tcp $port
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
	echo "$0: firewall-cmd --zone=public --add-port=$port/tcp --permanent"
	firewall-cmd --zone=public --add-port=$port/tcp --permanent
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
