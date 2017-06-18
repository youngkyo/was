package servlet;

import java.io.Writer;

public class SimpleHttpResponse {
	private Writer writer;

	public SimpleHttpResponse(Writer writer) {
		this.writer = writer;
	}

	public Writer getWriter() {
		return this.writer;
	}
}
