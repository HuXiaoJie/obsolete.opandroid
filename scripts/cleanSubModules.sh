#!/bin/bash
LOGFILE=$PWD/cleanlog.txt
echo $LOGFILE
 cleanSubModules()
 {
 	echo "cleanSubModules $PWD"
	if [ -f .gitmodules ]; then
 	for path in `grep path .gitmodules |cut -d \  -f3`;
 	do
 		if [ -d $path ];then
	 		pushd $PWD > /dev/null
	 		echo "entering $path" 
	 		cd $path
	 		git clean -fd >> $LOGFILE 2>&1
	 		git checkout . >> $LOGFILE 2>&1
			cleanSubModules
	 		popd > /dev/null
	 	else
	 		echo "$PWD/$path is not directory, go fix the .gitmodules file"!
 		fi
 	done
	fi
 }


 cleanSubModules
