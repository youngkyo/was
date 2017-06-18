package servlet;

public class SimpleHttpRequest {
	private String fileName;
	public SimpleHttpRequest(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return this.fileName;
	}
}
