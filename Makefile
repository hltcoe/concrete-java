usage:
	@echo "make protoc .......... build protocol buffer interfaces"
	@echo "make clean ........... delete buildfiles"

PROTOC=`which protoc`

protoc: protoc_concrete
protoc_concrete: concrete-protobufs/src/main/proto/.up_to_date
concrete-protobufs/src/main/proto/.up_to_date: $(wildcard concrete-protobufs/src/main/proto/*.proto)
	mkdir -p concrete-python/cpp/proto concrete-python/concrete/proto
	rm -f concrete-python/concrete/_fast_concrete_proto.so
	${PROTOC} concrete-protobufs/src/main/proto/*.proto \
		--proto_path=concrete-protobufs/src/main/proto \
		--python_out=concrete-python/concrete/proto \
		--cpp_out=concrete-python/cpp/proto
	(cd concrete-python; python setup.py build)
	cp concrete-python/build/lib.*/concrete/_fast_concrete_proto.so concrete-python/concrete
	rm -rf concrete-python/build
	touch concrete-protobufs/src/main/proto/.up_to_date

clean:
	$(RM) -rf concrete-python/cpp/proto
	$(RM) -rf concrete-python/concrete/proto/
	$(RM) -rf concrete-python/build
	$(RM) -rf concrete-protobufs/src/main/proto/.up_to_date

