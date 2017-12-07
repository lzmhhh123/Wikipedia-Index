package LzmWikiIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class IdOffset {
	
	public static void main(String args[]) throws Exception {
		FileReader fr = new FileReader(args[0]);
		FileWriter fw = new FileWriter(args[1]);
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		long cnt = 0;
		boolean isInPage = false;
		String id = "";
		while ((line = br.readLine()) != null) {
			if (line.indexOf("<page>") >= 0) {
				isInPage = true;
			}
			if (line.indexOf("<id>") >= 0 && isInPage == true) {
				isInPage = false;
				String subString = line.substring(line.indexOf("<id>") + 1, line.indexOf("</id>"));
				id = subString;
			}
			if (line.indexOf("<text") >= 0) {
				int s = line.indexOf("<text");
				while (line.charAt(s) != '>') s++;
				fw.write(id + ":" + String.valueOf(cnt + (s + 1) * 4) + '\n');
			}
			cnt += line.length() * 4;
		}
		fw.close();
		fr.close();
		br.close();
	}
}
