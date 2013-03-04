rm -r ./bigram
mkdir bigram
javac -verbose -classpath  /usr/local/Cellar/hadoop/1.1.1/libexec/hadoop-core-1.1.1.jar -d bigram_classes BigramCount.java
jar -cvf bigram.jar -C bigram_classes/ .
hadoop jar ./bigram.jar org.myorg.BigramCount ./BILLS-112hr3261ih.htm ./bigram/output
