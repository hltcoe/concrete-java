# Simple Concrete Ingesters

Simple ingesters supporting the [Concrete](https://github.com/hltcoe/concrete) library.

## Build the utility
From `ingesters/simple`, run:
```sh
mvn clean compile assembly:single
```

## Run CompleteFileIngester
This program ingests a file with UTF-8 characters into a Concrete `Communication` object.

Takes 3 arguments:
* `/path/to/utf8/text/file` - Path to a UTF-8 encoded character-based file.
* `communication-type` - The type of `Communication`s to be produced (e.g., `story`)
* `/output/dir` - A directory where the file will be written.

```sh
java -cp target/concrete-ingesters-simple-4.5.7-jar-with-dependencies.jar \
    edu.jhu.hlt.ingesters.simple.CompleteFileIngester \
    /path/to/utf8/text/file \
    communication-type \
    /output/dir
```

## Run DoubleLineBreakFileIngester
This program ingests a file with UTF-8 characters into a Concrete `Communication` object. If
the file has multiple newline characters (`\n\n`), the `Communication` will contain one `Section`
that represents all content between each set of double-newlines.

Takes 4 arguments:
* `/path/to/utf8/text/file` - Path to a UTF-8 encoded character-based file.
* `communication-type` - The type of `Communication`s to be produced, (e.g., `story`)
* `section-type` - The type of `Section`s to be produced (e.g., `passage`)
* `/output/dir` - A directory where the file will be written.

```sh
java -cp target/concrete-ingesters-simple-4.5.7-jar-with-dependencies.jar \
    edu.jhu.hlt.ingesters.simple.DoubleLineBreakFileIngester \
    utf8-file-with-double-newlines.txt \
    communication-type \
    section-type \
    /output/dir
```
