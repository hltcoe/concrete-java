Copyright 2012-2014 Johns Hopkins University HLTCOE. All rights
reserved.  This software is released under the 2-clause BSD license.
See LICENSE in the project root directory.

Concrete - Python
========

Requirements
------------

Concrete-Python requires the following:
* Python >= 2.7.x
* Apache Thrift >= 0.9.1
* 'thrift' Python package >= 0.9.1

Installation
------------

In order to generate the Python classes for Concrete, the Thrift
compiler (`thrift`) must be in your path.

Checkout the latest code:

    git clone git@github.com:hltcoe/concrete.git

On a *nix-like system, running:

    cd python
    python setup.py install

will build and install the 'concrete' package.


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
