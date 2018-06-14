#!/bin/bash

if [ $# -lt 1 ] ; then
	echo "This script is used for configuring automatic updates. Please make sure yum-cron is installed and enabled."
	echo "Usage: $0 [file path], e.g., $0 /etc/yum/yum-cron.conf."
	exit 0
fi

echo "$0: [file path] is $1."

##########
# Backup #
##########
echo "$0: cp -f $1 $1.bak_\`date +\"%Y-%m-%d_%H:%M:%S\"\`"
cp -f $1 $1.bak_`date +"%Y-%m-%d_%H:%M:%S"`

echo "" >> $1
echo "#$0" >> $1

##################################
# Specify the Category of Update #
##################################
echo "$0: sed -i 's/update_cmd = default/#update_cmd = default/g' $1"
sed -i "s/update_cmd = default/#update_cmd = default/g" $1
echo "update_cmd = security" >> $1

#################
# Apply Updates #
#################
echo "$0: sed -i 's/apply_updates = no/#apply_updates = no/g' $1"
sed -i "s/apply_updates = no/#apply_updates = no/g" $1
echo "apply_updates = yes" >> $1

echo "$0: systemctl restart yum-cron.service"
systemctl restart yum-cron.service
echo "$0: systemctl status yum-cron.service"
systemctl status yum-cron.service