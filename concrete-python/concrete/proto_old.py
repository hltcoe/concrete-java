
import sys, os, stat, time

######################################################################
# Paths
######################################################################
def _add_to_python_path(p):
    if p not in sys.path: sys.path.append(p)

# Add the path for the rebar protoc bindings:
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
# _proto_jar_path = os.path.join(_here, '..', '..', '..', '..', 'target')
def _get_mtime(directory, extension, combine):
    mtime = None
    for f in os.listdir(directory):
        if f.endswith(extension):
            p = os.path.join(directory, f)
            if mtime is None:
                mtime = os.stat(p)[stat.ST_MTIME]
            else:
                mtime = combine(mtime, os.stat(p)[stat.ST_MTIME])
            #print mtime, p
    return mtime

def _check_proto_defs():
    src_mtime = _get_mtime(_proto_src_path, '.proto', max)
    if not os.path.exists(_proto_py_path):
        raise ValueError(_NEED_TO_RECOMPILE_PROTOBUF %
                         (time.ctime(src_mtime),
                          '(not built yet'))
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

# os.environ['PROTOCOL_BUFFERS_PYTHON_IMPLEMENTATION'] = 'cpp'
# try: import _fast_rebar_proto
# except: pass # C++ protobufs not available; use pure python ones

from concrete_pb2 import *
import concrete_rpc_pb2 as rpc

######################################################################
# Utility Function
######################################################################

def enum_name(message, field_name):
    value = getattr(message, field_name)
    enum = message.DESCRIPTOR.fields_by_name[field_name].enum_type
    return enum.values_by_number[value].name

######################################################################
# Patch Bug
######################################################################
# This bug (basically just a simple missing import) has been fixed in
# the latest version of protobuf, but not in the version we're using.
# So check for it and monkey-patch it if necessary.

### MAX - edited this out - no idea what it's doing
# import google.protobuf.internal.cpp_message
# if not hasattr(google.protobuf.internal.cpp_message, 'text_format'):
#     import google.protobuf.text_format
#     google.protobuf.internal.cpp_message.text_format = google.protobuf.text_format

######################################################################
# Patch Bug
######################################################################
# This bug is recorded as issue 434, and has not yet been fixed:
#    <http://code.google.com/p/protobuf/issues/detail?id=434>
#
# If a root protobuf object is deleted but a reference still exists to
# a child object, then any accesses to the child object will cause
# undefined behavior (including segmentation fault).  This fix avoids
# the problem by adding a pointer from each child composite object to
# its parent, ensuring that the parent will not be delted until both
# it and the child are unreachable.  Note that this makes the GC's job
# a little harder, since we are creating a cycle -- it's possible that
# we should use a weakref here instead.  In terms of implementation,
# the only change is the addition of the line marked with "(**)".

### MAX - killed this
# import google.protobuf.internal.cpp_message
# def _bug434_CompositeProperty(cdescriptor, message_type):
#     def Getter(self):
#         sub_message = self._composite_fields.get(cdescriptor.name, None)
#         if sub_message is None:
#             cmessage = self._cmsg.NewSubMessage(cdescriptor)
#             sub_message = message_type._concrete_class(__cmessage=cmessage,
#                                                        __owner=self) # (**)
#             self._composite_fields[cdescriptor.name] = sub_message
#         return sub_message
#     return property(Getter)
# google.protobuf.internal.cpp_message.CompositeProperty = _bug434_CompositeProperty
