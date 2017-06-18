package servlet;

import httputils.HttpUtils;
import java.io.IOException;
import java.io.Writer;

public class Hello implements SimpleServlet {
	@Override
	public void service(SimpleHttpRequest request, SimpleHttpResponse response) {
		Writer writer = response.getWriter();
		try {
			String contentType = HttpUtils.getContentType(request.getFileName());
			HttpUtils.sendHeader(writer, "HTTP/1.0 200 OK", contentType, request.getFileName().length());
			writer.write(request.getFileName());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
