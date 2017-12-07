package LzmWikiIndex;

/**
 * Created by lzmhhh123 on 12/5/17
 * <lzmhhh123@gmail.com>
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class ExtractPage {
	
	public static class ExtractPageMap extends Mapper<LongWritable, Text, Text, Text> {
		
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
			
			String id = slice(doc, "<id>", "</id>", false);
			if (id.length() < 1) return;
			
			context.write(new Text(id), new Text(text));
		}
	}

	public static class ExtractPageReduce extends Reducer<Text, Text, Text, Text> {
		private MultipleOutputs<Text, Text> outputs;
		private Set<String> S = new HashSet<String>();
		
		@Override  
        protected void setup(Context context) throws IOException, InterruptedException {  
            outputs = new MultipleOutputs<Text, Text>(context);  
        }
		
		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for (Text val : values) {
				if (S.contains(key.toString())) break;
				S.add(key.toString());
				outputs.write(key, val, "/ExtractPages/page" + key.toString());
				break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("xmlinput.start", "<page>");
		conf.set("xmlinput.end", "</page>");
		
		Job job =Job.getInstance(conf);
		job.setJobName("ExrtactPages");
		job.setJarByClass(ExtractPage.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(ExtractPageMap.class);
		job.setReducerClass(ExtractPageReduce.class);
		
		job.setInputFormatClass(XmlInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		boolean wait = job.waitForCompletion(true);
		System.exit(wait ? 0 : 1);
	}
}
