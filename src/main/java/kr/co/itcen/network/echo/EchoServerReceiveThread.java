package kr.co.itcen.network.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class EchoServerReceiveThread extends Thread {
	private Socket socket;
	
	public EchoServerReceiveThread(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();			
		
		EchoServer.log("connected from client[" + inetRemoteSocketAddress.getAddress().getHostAddress() + ":" + inetRemoteSocketAddress.getPort()+"]");
					
		try {
			// 4. IOStream 받아오기
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			// 버퍼 보조스트림
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true);
			// true : 버퍼링 하지 말고 쓰는 즉시 보내라 - flush
			
			while(true) {
				// 5. 데이터 읽기(수신)
				String data = br.readLine();
				if(data == null) {
					EchoServer.log("closed by client");
					break;
				}
				EchoServer.log("received: "+ data);
				
				// 6. 데이터 쓰기(송신)
				pw.println(data);
			}
		} catch (SocketException e) {
			EchoServer.log("abnormal closed by client");	// 비정상 종료
		}catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 7. Socket 자원정리
			if(socket != null && !socket.isClosed()) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
