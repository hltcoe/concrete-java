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

######################################################################
# Edge Identifier
######################################################################

class FrozenEdgeId(object):
    """
    A unique identifier for an undirected edge in a Graph, consisting
    of the pair of UUIDs for the two vertices that the edge connects.

    FrozenEdgeIds are immutable and hashable, and have a value-based
    equals method.
    """
    def __init__(self, arg1, arg2=None):
        # Extract v1 and v2 from arguments.
        if arg2 is None:
            if hasattr(arg1, 'v1'): # edge id
                v1 = FrozenUUID(arg1.v1)
                v2 = FrozenUUID(arg1.v2)
            elif hasattr(arg1, 'src'): # directed edge id
                v1 = FrozenUUID(arg1.src)
                v2 = FrozenUUID(arg1.dst)
            elif hasattr(arg1, 'edge_id'): # edge
                v1 = FrozenUUID(arg1.edge_id.v1)
                v2 = FrozenUUID(arg1.edge_id.v2)
            else:
                raise TypeError('Expected two uuids or one EdgeId')
        else:
            if isinstance(arg1, (concrete.proto.Vertex, concrete.IndexedVertex)):
                arg1 = arg1.uuid
            if isinstance(arg2, (concrete.proto.Vertex, concrete.IndexedVertex)):
                arg2 = arg2.uuid
            if not (isinstance(arg1, (FrozenUUID, concrete.proto.UUID)) and
                    isinstance(arg2, (FrozenUUID, concrete.proto.UUID))):
                raise TypeError('Expected two uuids or one EdgeId')
            v1 = FrozenUUID(arg1)
            v2 = FrozenUUID(arg2)
        # Save them.
        if v1 <= v2:
            self._v1 = v1
            self._v2 = v2
        else:
            self._v1 = v2
            self._v2 = v1

    v1 = property(lambda self:self._v1)
    v2 = property(lambda self:self._v2)

    def to_proto(self):
        edge_id = concrete.proto.EdgeId()
        self.copy_to(edge_id)
        return edge_id

    def copy_to(self, edge_id):
        self.v1.copy_to(edge_id.v1)
        self.v2.copy_to(edge_id.v2)

    def __repr__(self):
        return '%s--%s' % (self._v1, self._v2)

    def _key(self):
        return (self._v1, self._v2)
    
    def __cmp__(self, other):
        if not isinstance(other, FrozenEdgeId): return -1
        return cmp(self._key(), other._key())

    def __hash__(self):
        return hash(self._key())
        
######################################################################
# Directed Edge Identifier
######################################################################

class FrozenDirectedEdgeId(object):
    """
    A unique identifier for an directed edge in a Graph, consisting of
    a source UUID and destination UUID for the two vertices that the
    edge connects.

    FrozenDirectedEdgeIds are immutable and hashable, and have a
    value-based equals method.
    """
    def __init__(self, arg1, arg2=None):
        # Extract src and dst from arguments.
        if arg2 is None:
            if hasattr(arg1, 'v1'): # edge id
                src = FrozenUUID(arg1.v1)
                dst = FrozenUUID(arg1.v2)
            elif hasattr(arg1, 'src'): # directed edge id
                src = FrozenUUID(arg1.src)
                dst = FrozenUUID(arg1.dst)
            elif hasattr(arg1, 'edge_id'): # edge
                src = FrozenUUID(arg1.edge_id.v1)
                dst = FrozenUUID(arg1.edge_id.v2)
            else:
                raise TypeError('Expected two uuids or one EdgeId')
        else:
            if isinstance(arg1, (concrete.proto.Vertex, concrete.IndexedVertex)):
                arg1 = arg1.uuid
            if isinstance(arg2, (concrete.proto.Vertex, concrete.IndexedVertex)):
                arg2 = arg2.uuid
            if not (isinstance(arg1, (FrozenUUID, concrete.proto.UUID)) and
                    isinstance(arg2, (FrozenUUID, concrete.proto.UUID))):
                raise TypeError('Expected two uuids or one EdgeId')
            src = FrozenUUID(arg1)
            dst = FrozenUUID(arg2)
        # Save them.
        self._src = src
        self._dst = dst

    src = property(lambda self:self._src)
    dst = property(lambda self:self._dst)

    @property
    def direction(self):
        if self._src <= self._dst:
            return concrete.proto.DirectedEdgeId.V1_TO_V2
        else:
            return concrete.proto.DirectedEdgeId.V2_TO_V1

    def reversed(self):
        return FrozenDirectedEdgeId(self.dst, self.src)

    def to_proto(self):
        edge_id = concrete.proto.DirectedEdgeId()
        self.copy_to(edge_id)
        return edge_id

    def to_edge_id(self):
        return FrozenEdgeId(self._src, self._dst)

    def copy_to(self, edge_id):
        self.src.copy_to(edge_id.src)
        self.dst.copy_to(edge_id.dst)

    def __repr__(self):
        return '%s->%s' % (self._src, self._dst)

    def _key(self):
        return (self._src, self._dst)
    
    def __cmp__(self, other):
        if not isinstance(other, FrozenDirectedEdgeId): return -1
        return cmp(self._key(), other._key())

    def __hash__(self):
        return hash(self._key())
        
