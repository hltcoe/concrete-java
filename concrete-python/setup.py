from distutils.core import setup
from distutils.extension import Extension
import os, sys, re

concrete_root = os.path.abspath(os.path.join(os.path.dirname(__file__), '..'))
cpp_dir = os.path.join(concrete_root, 'concrete-python', 'cpp', 'proto')
proto_sources = [os.path.join(cpp_dir, f) for f in os.listdir(cpp_dir)
                 if f.endswith('.cc')]
proto_sources.append('concrete/fast_proto/fast_concrete_proto.c')
include_dirs=['/export/apps/include']
library_dirs=['/export/apps/lib']

setup(
    name='concrete',
    description='HLTCOE Concrete Core Python Package',
    version='1.1.8',
    packages=['concrete'],
    ext_modules=[Extension('concrete._fast_concrete_proto',
                           include_dirs=include_dirs,
                           library_dirs=library_dirs,
                           sources=proto_sources,
                           libraries=['protobuf'])],
    )
