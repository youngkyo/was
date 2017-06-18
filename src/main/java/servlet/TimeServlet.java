package servlet;

import httputils.HttpUtils;

import java.io.IOException;
import java.io.Writer;
import java.net.URLConnection;
import java.time.LocalDateTime;

public class TimeServlet implements SimpleServlet {
	@Override
	public void service(SimpleHttpRequest request, SimpleHttpResponse response) {

		Writer writer = response.getWriter();
		try {
			String contentType = URLConnection.getFileNameMap().getContentTypeFor(request.getFileName());
			HttpUtils.sendHeader(writer, "HTTP/1.0 200 OK", contentType, getTime().length());
			writer.write(getTime());
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String getTime() {
		return LocalDateTime.now().toString();
	}
}
