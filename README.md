datamining-test
===============

To create an executable jar run the folllowing command,
mvn clean package

Now create a folder under $HOME
mkdir $HOME/datamining-test

cp -af target/* $HOME/datamining-test/*

To execute the program,
cd $HOME/datamining-test
sh datamining.sh

Results
-------
*.DAT files will be created under
/tmp/datamining-test

Expected files
---------------
ls /tmp/datamining-test
ERROR_GRAPH.dat		PERFORMANCE_GRAPH.dat
