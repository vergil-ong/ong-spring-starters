package com.github.ong.grpc.interceptor;

import com.github.ong.grpc.interceptor.server.ProviderServerInterceptor;
import com.github.ong.log.constant.LogConst;
import com.github.ong.log.util.LogUtil;
import com.github.ong.util.RandomUtil;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @date   2021/5/19 15:47
 * @Description 日志 拦截器
 */
@Slf4j
public class ServerLogInterceptor implements ProviderServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        Metadata.Key<String> reqTraceKey = Metadata.Key.of(LogConst.GRPC_REQ_KEY_TRACE_ID, Metadata.ASCII_STRING_MARSHALLER);

        String traceId = headers.get(reqTraceKey);
        if (Objects.isNull(traceId)){
            traceId = RandomUtil.getUUID();
        }
        final String logTraceId = traceId;
        LogUtil.replaceTraceId(logTraceId);

        MethodDescriptor<ReqT, RespT> methodDescriptor = call.getMethodDescriptor();

        ForwardingServerCall<ReqT, RespT> forwardingServerCall = new ForwardingServerCall<ReqT, RespT>() {

            @Override
            public MethodDescriptor<ReqT, RespT> getMethodDescriptor() {
                return methodDescriptor;
            }

            @Override
            protected ServerCall<ReqT, RespT> delegate() {
                return call;
            }

            @Override
            public void sendMessage(RespT message) {
                log.info("send message {}", message);
                super.sendMessage(message);
            }

            @Override
            public void sendHeaders(Metadata headers) {
                headers.put(reqTraceKey, logTraceId);
                super.sendHeaders(headers);
            }
        };

        return next.startCall(forwardingServerCall, headers);
    }
}
