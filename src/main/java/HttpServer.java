import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServer {
	private static final Logger logger = Logger.getLogger(HttpServer.class.getCanonicalName());
	private static final int NUM_THREADS = 50;
	private static final String INDEX_FILE = "index.html";
	private final File rootDirectory;
	private final int port;
	private final HttpServerConfig httpServerConfig;

	public HttpServer(HttpServerConfig config) throws IOException {
		this.rootDirectory = new File("" + config.getRootDir());
		this.port = config.getPort();
		httpServerConfig = config;
	}

	public void start() throws IOException {
		ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
		try (ServerSocket server = new ServerSocket(port)) {
			logger.info("Accepting connections on port " + server.getLocalPort());
			logger.info("Document Root: " + rootDirectory);
			while (true) {
				try {
					Socket request = server.accept();
					Runnable r = new RequestProcessor(rootDirectory, INDEX_FILE, httpServerConfig, request);
					pool.submit(r);
				} catch (IOException ex) {
					logger.log(Level.WARNING, "Error accepting connection", ex);
				}
			}
		}
	}

	public static void main(String[] args) {
		HttpServerConfig config = null;
		Gson gson = new Gson();
		try {
			JsonReader jsonReader = new JsonReader(new FileReader("classes/config.json"));
			config = gson.fromJson(jsonReader, HttpServerConfig.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			HttpServer webServer = new HttpServer(config);
			webServer.start();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Server could not start", ex);
		}
	}
}
