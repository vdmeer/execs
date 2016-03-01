#!/bin/bash


## Copyright 2014-2015 Sven van der Meer <vdmeer.sven@mykolab.com>
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
## 
##     http://www.apache.org/licenses/LICENSE-2.0
## 
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##

##
## Shell script to automate generation of run scripts for ExecS.
## Script can only be run locally, i.e. in the directory it is located in.
##
## @package    de.vandermeer.execs
## @author     Sven van der Meer <vdmeer.sven@mykolab.com>
## @copyright  2014-2015 Sven van der Meer
## @license    http://www.apache.org/licenses/LICENSE-2.0  Apache License, Version 2.0
## @version    v0.3.4 build 160301 (01-Mar-16)
##


LIB_HOME=../lib/java
PROP_FILE=
EXECS_CLASS=
BIN_TEMPLATES=../etc/bin-templates


##
## do not change anything below this line unless you know what you are doing :)
##

system=`uname -o`

dir_project_home_abs=`(cd ..;pwd)`

CP=${LIB_HOME}/*
SCRIPT_NAME=`basename $0`

if [ "$system" == "Cygwin" ] ; then
	if [[ $dir_project_home_abs == *"/cygdrive"* ]]; then
		dir_project_home_sh="/"`echo $dir_project_home_abs | cut -d/ -f4-`
	else
		dir_project_home_sh=${dir_project_home_abs}
	fi
	dir_project_home_cyg=`cygpath -m ${dir_project_home_abs}`
	dir_project_home_bat=`cygpath -m ${dir_project_home_abs}`
else
	dir_project_home_sh=${dir_project_home_abs}
fi


##
## Create application specific templates
##
DoTemplates()
{
	if [ -d "${BIN_TEMPLATES}/$1" ]; then
		for template in `find ${BIN_TEMPLATES}/$1 -type f`
		#for template in `find ../etc/bin-templates/$1 -type f`
		do
			cat ./$1/_header $template > $1/$(basename "$template").$1
			chmod 755 $1/$(basename "$template").$1
			if [ "$1" = "cyg" ]; then
				mv $1/$(basename "$template").$1 $1/$(basename "$template").sh
			fi
		done
	fi
}


##
## Cleanup: remove create artifacts and change all created files to be executable
##
CleanUp()
{
	rm $1/_header
	rm $1/gen-run-scripts.*
	chmod 755 $1/*
}


##
## Create bin directories for bat, cyg, and sh targets
##
CreateBin()
{
	if [ "$dir_project_home_sh" != "" ]; then
		echo ""
		echo "-----------------------------------------------------------------"
		echo "- generating for target sh"
		java -classpath "${CP}" ${EXECS_CLASS} gen-run-scripts --target sh --property-file ${PROP_FILE} --application-dir ${dir_project_home_sh} $*
		for file in `find -type f -print|grep "sh/"`
		do
			sed -i 's/\r//' $file
		done
		DoTemplates sh
		CleanUp sh
		echo "-----------------------------------------------------------------"
		echo ""
	fi

	if [ "$dir_project_home_cyg" != "" ]; then
		echo ""
		echo "-----------------------------------------------------------------"
		echo "- generating for target cyg"
		java -classpath "${CP}" ${EXECS_CLASS} gen-run-scripts --target cyg --property-file ${PROP_FILE} --application-dir ${dir_project_home_cyg} $*
		for file in `find -type f -print|grep "cyg/"`
		do
			sed -i 's/\r//' $file
		done
		DoTemplates cyg
		CleanUp cyg
		echo "-----------------------------------------------------------------"
		echo ""
	fi

	if [ "$dir_project_home_bat" != "" ]; then
		echo ""
		echo "-----------------------------------------------------------------"
		echo "- generating for target bat"
		java -classpath "${CP}" ${EXECS_CLASS} gen-run-scripts --target bat --property-file ${PROP_FILE} --application-dir ${dir_project_home_bat} $*
		for file in `find -type f -print|grep "bat/"`
		do
			sed -i 's/$/\r/' $file
		done
		DoTemplates bat
		CleanUp bat
		echo "-----------------------------------------------------------------"
	fi
}


##
## Rebase an application install, i.e. change APPLICATION_HOME in all scripts
##
Rebase()
{
	if [ "$dir_project_home_sh" != "" ]; then
		echo "- rebasing sh folder"
		for file in `ls sh`
		do
			sed -i 's|^APPLICATION_HOME=.*$|APPLICATION_HOME='$dir_project_home_sh'|' sh/$file
		done
	fi

	if [ "$dir_project_home_cyg" != "" ]; then
		echo "- rebasing cyg folder"
		for file in `ls cyg`
		do
			sed -i 's|^APPLICATION_HOME=.*$|APPLICATION_HOME='$dir_project_home_cyg'|' cyg/$file
		done
	fi

	if [ "$dir_project_home_bat" != "" ]; then
		echo "- rebasing bat folder"
		for file in `ls bat`
		do
			sed -i 's|^set APPLICATION_HOME=.*$|set APPLICATION_HOME='$dir_project_home_bat'|' bat/$file
		done
	fi
}


##
## Help screen and exit condition (i.e. too few arguments)
##
Help()
{
	echo ""
	echo "$SCRIPT_NAME - configure application"
	echo ""
	echo "       Usage:  $SCRIPT_NAME [-options]"
	echo ""
	echo "       Options"
	echo "         -c    - create directories with run scripts for supported targets (bat, cyg, sh)"
	echo "         -h    - this help screen"
	echo "         -r    - rebase run scripts (use if installation directory changed)"
	echo ""
	exit 255;
}
if [ $# -eq 0 ]; then
	Help
fi


while [ $# -gt 0 ]
do
	case $1 in
		#-c create bin directories
		-c)		CreateBin;exit 255;;

		#-h prints help and exists
		-h)		Help;exit 255;;

		#-r rebase scripts
		-r)		Rebase;exit 255;;

		*)	echo "$SCRIPT_NAME: undefined CLI option - $1"; exit 255;;
	esac
done
