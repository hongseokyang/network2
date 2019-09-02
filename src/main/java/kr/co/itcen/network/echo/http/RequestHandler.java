package kr.co.itcen.network.echo.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;

public class RequestHandler extends Thread {
	private static String documentRoot = "";
	static {
		documentRoot = RequestHandler.class.getClass().getResource("/webapp").getPath();
	}
	
	private Socket socket;
	
	public RequestHandler( Socket socket ) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			// get IOStream
			OutputStream outputStream = socket.getOutputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
			// logging Remote Host IP Address & Port
			InetSocketAddress inetSocketAddress = ( InetSocketAddress )socket.getRemoteSocketAddress();
			consoleLog( "connected from " + inetSocketAddress.getAddress().getHostAddress() + ":" + inetSocketAddress.getPort() );
					
			String request = null;
			while(true) {
				String line = br.readLine();
				
				// 브라우저가 연결을 끊으면
				if(line == null) {
					break;
				}
				
				// header만 읽음
				if("".equals(line)) {
					break;
				}
				
				if(request == null) {
					request = line;
					break;
				}
				
			}
			String[] tokens = request.split(" ");
			if("GET".equals(tokens[0])) {
				consoleLog("request:"+request);
				reponseStaticResource(outputStream, tokens[1], tokens[2], " 200 OK");
			} else { // POST, PUT, DELETE 명령은 무시
				consoleLog("bad request:"+request);
				response400Error(outputStream, tokens[2]);
			}
			
			
		} catch( Exception ex ) {
			consoleLog( "error:" + ex );
		} finally {
			// clean-up
			try{
				if( socket != null && socket.isClosed() == false ) {
					socket.close();
				}
				
			} catch( IOException ex ) {
				consoleLog( "error:" + ex );
			}
		}			
	}
	
	private void reponseStaticResource(OutputStream outputStream, String url, String protocol, String msg) throws IOException {
		if("/".equals(url)) {
			url = "/index.html";
		}
		
		File file = new File(documentRoot+url);
		if(!file.exists()) {
			response404Error(outputStream, protocol);
			return;
		} 
		// nio
		byte[] body = Files.readAllBytes(file.toPath());
		String contentType = Files.probeContentType(file.toPath());
		
		// 응답
		outputStream.write( (protocol+msg+"\r\n").getBytes( "UTF-8" ) );	// 헤더
		outputStream.write( ("Content-Type:"+contentType+"; charset=utf-8\r\n").getBytes( "UTF-8" ) );	
		outputStream.write( "\r\n".getBytes() );	// 공백 : header body 구분
		outputStream.write( body );					// body
	}
	
	private void response404Error(OutputStream outputStream, String protocol) throws IOException {
		String url = "/error/404.html";
		reponseStaticResource(outputStream, url, protocol, " 404 File Not Found");
	}
	
	private void response400Error(OutputStream outputStream, String protocol) throws IOException {
		String url = "/error/400.html";
		reponseStaticResource(outputStream, url, protocol, " 400 Bad Request");
	}
	
	public void consoleLog( String message ) {
		System.out.println( "[RequestHandler#" + getId() + "] " + message );
	}

}
