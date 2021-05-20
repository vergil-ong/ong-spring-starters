package com.github.ong.grpc.marshaller;

import com.github.ong.util.ClassUtil;
import com.github.ong.util.StreamUtil;
import io.grpc.MethodDescriptor;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 16:48
 * @Description protobuf 的格式转换
 */
public class ProtoMarshaller implements MethodDescriptor.Marshaller<Object> {

    private Schema<Object> responseSchema;

    private Schema<Object> requestSchema;

    public ProtoMarshaller(Type type) {
        if (Objects.nonNull(type)){
            Type rawType = ClassUtil.getRawType(type);
            this.responseSchema = RuntimeSchema.getSchema((Class<Object>) rawType);
        }
    }

    @Override
    public InputStream stream(Object value) {
        if (value == null) {
            return new ByteArrayInputStream(new byte[]{});
        }

        if (requestSchema == null) {
            requestSchema = RuntimeSchema.getSchema((Class<Object>) value.getClass());
        }

        //这个地方要保证线程安全
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

        return new ByteArrayInputStream(ProtobufIOUtil.toByteArray(value, requestSchema, buffer));
    }

    @Override
    public Object parse(InputStream stream) {
        if (stream == null) {
            return new Object();
        }
        Object obj = responseSchema.newMessage();
        try {
            ProtobufIOUtil.mergeFrom(StreamUtil.copyToByteArray(stream), obj, responseSchema);
            return obj;
        } catch (IOException ignored) {
        }
        return new Object();
    }
}
