import distutils.spawn
from setuptools import setup
from setuptools.command.build_py import build_py as _build_py

import glob
import subprocess


def compile_thrift_files():
    # Probe for 'thrift' executable in path
    if not distutils.spawn.find_executable('thrift'):
        raise SystemExit("\nERROR: Cannot find a 'thrift' executable anywhere in your path.\n" 
                         "See 'README.md' for installation requirements and instructions")
    for thrift_filename in glob.glob("../thrift/*.thrift"):
        subprocess.call(["thrift", "--gen", "py:new_style,utf8strings", thrift_filename])


# We want to generate the Python code for Concrete-Thrift data
# structures *before* we install the module.
# 
# setuptools, by design, only permits modules to depend on other
# Python modules - it does not let you invoke executables that
# generate files as part of the build process.
#
# As a hacky workaround, we are overriding the build_py() function
# based on this post:
#   http://stackoverflow.com/questions/17806485/execute-a-python-script-post-install-using-distutils-setuptools
class build_py(_build_py):
    def run(self):
        self.execute(compile_thrift_files, (), msg="Compiling Thrift modules")
        _build_py.run(self)


setup(
    name = "concrete",
    version = "2.0.5pre",
    packages = [
        'concrete',
        'concrete.audio',
        'concrete.communication',
        'concrete.discourse',
        'concrete.email',
        'concrete.entities',
        'concrete.language',
        'concrete.metadata',
        'concrete.situations',
        'concrete.spans',
        'concrete.structure',
        'concrete.twitter',
        ],
    package_dir = {'':'gen-py'},

    install_requires = ['thrift>=0.9.1'],

    url = "https://github.com/hltcoe/concrete",
    license="BSD",

    cmdclass={ 'build_py': build_py }
)
