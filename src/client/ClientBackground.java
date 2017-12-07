package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import shared.JsonKeyList;

/**
 * @author	: 정동희     
 * @date		: 2017-11-19
 * @desc		: 서버와 소켓 통신 담당 클래스 
 * 					서버를 거쳐 들어오는 메시지들을 수신해서 ClientMain클래스에 전달하는 역할 수행
 * 					서버와의 메시지는 JSONString으로 주고 받음  
 */

public class ClientBackground {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private ClientMain Main;
	private String msg;
	private static String str_ID ;
	private static List list_users = new ArrayList<>();	//유저들 아이디를 담고 있는 list
	
	private Receiver receiver;
	

	public void setStr_ID(String str_ID) {
		this.str_ID = str_ID;
	}

	public void setMain(ClientMain main) {
		this.Main = main;
	}

	//생성자
	public ClientBackground() {

	}
	public void SetThreadStop() {
		receiver.setStop();
	}
	
	public void connet() {
		try {
			
			socket = new Socket("localhost", 7777);
//			socket = new Socket("192.168.55.0", 7777);
			
			System.out.println("서버 연결 됨");
			
			JSONObject json = new JSONObject();
			
			//통신할 수 있는 통로 생성
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
				
			//사용자 ID 전송
			json.put(JsonKeyList.LoginACK, str_ID);
			out.writeUTF(json.toJSONString());
			
			receiver = new Receiver();
			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/** to Server */
	public void sendMsg(String msg) {
		try {
			out.writeUTF(msg);
			System.out.println("ClientBack[sendMsg] : " + msg);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/** 로그인 유저와 사용자 ID 상태 콘솔 출력용 */
	private void printStatus() {
		System.out.println("##############################");
		System.out.println("Login ID  : " + str_ID);
		System.out.print("User List : ");
		for (int i = 0 ; i < list_users.size(); i ++) {
			System.out.print(   (i + 1) +"."  + list_users.get(i).toString() + "  " );
		}
		System.out.println("\n##############################\n");
	}
	
	
	/**
	 * @author	: 정동희
	 * @date		: 2017. 11. 19.
	 * @desc 		: 내부클래스. 서버로부터 들어오는 메시지 처리
	 */
	class Receiver extends Thread{
		private boolean bln_setStop = false; 
		public Receiver() {
			this.start();
		}
		
		public void setStop () {
			bln_setStop = true; 
		}
		
		public void run() {
			
			// 서버로부터 들어오는 메시지 표시
			while(in !=null ) {
				
				if (bln_setStop) {
					exit();
					break;
				}
				try {
					String strL_senderID ;
					JSONObject json = new JSONObject();
					msg = in.readUTF();
					
					json = (JSONObject)new JSONParser().parse(msg);
					
					//전체 유저 리스트 받아오기
					if (!json.getOrDefault(JsonKeyList.List, "").equals("")) {
						//ID = (String) js.getOrDefault(JsonKeyList.LoginACK, "");
						JSONArray recArray = (JSONArray) json.get(JsonKeyList.List);
						System.out.println( recArray.toJSONString());
						
						Main.AddUser(recArray.toJSONString());
					}else if (!json.getOrDefault(JsonKeyList.LogOut, "").equals("")) {	
						// 로그아웃한 유저 ID 수신.
						String id =  json.get(JsonKeyList.LogOut).toString();
						Main.removeID(id);
						
					}else {	//클라이언트 메시지 수신 부분. Background -> ClientMain
						Main.recMsg(msg);
					}
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		
	}
	

	public static void main(String[] args) {
		ClientBackground C = new ClientBackground();
		System.out.println("<Client>");
		C.connet();

	}

	public void exit() {
		try {
			in.close();
			out.close();
			socket.close();
			System.exit(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
