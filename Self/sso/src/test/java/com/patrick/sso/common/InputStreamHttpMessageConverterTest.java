package com.patrick.sso.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;

/**
 * 
 * @author Patrick Pan
 *
 */
public class InputStreamHttpMessageConverterTest {

	private static final String FILE_NAME = String.format("tmp/%s.txt", (new Date()).getTime());
	private InputStreamHttpMessageConverter inputStreamHttpMessageConverter;

	@Before
	public void beforeTest() {
		inputStreamHttpMessageConverter = new InputStreamHttpMessageConverter();
	}

	@Test
	public void testSupports0() {
		assertTrue(inputStreamHttpMessageConverter.supports(InputStream.class));
	}

	@Test
	public void testSupports1() {
		assertTrue(inputStreamHttpMessageConverter.supports(FileInputStream.class));
	}

	@Test
	public void testSupports2() {
		assertFalse(inputStreamHttpMessageConverter.supports(InputStreamHttpMessageConverterTest.class));
	}

	@Test
	public void testReadInternal0() throws IOException {
		HttpInputMessage inputMessage = mock(HttpInputMessage.class);
		InputStream inputStream = mock(InputStream.class);
		when(inputMessage.getBody()).thenReturn(inputStream);
		assertEquals(inputStream, inputStreamHttpMessageConverter.readInternal(InputStream.class, inputMessage));
	}

	@Test(expected = IOException.class)
	public void testReadInternal1() throws IOException {
		HttpInputMessage inputMessage = mock(HttpInputMessage.class);
		when(inputMessage.getBody()).thenThrow(new IOException("Unit Test"));
		inputStreamHttpMessageConverter.readInternal(InputStream.class, inputMessage);
	}

	@Test
	public void testWriteInternal0() throws IOException {
		try (InputStream inputStream = InputStreamHttpMessageConverterTest.class.getClassLoader()
				.getResourceAsStream("InputStreamHttpMessageConverterTest.txt");
				OutputStream outputStream = new FileOutputStream(FILE_NAME)) {
			HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
			when(outputMessage.getBody()).thenReturn(outputStream);
			inputStreamHttpMessageConverter.writeInternal(inputStream, outputMessage);
		}
	}

	@Test(expected = IOException.class)
	public void testWriteInternal1() throws IOException {
		HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
		when(outputMessage.getBody()).thenThrow(new IOException("Unit Test"));
		inputStreamHttpMessageConverter.writeInternal(mock(InputStream.class), outputMessage);
	}

	@Test(expected = IOException.class)
	public void testWriteInternal2() throws IOException {
		InputStream inputStream = mock(InputStream.class);
		when(inputStream.read(any((new byte[] {}).getClass()))).thenThrow(new IOException("Unit Test"));
		inputStreamHttpMessageConverter.writeInternal(inputStream, mock(HttpOutputMessage.class));
	}

	@Test(expected = IOException.class)
	public void testWriteInternal3() throws IOException {
		OutputStream outputStream = mock(OutputStream.class);
		doThrow(new IOException("Unit Test")).when(outputStream).write(any((new byte[] {}).getClass()),
				any(Integer.class), any(Integer.class));
		HttpOutputMessage outputMessage = mock(HttpOutputMessage.class);
		when(outputMessage.getBody()).thenReturn(outputStream);
		try (InputStream inputStream = InputStreamHttpMessageConverterTest.class.getClassLoader()
				.getResourceAsStream("InputStreamHttpMessageConverterTest.txt")) {
			inputStreamHttpMessageConverter.writeInternal(inputStream, outputMessage);
		}
	}
}
