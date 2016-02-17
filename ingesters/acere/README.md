# Concrete ACERE Ingester
Concrete ingester supporting ACERE data.

## Quick start
This project uses the
[ACE 2005 Multilingual Training Corpus](https://catalog.ldc.upenn.edu/LDC2006T06)
from the LDC. Before beginning, ensure you have extracted the files
from the corpus to a location on disk.

Additionally, ensure you have Java 1.8 and Maven installed.

``` sh
LDC_PATH=/path/to/your/LDC2006T06
sh run.sh $LDC_PATH
```

## License
This project is licensed under GPLv3. See [LICENSE](LICENSE).
