package LzmWikiIndex;

/**
 * Created by lzmhhh123 on 12/6/17
 * <lzmhhh123@gmail.com>
 */

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class TF_IDF {
		
	public static class TF_IDFMap extends Mapper<LongWritable, Text, Text, TextArrayWritable> {
		private double page = 0;
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String doc[] = value.toString().split("\t");
			if (doc[0].indexOf('-') < 0) {
				if (doc[0].equals("0pages")) {
					page = Double.parseDouble(doc[1]);
				} else {
					while(page == 0);
					Text tmp1[] = {new Text(String.valueOf(Math.log(page/Double.parseDouble(doc[1]))))};
					TextArrayWritable tmp = new TextArrayWritable(tmp1);
					context.write(new Text(doc[0]), tmp);
				}
			} else {
				double tf = Double.parseDouble(doc[1]);
				doc = doc[0].split("-");
				Text tmp1[] = { new Text(String.valueOf(doc[0])), new Text(String.valueOf(tf)) };
				TextArrayWritable tmp = new TextArrayWritable(tmp1);
				context.write(new Text(doc[1]), tmp);
			}
		}
	}

	public static class TF_IDFReduce extends Reducer<Text, TextArrayWritable, Text, DoubleWritable> {
		private Text word = new Text();
		@Override
		protected void reduce(Text key, Iterable<TextArrayWritable> values, Context context) throws IOException, InterruptedException {
			double IDF = 0;
			for (TextArrayWritable val : values) {
				if (val.get().length == 1) {
					IDF = Double.parseDouble(((Text)val.get()[0]).toString());
					break;
				}
			}
			for (TextArrayWritable val : values) {
				if (val.get().length != 1) {
					double tf = Double.parseDouble(((Text)val.get()[1]).toString());
					word.set(((Text)val.get()[0]).toString() + '-' + key.toString());
					context.write(word, new DoubleWritable(tf * IDF));
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job =Job.getInstance(conf);
		job.setJobName("TF-IDFCount");
		job.setJarByClass(TF_IDF.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(TextArrayWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		
		job.setMapperClass(TF_IDFMap.class);
		job.setReducerClass(TF_IDFReduce.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));
		boolean wait = job.waitForCompletion(true);
		System.exit(wait ? 0 : 1);
	}
}
