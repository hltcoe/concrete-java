mkdir concrete-python/target 2>/dev/null
protoc --proto_path=./concrete-protobufs/src/main/proto/ --python_out=./concrete-python/target concrete-protobufs/src/main/proto/concrete* 
cp -R ./concrete-python/src/* ./concrete-python/target/