#!/bin/sh
app=datamining-test
app_home=$HOME/$app

while getopts o:d flag
do
    case "$flag" in
        o) output_dir=$OPTARG;;
        d) decision_tree=1;;
        *) echo "Invalid arg";;
    esac
done

if [ "$output_dir" != "" ]; then
    OUTPUT_DIR="--output_dir $output_dir"
fi

if [[ ! -z $decision_tree ]]
then
    ALGORITHM="--decisiontree"
fi

jar=$app_home/datamining-test.dependencies.jar

export CLASSPATH=$CLASSPATH:$app_home:$app_home/*:$jar

command_str="cd $app_home && time java ${JVM_ARGS} -classpath $CLASSPATH com.mstest.datamining.app.DataAnalyzerApp ${ALGORITHM} ${OUTPUT_DIR} > ./log_file 2>&1"

echo "$command_str"

cd $app_home && time java ${JVM_ARGS} -classpath $CLASSPATH com.mstest.datamining.app.DataAnalyzerApp ${ALGORITHM} ${OUTPUT_DIR} > ./log_file 2>&1