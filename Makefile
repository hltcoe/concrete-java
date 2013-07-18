usage:
	@echo "make protoc .......... build protocol buffer interfaces"
	@echo "make clean ........... delete buildfiles"

PROTOC=`which protoc`

protoc: protoc_concrete
protoc_concrete: concrete-protobufs/src/main/proto/.up_to_date
concrete-protobufs/src/main/proto/.up_to_date: $(wildcard concrete-protobufs/src/main/proto/*.proto)
	mkdir -p concrete-python/proto
	${PROTOC} concrete-protobufs/src/main/proto/*.proto \
		--proto_path=concrete-protobufs/src/main/proto \
		--python_out=concrete-python/proto
	touch concrete-protobufs/src/main/proto/.up_to_date

clean:
	$(RM) -rf concrete-python/proto
	$(RM) -rf concrete-protobufs/src/main/proto/.up_to_date

