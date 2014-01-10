import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebServer {
	private final static Logger log = Logger.getLogger(WebServer.class.getName());
	
	public static void main(String[] args) throws IOException {
		  ServerSocket listenSocket = new ServerSocket(8080, 5);
		  
	        log.log(Level.INFO, "WebServer Socket Created");
	        
	        Socket connection;
	        while ((connection = listenSocket.accept()) != null) {
	        	RequestHandler requestHandler = new RequestHandler(connection);
	            requestHandler.start();
	        }
	        System.out.println("END");
	}
}

//RFC-2616