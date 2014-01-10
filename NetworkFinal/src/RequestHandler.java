import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.Position;

public class RequestHandler extends Thread {
	private final static Logger log = Logger.getLogger(RequestHandler.class
			.getName());

	private static final String DEFAULT_WEB_ROOT = "testFolder";
	private static final String DEFAULT_FILE_PATH = "index.html";

	private Socket connection;

	private FileInputStream fis;

	public RequestHandler(Socket connection) {
		this.connection = connection;
	}

	public void run() {
		// logger? 효과적으로 로그를 남길 수 있음.
		log.log(Level.INFO, "WebServer Thread Created!");
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
		DataOutputStream dos = null;

		try {
			// 사용자 요청 및 응답
			br = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			dos = new DataOutputStream(connection.getOutputStream());

			// get first line
			// connection이 여러개가 생기는 바람에 header==null 인 경우가 발생.
			String methodLine = br.readLine();
			System.out.println("method Line: " + methodLine);

			StringTokenizer tokenizedLine = new StringTokenizer(methodLine);
			String method = tokenizedLine.nextToken();
			String fileName = tokenizedLine.nextToken();
			System.out.println(fileName);
			int contentLength = 0;

			if (method.equals("POST")) {
				String headLine = br.readLine();
				while (!(headLine.equals("")) && !(headLine.equals(null))) {
					System.out.println("HEADLINE");
					if (headLine.contains("Content-Length")) {
						contentLength = Integer.parseInt(headLine
								.substring("Content-Length: ".length()));
					}
					System.out.println(headLine);
					headLine = br.readLine();
				}
				System.out.println(contentLength);

				char[] receivedBody = new char[contentLength];
				br.read(receivedBody, 0, contentLength);
				String receivedBodyString = "";

				for (int i = 0; i < contentLength; i++) {
					receivedBodyString += receivedBody[i];
				}

				System.out.println(receivedBodyString);

				String id = receivedBodyString.substring(
						receivedBodyString.indexOf("=") + 1,
						receivedBodyString.indexOf("&"));
				String passwd = receivedBodyString.substring(
						receivedBodyString.lastIndexOf("=") + 1,
						receivedBodyString.length());

				System.out.println("id : " + id + "\npw : " + passwd);

				if (fileName.startsWith("/")) {
					if (fileName.length() > 1) { // index가 아닌 경우.
						fileName = DEFAULT_WEB_ROOT + File.separator
								+ fileName.substring(1); // '/'를 제거한 파일명 리턴.
					} else {
						fileName = DEFAULT_WEB_ROOT + File.separator
								+ DEFAULT_FILE_PATH; // '/'만 있는 경우 index 리턴.
					}
				}

				File file = new File(fileName);

				// 리턴 할 파일의 타입.
				String mimeType = null;

				String extArr[] = fileName.split("\\.");

				if (extArr.length >= 2) {
					String ext = extArr[1];
					if (ext.equals("html")) {
						mimeType = "text/html";
					} else if (ext.equals("jpg")) {
						mimeType = "image/jpeg";
					}
				} else {
					mimeType = "text/html";
				}

				if (file.exists()) {
					int numOfBytes = (int) file.length();

					FileInputStream fis = new FileInputStream(fileName);
					byte[] fileBytesArray = new byte[numOfBytes];
					fis.read(fileBytesArray);

					// 정상처리 된 경우 ACK 200 출력.
					dos.writeBytes("HTTP/1.0 200 Document Follows \r\n");
					dos.writeBytes("Content-Type: " + mimeType + "\r\n");
					

					// 출력할 컨텐츠의 길이.
					dos.writeBytes("Content-Length: " + numOfBytes + "\r\n");
					// 메타 정보의 종료.
					dos.writeBytes("\r\n");

					// 요청 파일 출력.
					// Parameter: data, start offset, length
					dos.write(fileBytesArray, 0, numOfBytes);
				} else {
					System.out.println("Requested File doesn't exist: "
							+ fileName);

					dos.writeBytes("HTTP/1.0 404 Not Found \r\n");
					dos.writeBytes("Connection: close \r\n");
					dos.writeBytes("\r\n");
				}

			}

			else if (method.equals("GET")) {
				// 파일명 읽기
				if (fileName.startsWith("/")) {
					if (fileName.length() > 1) { // index가 아닌 경우.
						fileName = DEFAULT_WEB_ROOT + File.separator
								+ fileName.substring(1); // '/'를 제거한 파일명 리턴.
					} else {
						fileName = DEFAULT_WEB_ROOT + File.separator
								+ DEFAULT_FILE_PATH; // '/'만 있는 경우 index 리턴.
					}
				}

				File file = new File(fileName);

				// 리턴 할 파일의 타입.
				String mimeType = null;

				String extArr[] = fileName.split("\\.");

				if (extArr.length >= 2) {
					String ext = extArr[1];
					if (ext.equals("html")) {
						mimeType = "text/html";
					} else if (ext.equals("jpg")) {
						mimeType = "image/jpeg";
					}
				} else {
					mimeType = "text/html";
				}

				if (file.exists()) {
					int numOfBytes = (int) file.length();

					FileInputStream fis = new FileInputStream(fileName);
					byte[] fileBytesArray = new byte[numOfBytes];
					fis.read(fileBytesArray);

					// 정상처리 된 경우 ACK 200 출력.
					dos.writeBytes("HTTP/1.0 200 Document Follows \r\n");
					dos.writeBytes("Content-Type: " + mimeType + "\r\n");

					// 출력할 컨텐츠의 길이.
					dos.writeBytes("Content-Length: " + numOfBytes + "\r\n");
					// 메타 정보의 종료.
					dos.writeBytes("\r\n");

					// 요청 파일 출력.
					// Parameter: data, start offset, length
					dos.write(fileBytesArray, 0, numOfBytes);
				} else {
					System.out.println("Requested File doesn't exist: "
							+ fileName);

					dos.writeBytes("HTTP/1.0 404 Not Found \r\n");
					dos.writeBytes("Connection: close \r\n");
					dos.writeBytes("\r\n");
				}
			} else {
				System.out.println("Bad Request");
				dos.writeBytes("HTTP/1.0 400 Bad Request Message \r\n");
				dos.writeBytes("Connection: close \r\n");
				dos.writeBytes("\r\n");
			}

			connection.close();
			System.out.println("Connection Closed");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}