app=datamining-test
app_home=$HOME/$app

jar=$app_home/datamining-test.dependencies.jar

export CLASSPATH=$CLASSPATH:$app_home:$app_home/*:$jar

cd $app_home && time java ${JVM_ARGS} \
-classpath $CLASSPATH \
com.mstest.datamining.app.DataAnalyzer > ./log_file 2>&1
