package LzmWikiIndex;

/**
 * Created by lzmhhh123 on 12/6/17
 * <lzmhhh123@gmail.com>
 */

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;

public class TextArrayWritable extends ArrayWritable {
	public TextArrayWritable() {
		super(Text.class);
	}
	public TextArrayWritable(Text[] t) {
		super(Text.class, t);
	}
}
