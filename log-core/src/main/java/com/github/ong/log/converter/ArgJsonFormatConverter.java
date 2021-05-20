package com.github.ong.log.converter;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.github.ong.log.util.LogLayoutUtil;
import org.slf4j.helpers.MessageFormatter;

import java.util.stream.Stream;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/5/12 16:09
 * @Description 参数转换
 */
public class ArgJsonFormatConverter extends MessageConverter {

    @Override
    public String convert(ILoggingEvent event) {

        try {
            return MessageFormatter.arrayFormat(
                    event.getMessage(),
                    Stream.of(event.getArgumentArray())
                            .map(LogLayoutUtil::logObject)
                            .toArray())
                    .getMessage();
        }catch (Exception e){
            return event.getMessage();
        }
    }
}
