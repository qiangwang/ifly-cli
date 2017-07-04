#讯飞语音命令行工具

`java平台` `命令行工具` 目前只支持语音转文字功能，可根据需要自行扩展。

##构建

``` 
#进入gradle容器命令行
./build.sh

#执行编译，打包，生成ifly-cli.jar
gradle build
```

##执行

* 语音转文本
```
#xxx替换为你申请的java平台的appId
java -Djava.library.path="./binLib" -jar build/libs/ifly-cli.jar audioToText --appId xxx -i test/test.wav -o test/tmp.txt
```
