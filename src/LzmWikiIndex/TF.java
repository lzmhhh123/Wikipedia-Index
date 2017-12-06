package LzmWikiIndex;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class TF {
	
	public static class TFMap extends Mapper<LongWritable, Text, Text, IntArrayWritable> {
		private Text word = new Text();
		
		private String slice(String doc, String st, String ed, Boolean judge) {
			int s = doc.indexOf(st), t = judge ? doc.lastIndexOf(ed) : doc.indexOf(ed);
			if (s < 0 || t < 0) return "";
			while (doc.charAt(s) != '>') s++;
			return doc.substring(s + 1, t);
		}
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
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
			int sum = itr.countTokens();
			while (itr.hasMoreTokens()) {
				String s = itr.nextToken();
				word.set(id + '-' + s);
				IntWritable tmp[] = {new IntWritable(sum), new IntWritable(1)};
				IntArrayWritable temp = new IntArrayWritable(tmp);
				context.write(word, temp);
			}
		}
	}

	public static class TFReduce extends Reducer<Text, IntArrayWritable, Text, DoubleWritable> {
		@Override
		protected void reduce(Text key, Iterable<IntArrayWritable> values, Context context) throws IOException, InterruptedException {
			int sum = 0, textSum = 0;
			for (IntArrayWritable v : values) {
				Writable val[] = v.get();
				sum += ((IntWritable)val[1]).get();
				textSum = ((IntWritable)val[0]).get();
			}
//			context.write(key, new IntWritable(sum));
			double TF = (double)sum / textSum;
			context.write(key, new DoubleWritable(TF));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("xmlinput.start", "<page>");
		conf.set("xmlinput.end", "</page>");
		
		Job job =Job.getInstance(conf);
		job.setJobName("TermFrequencyCount");
		job.setJarByClass(TF.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntArrayWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		
		job.setMapperClass(TFMap.class);
		job.setReducerClass(TFReduce.class);
		
		job.setInputFormatClass(XmlInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		boolean wait = job.waitForCompletion(true);
		System.exit(wait ? 0 : 1);
	}
}
