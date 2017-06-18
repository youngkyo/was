import httputils.HttpUtils;
import servlet.SimpleHttpRequest;
import servlet.SimpleHttpResponse;
import servlet.SimpleServlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Map;

class HttpWriter {
	private File rootDirectory;
	private String root;
	private Map<String, String> hosts;
	private Map<String, String> servlet;
	private String indexFileName;

	HttpWriter(File rootDirectory, String root, Map<String, String> hosts, Map<String, String> servlet, String indexFileName) {
		this.rootDirectory = rootDirectory;
		this.root = root;
		this.hosts = hosts;
		this.servlet = servlet;
		this.indexFileName = indexFileName;
	}

	void writeGetPage(OutputStream raw, Writer out, String[] tokens, String host)
		throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		String version = "";
		String fileName = tokens[1];
		if (fileName.endsWith("/"))
			fileName += indexFileName;
		String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
		if (tokens.length > 2) {
			version = tokens[2];
		}


		File theFile = new File(rootDirectory, this.hosts.get(host) + "/" + fileName.substring(1, fileName.length()));
		if (theFile.canRead() && theFile.getCanonicalPath().startsWith(root)) {
			byte[] theData = Files.readAllBytes(theFile.toPath());
			if (version.startsWith("HTTP/")) { // send a MIME header
				HttpUtils.sendHeader(out, "HTTP/1.0 200 OK", contentType, theData.length);
			}
			raw.write(theData);
			raw.flush();
		} else {
			SimpleHttpResponse response = new SimpleHttpResponse(out);
			SimpleHttpRequest request = new SimpleHttpRequest(fileName);
			if (this.servlet.containsKey(fileName)) {
				SimpleServlet servlet = (SimpleServlet) Class.forName(this.servlet.get(fileName)).newInstance();
				servlet.service(request, response);
			} else {
				String errorFileName = "404error.html";
				writeErrorPage(out, errorFileName, "HTTP/1.0 404 File Not Found");
			}
		}
	}

	void writeErrorPage(Writer out, String errorFileName, String responseCode) throws IOException {
		File errorFile = new File(rootDirectory, errorFileName);
		byte[] readFile = Files.readAllBytes(errorFile.toPath());
		HttpUtils.sendHeader(out, responseCode, "text/html; charset=utf-8", readFile.length);
		out.write(new String(readFile, "UTF-8"));
		out.flush();
	}
}
