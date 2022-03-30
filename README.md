## simple-computer-architecture

Define instruction set and implement a simple calculator that can execute instructions 

### How to run 

Quick start (sample code)
```
git clone https://github.com/ecsimsw/simple-computer-architecture ecsimsw-ca
cd ecsimsw-ca
./gradlew process
```

run your own code file written in my ISA
```
./gradlew process -PinputFile="${filePath}"
```

you also can set multiple code files split by ,
```
./gradlew process -PinputFile="${filePath1, filePath2, filePath3}"
```

run test code
```
./gradlew test
```
