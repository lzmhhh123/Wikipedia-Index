package LzmWikiIndex;

/**
 * Created by lzmhhh123 on 12/4/17
 * <lzmhhh123@gmail.com>
 */

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class PageWordCount {
	
	public static class PageWordCountMap extends 
			Mapper<LongWritable, Text, Text, IntWritable> {
		private Text word = new Text();
		
		private String slice(String doc, String st, String ed, Boolean judge) {
			int s = doc.indexOf(st), t = judge ? doc.lastIndexOf(ed) : doc.indexOf(ed);
			if (s < 0 || t < 0) return "";
			while (doc.charAt(s) != '>') s++;
			return doc.substring(s + 1, t);
		}
		
		public void map(LongWritable key, Text value, Context context) 
				throws IOException, InterruptedException {
			String doc = value.toString();
			
			String text = slice(doc, "<text", "</text>", true);
			if (text.length() < 1) return;
			
			char txt[] = text.toLowerCase().toCharArray();
			for (int i = 0; i < txt.length; ++i) {
				if (!((txt[i] >= 'a' && txt[i] <= 'z') || (txt[i] >= 'A' && txt[i] <= 'Z')))
					txt[i] = ' ';
			}
						
			String id = slice(doc, "<id>", "</id>", false);
			if (id.length() < 1) return;
			
			StringTokenizer itr = new StringTokenizer(String.valueOf(txt));
			word.set(id);
			context.write(word, new IntWritable(itr.countTokens()));
			while (itr.hasMoreTokens()) {
				String s = itr.nextToken();
				word.set(id + '-' + s);
				context.write(word, new IntWritable(1));
			}
			
		}
	}
	
	public static class PageWordCountReduce extends 
			Reducer<Text, IntWritable, Text, IntWritable> {
		public void reduce(Text key, Iterable<IntWritable> values, Context context) 
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			context.write(key, new IntWritable(sum));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("xmlinput.start", "<page>");
		conf.set("xmlinput.end", "</page>");
				
		Job job =Job.getInstance(conf);
		job.setJobName("PageWordCount");
		job.setJarByClass(PageWordCount.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapperClass(PageWordCountMap.class);
		job.setCombinerClass(PageWordCountReduce.class);
		job.setReducerClass(PageWordCountReduce.class);
		
		job.setInputFormatClass(XmlInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		job.waitForCompletion(true);
	}
	
}
