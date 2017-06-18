import java.util.Map;

class HttpServerConfig {
	private Map<String, String> hosts;
	private Map<String, String> servlet;
	private String rootDir;
	private int port;

	String getRootDir() {
		return rootDir;
	}

	int getPort() {
		return port;
	}

	Map<String, String> getHosts() {
		return hosts;
	}
	Map<String, String> getServlet() { return servlet;}
}
