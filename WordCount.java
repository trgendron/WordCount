package org.myorg;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class WordCount 
   {
   public static class Map extends MapReduceBase 
                 implements Mapper<LongWritable, Text, Text, IntWritable> 			
      {		
      private final static IntWritable one = new IntWritable(1);
      private Text word = new Text();
	
      public void map( LongWritable key, Text value, 
                       OutputCollector<Text, IntWritable> output, Reporter reporter
                     ) throws IOException 
	     {

	     // convert to lower case 
	     // delimet on whitespace and all punctuation

	     String line = value.toString().toLowerCase(); 
	     String[] tokens = line.split("[\\s* \\p{Punct}]+");

	     for (String token : tokens )
	        {
	        word.set(token);
		output.collect(word, one); 
		}
	     }
        }

    public static class Reduce extends MapReduceBase 
                  implements Reducer<Text, IntWritable, Text, IntWritable> 
        {
	public void reduce( Text key, Iterator<IntWritable> values, 
                            OutputCollector<Text, IntWritable> output, Reporter reporter
                          ) throws IOException 
            {
	    int sum = 0;
	    while (values.hasNext()) 
              {
	      sum += values.next().get();
	      }
	      output.collect(key, new IntWritable(sum));
	    }
	}
    public static void main(String[] args) throws Exception
        {
	JobConf conf = new JobConf(WordCount.class);

	conf.setJobName("wordcount");
	conf.setOutputKeyClass(Text.class);
	conf.setOutputValueClass(IntWritable.class);
	conf.setMapperClass(Map.class);
	conf.setCombinerClass(Reduce.class);
	conf.setReducerClass(Reduce.class);
	conf.setInputFormat(TextInputFormat.class);
	conf.setOutputFormat(TextOutputFormat.class);
	conf.set("mapred.job.tracker", "local");

	FileInputFormat.setInputPaths(conf, new Path(args[0]));
	FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	JobClient.runJob(conf);
	}
   }
