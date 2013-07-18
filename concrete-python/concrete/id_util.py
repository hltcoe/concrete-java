"""
Utility methods for converting UUIDs
"""

import concrete
import uuid as python_uuid

######################################################################
# UUIDs
######################################################################

class FrozenUUID(python_uuid.UUID):
    """
    An immutable copy of a rebar UUID.  FrozenUUIDs should be used in
    cases where you need to use UUIDs as keys in a dictionary or
    elements in a set.
    """
    def __new__(self, value):
        if isinstance(value, python_uuid.UUID):
            return value # reuse the same object (it's immutable)
        else:
            return python_uuid.UUID.__new__(self)
        
    def __init__(self, value):
        if isinstance(value, concrete.proto.UUID):
            python_uuid.UUID.__init__(self, int=((value.high << 64) + value.low))
        elif isinstance(value, python_uuid.UUID):
            assert self is value
        elif isinstance(value, basestring):
            python_uuid.UUID.__init__(self, value)
        else:
            raise TypeError("Expected a UUID: %r" % value)

    def copy_to(self, pb_uuid):
        """
        Copy the value of this frozen UUID into the given
        concrete.proto.UUID object.
        """
        pb_uuid.high = self.int >> 64
        pb_uuid.low = self.int & 0xffffffffffffffff

    def to_proto(self):
        uuid = concrete.proto.UUID()
        self.copy_to(uuid)
        return uuid

    def __cmp__(self, other):
        if not isinstance(other, FrozenUUID): return -1
        return cmp(self.int, other.int)

    def __repr__(self):
        return python_uuid.UUID.__str__(self)


def generate_uuid(pb_uuid=None):
    """
    Generate a random new UUID, and store it in the given
    `concrete.UUID` message (or in a newly created `concrete.UUID`
    message, if pb_uuid=None).  The given message should have type
    concrete.UUID.  Return the concrete.UUID object.
    """
    if pb_uuid is None:
        pb_uuid = concrete.proto.UUID()
    if not isinstance(pb_uuid, concrete.proto.UUID):
        raise TypeError('Arg 1: Expected a concrete.UUID')
    if pb_uuid.HasField('high'):
        raise ValueError('Arg 1: Already has a UUID')
    py_uuid = python_uuid.uuid4() # random UUID.
    pb_uuid.high = py_uuid.int >> 64
    pb_uuid.low = py_uuid.int & 0xffffffffffffffff
    return pb_uuid

