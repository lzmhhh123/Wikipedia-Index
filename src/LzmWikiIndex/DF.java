package LzmWikiIndex;

/**
 * Created by lzmhhh123 on 12/6/17
 * <lzmhhh123@gmail.com>
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class DF {
		
	public static class DFMap extends Mapper<LongWritable, Text, Text, IntWritable> {
		private Text word = new Text();
		private Set<String> M_id = new HashSet<String>();
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String doc[] = value.toString().split(String.valueOf('\t'));
			doc = doc[0].split(String.valueOf('-'));
			word.set(doc[1]);
			context.write(word, new IntWritable(1));
			if (!M_id.contains(doc[0])) {
				M_id.add(doc[0]);
				word.set(String.valueOf("0pages"));
				context.write(word, new IntWritable(1));
			}
		}
	}

	public static class DFReduce extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job =Job.getInstance(conf);
		job.setJobName("DocumentFrequencyCount");
		job.setJarByClass(DF.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapperClass(DFMap.class);
		job.setReducerClass(DFReduce.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		boolean wait = job.waitForCompletion(true);
		System.exit(wait ? 0 : 1);
	}
}
