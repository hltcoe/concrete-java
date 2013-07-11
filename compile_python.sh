mkdir -p concrete-python/target 
protoc --proto_path=./concrete-protobufs/src/main/proto/ --python_out=./concrete-python/target concrete-protobufs/src/main/proto/*.proto
cp -R ./concrete-python/src/* ./concrete-python/target/
