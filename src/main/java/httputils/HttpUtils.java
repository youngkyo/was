package httputils;

import java.io.IOException;
import java.io.Writer;
import java.net.URLConnection;
import java.util.Date;

public class HttpUtils {
	public static String INDEX_FILE_NAME = "index.html";

	public static void sendHeader(Writer out, String responseCode, String contentType, int length)
		throws IOException {
		out.write(responseCode + "\r\n");
		Date now = new Date();
		out.write("Content-length: " + length + "\r\n");
		out.write("Date: " + now + "\r\n");
		out.write("Server: JHTTP 2.0\r\n");
		out.write("Content-type: " + contentType + "\r\n\r\n");
		out.flush();
	}

	public static String getContentType(String fileName) {
		return URLConnection.getFileNameMap().getContentTypeFor(fileName);
	}

	public static boolean isSecurityValid(String token) {
		String executeRegexp = "^\\S+.(exe)$";
		String rootRegExp = "\\.\\.";

		if (token.matches(executeRegexp) || token.matches(rootRegExp)) {
			return false;
		}

		return true;
	}
}
