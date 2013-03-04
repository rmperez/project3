package org.myorg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;
public class BigramCount {

	public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
	  private final static IntWritable one = new IntWritable(1);
	  private Text word1 = new Text();
	  private Text word2 = new Text();
	  private Text bigram = new Text();
          private boolean first_time;

	  public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line);
		first_time = true;
		while (tokenizer.hasMoreTokens()) {
		  if (first_time){
		    word2.set(tokenizer.nextToken());  
		    first_time = false;
		  }
		  else{
		    word1.set(word2);
		    word2.set(tokenizer.nextToken());
		    bigram.set(word1.toString().concat(" ").concat(word2.toString()));
		    //System.out.println(word1);
		    //System.out.println(word2);
		    //System.out.println(bigram);
		    output.collect(bigram, one); // <"word1 + word2", 1>
		  }
		}
	  }
	}

	public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
	  public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
		int sum = 0;
		while (values.hasNext()) {
		  sum += values.next().get();
		}
		output.collect(key, new IntWritable(sum));
	  }
	}

	public static void main(String[] args) throws Exception {
	  JobConf conf = new JobConf(BigramCount.class);
	  conf.setJobName("bigramcount");

	  conf.setOutputKeyClass(Text.class);
	  conf.setOutputValueClass(IntWritable.class);

	  conf.setMapperClass(Map.class);
	  conf.setCombinerClass(Reduce.class);
	  conf.setReducerClass(Reduce.class);

	  conf.setInputFormat(TextInputFormat.class);
	  conf.setOutputFormat(TextOutputFormat.class);

	  FileInputFormat.setInputPaths(conf, new Path(args[0]));
	  FileOutputFormat.setOutputPath(conf, new Path(args[1]));

	  JobClient.runJob(conf);
	}
}

