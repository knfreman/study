#!/bin/bash

if [ $# -lt 1 ] ; then
	echo "This script is used for configuring automatic updates."
	echo "Usage: $0 [file path]."
	exit 0
fi

echo "$0: [file path] is $1."

##########
# Backup #
##########
echo "$0: cp -f $1 $1.bak"
cp -f $1 $1.bak

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

