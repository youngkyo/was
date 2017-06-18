import httputils.HttpUtils;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestProcessor implements Runnable {
	private final static Logger logger = Logger.getLogger(RequestProcessor.class.getCanonicalName());
	private File rootDirectory;
	private String indexFileName;
	private Socket connection;
	private int BUF_SIZE = 8;
	private Map<String, String> hosts;
	private Map<String, String> servlet;
	private String root;

	public RequestProcessor(File rootDirectory, String indexFileName, HttpServerConfig httpServerConfig, Socket connection) {
		if (rootDirectory.isFile()) {
			throw new IllegalArgumentException(
				"rootDirectory must be a directory, not a file");
		}
		try {
			rootDirectory = rootDirectory.getCanonicalFile();
		} catch (IOException ex) {
			logger.log(Level.WARNING, "root Directory Invalid");
		}

		this.rootDirectory = rootDirectory;
		this.root = rootDirectory.getPath();
		if (indexFileName != null) {
			this.indexFileName = HttpUtils.INDEX_FILE_NAME;
		}

		this.connection = connection;
		this.hosts = httpServerConfig.getHosts();
		this.servlet = httpServerConfig.getServlet();
	}

	@Override
	public void run() {
		HttpWriter httpWriter = new HttpWriter(rootDirectory, root, hosts, servlet, indexFileName);
		try {
			OutputStream raw = new BufferedOutputStream(connection.getOutputStream());
			Writer out = new OutputStreamWriter(raw);
			Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()), "UTF-8");
			StringBuilder requestLine = new StringBuilder();

			char[] buffer = new char[BUF_SIZE];
			while (true) {
				int c = in.read(buffer);
				requestLine.append(buffer);
				if (c < BUF_SIZE)
					break;
			}

			String get = requestLine.toString();
			logger.info(connection.getRemoteSocketAddress() + " " + get);

			String[] tokens = get.split("\\s+");
			String method = tokens[0];
			String hosts = tokens[4];
			String host = hosts.split(":")[0];
			if (!HttpUtils.isSecurityValid(tokens[1])) {
				String errorFileName = "classes/error403.html";
				httpWriter.writeErrorPage(out, errorFileName, "HTTP/1.0 403 Forbidden");
				connection.close();
				return;
			}

			if (method.equals("GET")) {
				httpWriter.writeGetPage(raw, out, tokens, host);
			} else {
				String errorFileName = "classes/error501.html";
				httpWriter.writeErrorPage(out, errorFileName, "HTTP Error 501: Not Implemented");
			}
		} catch (IOException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
			logger.log(Level.WARNING, "Error talking to " + connection.getRemoteSocketAddress(), ex);
		} finally {
			try {
				connection.close();
			} catch (IOException ex) {

			}
		}
	}
}