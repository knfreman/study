backupFile(){
	echo "cp -f $1 $1.bak_\`date +\"%Y-%m-%d_%H:%M:%S\"\`"
	cp -f $1 $1.bak_`date +"%Y-%m-%d_%H:%M:%S"`
}

##################################
# Show Line Number in Vi and Vim #
##################################

appendSetNumber(){
	backupFile $2
	echo "" >> $2
	echo $1 >> $2
	echo "set number" >> $2
}

appendSetNumber "\" $0" "/etc/virc"
appendSetNumber "\" $0" "/etc/vimrc"

 
