#!/bin/sh
app=datamining_test
app_home=$HOME/$app

if [[ ! -d "$app_home" ]]
then
    echo "Make sure to create directory structure like this <Your home dir>/datamining_test and copy all the contents of tar file\n"
    exit -1
fi

jar=$app_home/datamining-test.dependencies.jar

if [[ ! -s "$jar" ]]
then
    echo "Important jar missing $jar. Check & re-run the program\n"
    exit -1
fi

while getopts "jmkso:acze" flag
do
    case "$flag" in
        o) output_dir=$OPTARG;;
        j) j48=1;;
        m) mlp=1;;
        k) knn=1;;
        a) adaboost=1;;
        s) svm=1;;
        c) assign3=1;;
        z) seed=1;;
        e) em=1;;
        *) echo "Invalid arg";;
    esac
done

if [ "$output_dir" != "" ]; then
    OUTPUT_DIR="--output_dir $output_dir"
fi

if [[ ! -z "$j48" ]]
then
    ALGORITHM="--j48"
fi

if [[ ! -z "$mlp" ]]
then
    ALGORITHM="--multilayerperceptron"
fi

if [[ ! -z "$knn" ]]
then
    ALGORITHM="--knn"
fi

if [[ ! -z "$adaboost" ]]
then
    ALGORITHM="--adaboost"
fi

if [[ ! -z "$svm" ]]
then
    ALGORITHM="--svm"
fi

if [[ ! -z "$assign3" ]]
then
    ALGORITHM="--assn3"
fi

if [[ ! -z "$seed" ]]
then
    ALGORITHM="--seed"
fi

if [[ ! -z "$em" ]]
then
    ALGORITHM="--em"
fi

if [[ -z "$ALGORITHM" ]]
then
    echo "\nUsage: sh datamining.sh -<j|m|k|a|s|c|z|e> -o <full_path>"
    echo "\t\t -<j|m|k|a|s|c|z|e> -> specifies the classification type"
    echo "\t\t\t j - j48"
    echo "\t\t\t m - multilayerperceptron"
    echo "\t\t\t k - knn"
    echo "\t\t\t a - adaboost"
    echo "\t\t\t s - svm"
    echo "\t\t\t c - clustering / assignment3"
    echo "\t\t\t z - clustering / seed"
    echo "\t\t\t e - clustering / em"
    echo "\t\t -o output_dir will default to /tmp/datamining-test/<algorithm>\n"
    exit
fi

export CLASSPATH=$CLASSPATH:$app_home:$app_home/*:$jar

command_str="cd $app_home && time java ${JVM_ARGS} -classpath $CLASSPATH com.mstest.datamining.app.DataAnalyzerApp ${ALGORITHM} ${OUTPUT_DIR} > ./log_file 2>&1"

echo "$command_str"

jvm_args="-Xms2048m -Xmx4096m"

cd $app_home && time java $jvm_args -classpath $CLASSPATH com.mstest.datamining.app.DataAnalyzerApp ${ALGORITHM} ${OUTPUT_DIR} > ./log_file 2>&1
