# Wikipedia-Index
A distributed system of Wikipedia Index. Project of Fudan University Distributed System Course.
Using Hadoop File System and MapReduce Framework.

## Dependencies
- [Hadoop File System](http://hadoop.apache.org/)
- [Wikipedia](https://dumps.wikimedia.org/enwikisource/latest/)
- [node.js](https://nodejs.org/)
- [yarn](https://yarnpkg.com/)

## Usage
1. `git clone https://github.com/lzmhhh123/Wikipedia-Index & cd Wikipedia-Index`
2. Use the [Eclipse](https://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/neon3) to export `.jar` package.
3. `hadoop fs -put ${WikipediaFilePath} ${YourHadoopFileSystemPath}` to put Wikipedia on Hadoop file system.
4.
```
hadoop jar LzmWikiIndex.jar LzmWikiIndex.TF ${WikipediaPathOnHadoop} ${TFOutputPath}
hadoop jar LzmWikiIndex.jar LzmWikiIndex.DF ${TFOutputPath}/part-r-00000 ${DFOutputPath}
hadoop jar LzmWikiIndex.jar LzmWikiIndex.TF_IDF ${DFOutputPath}/part-r-00000 ${TFOutputPath}/part-r-00000 ${TF-IDFOutputPath}
hadoop jar LzmWikiIndex.jar LzmWikiIndex.MaxThreeLabel ${TF-IDFOutputPath}/part-r-00000 ${MaxThreeWordOutputPath}
```
5. `cd view & yarn build`
6. `echo ${WikipediaFilePath} > WikipediaPath`
6. `hadoop fs -get ${MaxThreeWordOutputPath}/part-r-00000 ${ThisRepoPath}/view/`
7. `java -classpath LzmWikiIndex.jar LzmWikiIndex.IdOffset ${WikipediaFilePath} ${ThisRepoPath}/view/IdOffset`
8. `node bin/www` then you can see the web page at http://localhost:8080

## Source File Architecture
<pre>
WikipediaIndex
├── bin                           //Java class
│   ├── DF.class
│   ├── ...
│   └── XmlInputFormat.class
├── src                           //MapReduce source file
│   ├── DF.java                   //count document frequency
│   ├── ExtractPage.java          //extract each page to a independent file
│   ├── IdOffset.java             //count each page's size & offset in Wikipedia
│   ├── IntArrayWritable.java     //A class extends ArrayWritable
│   ├── MaxThreeLabel.java        //count the biggest three TF-IDF words for each page
│   ├── PageWordCount.java        //count the number of words for each PageWordCount
│   ├── TextArrayWritable.java    //A class extends ArrayWritable
│   ├── TF_IDF.java               //count term frequency–inverse document frequency
│   ├── TF.java                   //count term frequency
│   └── XmlInputFormat.java       //A class extends TextInputFormat
├── view
│   ├── bin
│   │   └── www                   //start node.js server
│   ├── public                    //frontend static file
│   │   ├── favicon.ico
│   │   ├── ...
│   │   └── manifest.json
│   ├── src                       //frontend source file
│   │   ├── App.css
│   │   ├── App.js
│   │   ├── index.css
│   │   ├── ...
│   │   └── logo.svg
│   ├── app.js                    //main node.js file
│   ├── package.json
│   └── yarn.lock
├── preview.jpg
├── preview2.jpg
└── README.md
</pre>

## Preview
![Alt text](/preview.jpg)
![Alt text](/preview2.jpg)
