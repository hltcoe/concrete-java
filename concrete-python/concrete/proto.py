
import sys, os, stat, time

######################################################################
# Paths
######################################################################
def _add_to_python_path(p):
    if p not in sys.path: sys.path.append(p)

# Add the path for the concrete protoc bindings:
_here = os.path.abspath(os.path.split(__file__)[0])
_proto_py_path = os.path.join(_here, 'proto')
_add_to_python_path(_proto_py_path)

######################################################################
# Sanity Check -- Are python files up to date?
######################################################################
_NEED_TO_RECOMPILE_PROTOBUF = """Python protobuf interface out-of-date.
===========================================================================
You need to recompile the python probobuf interface from the *.proto
files.  To do so, please run:

   %% make all

---------------------------------------------------------------------------
    protobuf *.proto files last modified.... %s
    protobuf *.py files last modified....... %s
===========================================================================\
"""

_proto_src_path = os.path.join(_here, '..', '..', 'concrete-protobufs', 'src', 'main', 'proto')
def _get_mtime(directory, extension, combine):
    mtime = None
    for f in os.listdir(directory):
        if f.endswith(extension):
            p = os.path.join(directory, f)
            if mtime is None:
                mtime = os.stat(p)[stat.ST_MTIME]
            else:
                mtime = combine(mtime, os.stat(p)[stat.ST_MTIME])
            # print mtime, p
    return mtime

def _check_proto_defs():
    src_mtime = _get_mtime(_proto_src_path, '.proto', max)
    if not os.path.exists(_proto_py_path):
        raise ValueError(_NEED_TO_RECOMPILE_PROTOBUF %
                         (time.ctime(src_mtime),
                          '(not built yet)'))
    py_mtime = _get_mtime(_proto_py_path, '.py', min)
    # jar_mtime = _get_mtime(_proto_jar_path, 'rebar.jar', min)
    # if src_mtime >= py_mtime or src_mtime >= jar_mtime:
    if src_mtime >= py_mtime:
        raise ValueError(_NEED_TO_RECOMPILE_PROTOBUF % 
                         (time.ctime(src_mtime), 
                          time.ctime(py_mtime)))
                          # time.ctime(jar_mtime)))
_check_proto_defs()

######################################################################
# Protocol Buffer Definitions
######################################################################

# Use binary implementations, when possible.

### MAX - this is causing weird errors, I think
### turns out we need to install CPP support - uncomment when available

# os.environ['PROTOCOL_BUFFERS_PYTHON_IMPLEMENTATION'] = 'cpp'
# try: import _fast_concrete_proto
# except ImportError as e: 
#     print "Error: {0}".format(e)
#     print "WARNING - C++ Protobufs unavailable; defaulting to slow python ones"
#     pass # C++ protobufs not available; use pure python ones

from concrete_pb2 import *
from graph_pb2 import *

######################################################################
# Utility Function
######################################################################

def enum_name(message, field_name):
    value = getattr(message, field_name)
    enum = message.DESCRIPTOR.fields_by_name[field_name].enum_type
    return enum.values_by_number[value].name

