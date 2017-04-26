#!/bin/bash

REPO=/Users/gcuellar/Documents/workspace/bz-datazone
TOMCAT_WEB=/Users/gcuellar/webapps
STATUS_FILE=sync-status_`date +'%Y%m%d-%H%M%S'`.txt
LOG_FILE=log_`date +'%Y%m%d-%H%M%S'`.txt
WORKING_DIR=`pwd`
STATUS_FILE_PATH=$WORKING_DIR/$STATUS_FILE
BUILD_RESULTS=$WORKING_DIR/build-results_`date +'%Y%m%d-%H%M%S'`.txt
BUILD_WAR_FILE=datazone-0.1.0.war
BUILD_WAR_PATH=$REPO/build/libs/$BUILD_WAR_FILE


function logger {
    echo `date +'%Y-%m-%d %H:%M:%S'` $1 
}

cd $REPO

logger "Build and deploy started."
logger "Logging to file: $LOG_FILE"
logger "Cleaning log files."
rm scripts/*.txt

logger "Syncing repository."

git pull > $WORKING_DIR/$STATUS_FILE
STATUS=`cat "$STATUS_FILE_PATH"`
logger "Sync Status = [$STATUS]"


if [ "$STATUS" != 'Already up-to-date.' ]; then

	./gradlew build > $BUILD_RESULTS
	IS_SUCCESS=`cat $BUILD_RESULTS | grep SUCCESSFUL | wc -l`
	logger "Is Build Success = $IS_SUCCESS"

	if [ "$IS_SUCCESS" -gt "0" ]; then
		logger "Undeploying $BUILD_WAR_FILE"
		rm -rf $TOMCAT_WEB/$BUILD_WAR_FILE
		logger "Deploying new build."
		cp $BUILD_WAR_PATH $TOMCAT_WEB
	else
		logger "Build error. Could not deploy."
	fi 
else
	logger "Nothing to build or deploy.  No changes detected."
fi


logger "Finished"





