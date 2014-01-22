Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights
reserved.  This software is released under the 2-clause BSD license.
See LICENSE in the project root directory.

Concrete - Python
========

Requirements
------------

Concrete-Python requires the following:
* Python >= 2.7.x
* Thrift installed with the python package

Installation
------------

First, checkout the latest code:

    git clone git@github.com:hltcoe/concrete.git

On a *nix-like system, running:

    cd python
    sh build-python-thrift.sh ../thrift/

will build the Python classes for Concrete in a folder called
`gen-py`. You will then need to add these to your script's build path:

```python
import sys
sys.path.append('gen-py')
```

Using the code in your project
------------------------------

Compiled Python classes end up in the `concrete` namespace. You can
use them by importing them as follows:

```python
from concrete.communication import *
from concrete.communication.ttypes import *

foo = Communication()
foo.text = 'hello world'
...
```

Consult `test-thrift.py` for an example script.
