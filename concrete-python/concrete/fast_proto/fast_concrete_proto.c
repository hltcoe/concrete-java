#include <Python.h>

static PyMethodDef ConcreteMethods[] = {
  {NULL, NULL, 0, NULL}        /* Sentinel */
};

PyMODINIT_FUNC
init_fast_concrete_proto(void)
{
  PyObject *m;

  m = Py_InitModule("_fast_concrete_proto", ConcreteMethods);
  if (m == NULL)
    return;
}
