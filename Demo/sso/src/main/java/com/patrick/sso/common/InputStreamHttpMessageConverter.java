package com.patrick.sso.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

/**
 * 
 * @author Patrick Pan
 *
 */
public class InputStreamHttpMessageConverter extends AbstractHttpMessageConverter<InputStream> {

	public InputStreamHttpMessageConverter() {
		super(StandardCharsets.UTF_8, MediaType.APPLICATION_OCTET_STREAM);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return InputStream.class.isAssignableFrom(clazz);
	}

	@Override
	public InputStream readInternal(Class<? extends InputStream> clazz, HttpInputMessage inputMessage)
			throws IOException {
		return inputMessage.getBody();
	}

	@Override
	public void writeInternal(InputStream t, HttpOutputMessage outputMessage) throws IOException {
		byte[] bytes = new byte[1024];
		int hasRead = 0;
		OutputStream outputStream = outputMessage.getBody();
		while ((hasRead = t.read(bytes)) > 0) {
			outputStream.write(bytes, 0, hasRead);
		}
	}
}
