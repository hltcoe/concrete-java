## Concrete services

### Command line tools

Before beginning, you'll need to handle configuration of the command
line tools.

The host and port configuration is handled in the configuration files.

The easiest way to get started is to copy
[this file](src/main/reference.conf) and name it `application.conf` in
the same folder (please don't check this file in).

In `application.conf`, update the host and port to hit whatever service you're
looking to link up with.

Example: you have a summary service running on `localhost:44422`:

``` shell
cp src/main/resources/reference.conf src/main/resources/application.conf
emacs src/main/resources/application.conf
### edit the 'summary' block to host = localhost, port = 44422
```

Placing the file in the `src/main/resources` means that upon building
the executable jar, it will be the "first" configuration (priority
wise) in the program. Those familiar with java classpaths can include
the `application.conf` file anywhere on the classpath when launching
the program.

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
