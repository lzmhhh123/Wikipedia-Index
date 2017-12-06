package LzmWikiIndex;

/**
 * Created by lzmhhh123 on 12/6/17
 * <lzmhhh123@gmail.com>
 */

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MaxThreeLabel {
		
	public static class MaxThreeLabelMap extends Mapper<LongWritable, Text, Text, TextArrayWritable> {
		private Text word = new Text();
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String doc[] = value.toString().split("\t");
			double TF_IDF = Double.parseDouble(doc[1]);
			doc = doc[0].split("-");
			word.set(doc[0]);
			Text tmp[] = {new Text(doc[1]), new Text(String.valueOf(TF_IDF))};
			context.write(word, new TextArrayWritable(tmp));
		}
	}

	public static class MaxThreeLabelReduce extends Reducer<Text, TextArrayWritable, Text, Text> {
		@Override
		protected void reduce(Text key, Iterable<TextArrayWritable> values, Context context) throws IOException, InterruptedException {
			double max1 = 0, max2 = 0, max3 = 0;
			String word1 = "", word2 = "", word3 = "";
			for (TextArrayWritable val : values) {
				String word = val.get()[0].toString();
				double TF_IDF = Double.parseDouble(val.get()[1].toString());
				if (TF_IDF > max1) {
					max3 = max2; max2 = max1; max1 = TF_IDF;
					word3 = word2; word2 = word1; word1 = word;
				} else if (TF_IDF > max2) {
					max3 = max2; max2 = TF_IDF;
					word3 = word2; word2 = word;
				} else if (TF_IDF > max3) { 
					max3 = TF_IDF;
					word3 = word;
				}
			}
			context.write(key, new Text(word1));
			context.write(key, new Text(word2));
			context.write(key, new Text(word3));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job =Job.getInstance(conf);
		job.setJobName("MaxThreeLabel");
		job.setJarByClass(MaxThreeLabel.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(TextArrayWritable.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(MaxThreeLabelMap.class);
		job.setReducerClass(MaxThreeLabelReduce.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		boolean wait = job.waitForCompletion(true);
		System.exit(wait ? 0 : 1);
	}
}
