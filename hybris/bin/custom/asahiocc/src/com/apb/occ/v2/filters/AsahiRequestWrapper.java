package com.apb.occ.v2.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import jakarta.servlet.ReadListener;


public class AsahiRequestWrapper extends HttpServletRequestWrapper {

    private StringBuilder _body = new StringBuilder();
    private static final String NEW_LINE = "\n";

	public AsahiRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		BufferedReader bufferedReader = request.getReader();			
		String line;
		while ((line = bufferedReader.readLine()) != null){
			_body.append(line).append(NEW_LINE);
		}
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.toString().getBytes());
		return new ServletInputStream() {
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
			
			@Override
			public boolean isFinished()
			{
				return byteArrayInputStream.available() == 0;
			}

			@Override
			public boolean isReady()
			{
				return true;
			}

			@Override
			public void setReadListener(final ReadListener readListener)
			{
				throw new UnsupportedOperationException("ServletInputStream.setReadListener not implemented");
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
}