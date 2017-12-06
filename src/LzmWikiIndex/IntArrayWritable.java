package LzmWikiIndex;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.IntWritable;

public class IntArrayWritable extends ArrayWritable {
	public IntArrayWritable() {
		super(IntWritable.class);
	}
	public IntArrayWritable(IntWritable[] t) {
		super(IntWritable.class, t);
	}
}
