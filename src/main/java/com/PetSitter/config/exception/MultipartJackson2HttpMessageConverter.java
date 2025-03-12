package com.PetSitter.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

/**
 * 전송받은 파라미터들의 Content-type이 올바르지 않은 타입인 ‘application/octet-stream’으로 들어오는 경우
 * HttpMediaTypeNotSupportedException: Content type 'application/octet-stream is not supported 에러 발생
 * Request DTO: "ex)Content-type: application/json, MultiPartFile: ex)Content-type: image/png
 * 이렇게 따로따로 각각 들어오게 되는데, 스웨거의 경우 파라미터마다 Content type을 지정할 수 없기 때문에
 * HttpMessageConverter나 HttpMessageConverter를 구현하는 커스텀 컨버터 컴포넌트를 추가해야 한다.
 * 그리고 ‘application/octet-stream’ 사용을 비활성화함으로서, 에러를 방지할 수 있다.
 * 실제로 구현할 클래스는 AbstractJackson2HttpMessageConverter로 충분하다.
 * 본 문제가 해결되더라도 파라미터 별로 Content type을 지정할 수 있는 경우에는 올바른 사용법대로 파라미터별로 Content type을 지정하는 것이 좋다.
 */
@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canWrite(MediaType mediaType) {
        return false;
    }
}
