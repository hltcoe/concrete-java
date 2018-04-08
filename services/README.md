## Concrete services

### Command line tools

#### Build

You need [maven](https://maven.apache.org/).

Start from the [root project directory](..).

``` shell
mvn install
cd services
mvn clean package
```

#### SummarizeTool

The `SummarizeTool` is exposed via `summarize.sh`

``` shell
### after buld step above
./summarize.sh --help
```
