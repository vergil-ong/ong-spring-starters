package com.github.ong.grpc.interceptor;

import com.github.ong.log.constant.LogConst;
import com.github.ong.log.util.LogUtil;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/6 17:31
 * @Description 日志拦截器
 */
@Slf4j
public class ClientLogInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        String traceId = LogUtil.setAndGetTraceId();

        Metadata.Key<String> reqTraceKey = Metadata.Key.of(LogConst.GRPC_REQ_KEY_TRACE_ID, Metadata.ASCII_STRING_MARSHALLER);
        ClientCall<ReqT, RespT> newCall = next.newCall(method, callOptions);

        return new ForwardingClientCall<ReqT, RespT>() {
            @Override
            protected ClientCall<ReqT, RespT> delegate() {
                return newCall;
            }

            @Override
            public void sendMessage(ReqT message) {
                log.info("send message is {}", message);
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                Listener<RespT> forwardListener = new ForwardingClientCallListener<RespT>() {
                    @Override
                    protected Listener<RespT> delegate() {
                        return responseListener;
                    }

                    @Override
                    public void onMessage(RespT message) {
                        log.info("response msg is {}", message);
                        super.onMessage(message);
                    }

                    @Override
                    public void onHeaders(Metadata headers) {
                        String traceToken = headers.get(reqTraceKey);
                        if (StringUtils.isNoneBlank(traceToken)){
                            LogUtil.replaceTraceId(traceToken);
                        } else {
                            LogUtil.replaceTraceId(traceId);
                        }

                        log.debug("response header token {}", traceToken);
                        super.onHeaders(headers);
                    }
                };

                headers.put(reqTraceKey, traceId);
                super.start(forwardListener, headers);
            }
        };
    }
}
