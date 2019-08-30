package kr.co.itcen.networck.echo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
	private static String SERVER_IP = "192.168.1.32";
	private static int SERVER_PORT = 8000;
	
	public static void main(String[] args) {
		Socket socket = null;
		Scanner sc = null;

		try {
			// 1. 소켓생성
			socket = new Socket();
			
			// 2. 서버연결
			InetSocketAddress inetSocketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
			socket.connect(inetSocketAddress);

			log("connected");
			
			// 3. IOStream 생성하기			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
			
			sc = new Scanner(System.in);
			String line = null;
			while(true) {
				System.out.print(">>");
				if("exit".equals(line = sc.nextLine())) {
					break;	
				}		
				pw.println(line);
				
				// 5. 데이터 읽기
				String data = br.readLine();	// blocking
				if(data == null) {
					log("closed by Server");
					return;
				}
				
				// 6. 콘솔 출력
				System.out.println("<<"+data);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(socket != null && !socket.isClosed()) {
					socket.close();
				}
				if(sc != null) {
					sc.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static void log(String log) {
		System.out.println("[Echo CLient] " + log);
	}
}


