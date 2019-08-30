package kr.co.itcen.networck.echo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	private static final int PORT = 8000;
	
	public static void main(String[] args) {
		
		ServerSocket serverSocket = null;
		
		try {
			// 1. 서버소켓 생성
			serverSocket = new ServerSocket();
			
			// 2. Binding : Socket 에 SocketAddress(IPAddress + Port) 바인딩
			InetAddress inetAddress = InetAddress.getLocalHost();
			InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, PORT);
			serverSocket.bind(inetSocketAddress);
			log("binding " + inetAddress.getHostAddress() + ":" + PORT);
			
			// 3. accept : 클라이언트로부터 연결요청 (Connect)을 기다린다. - blocking
			while(true) {
				Socket socket = serverSocket.accept();
				// connect 들어오면 accept - Thread 객체 만들고 생성된 Socket Thread 에 넘겨주고 start() : 
				new EchoServerReceiveThread(socket).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 8. Server Socket 자원정리
			try {
				if(serverSocket != null && !serverSocket.isClosed()) {
					serverSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void log(String log) {
		System.out.println("[Echo Server#"+ Thread.currentThread().getId() +"] "+ log);
		/*
		main thread id: 1
		[Echo Server#1] binding 192.168.1.32:8000
		[Echo Server#10] connected from client[192.168.1.32:50825]
		[Echo Server#11] connected from client[192.168.1.32:50840]
		*/
	}
}
