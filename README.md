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
hadoop jar LzmWikiIndex.jar LzmWikiIndex.ExtractPage ${WikipediaPathOnHadoop} ${anyPath}
```
5. `cd view & yarn build`
6. `hadoop fs -get ${MaxThreeWordOutputPath}/part-r-00000 ${ThisRepoPath}/build`
7. `hadoop fs -get /Extractpages ${ThisRepoPath}/pages`
8. `cd .. & node bin/www` then you can see the web page at http://localhost:8000

