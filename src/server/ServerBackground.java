package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import shared.JsonKeyList;
import shared.Print;

/**
 * @author	: 정동희     
 * @date		: 2017. 11. 19.
 * @desc		: 클라이언트와 소켓 통신 하는 클래스
 * 					  클라이언트의 접속을 계속해서 받아 주는 기능
 * 					  접속한 클라이언트를 내부 클래스인 Receiver 를 쓰레드화 해서 메시지를 수신 받는다.
 */
public class ServerBackground {
	
	// 소켓 정의
	private ServerSocket serverSocket;
	private Socket socket;
	
	//**XXX 01.사용자들의 정보를 저장하는 맵 생성
	private Map<String,ClientInfo> clientsMap = new HashMap<String, ClientInfo>();
	
	private Print print  = (String title, String content) -> System.out.println("[" + title + "] : " + content);
	
	
	/** 서버Socket 생성 및 사용자 접속 수신  */
	public void setting() {
		try {
			Collections.synchronizedMap(clientsMap);		//클라이언트 맵을 교통정리 
			serverSocket = new ServerSocket(7777);
			
			System.out.println("Client 기다리는중...");
			while(true) {
				// 접속하는 클라이언트 받아주는 부분.
				socket = serverSocket.accept();		//먼저 서버가 할일은 계속 반복해서 사용자를 받는다.
				System.out.println(socket.getInetAddress()+"에서 접속했습니다.");

				//여기서 새로운 사용자 쓰레드 클래스 생성해서 소켓정보를 저장한다.
				Receiver receiver = new Receiver(socket);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	// 맵의 내용(클라이언트) 저장과 삭제
	public void addClient(String ID, Socket socket) {
		
		DataInputStream in;
		DataOutputStream out;
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			ClientInfo Cinfo = new ClientInfo(in, out);
			clientsMap.put(ID, Cinfo );
			System.out.println("ServerBack[addClient] : Map.put " + ID + "완료"  );
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void removeClient(String ID) {
		clientsMap.remove(ID);
	}
	
	public static void main(String[] args) {
		ServerBackground s =  new ServerBackground();
		s.setting();	
	}
	
	
	
	/*  유형 1 : 클라이언트간 메시지 송수신
	 *  	파라미터 : #msg : json 형식 String  ( key : msg, receiver, sender ) / #index : JSKey_Msg 
	 *  유형 2 : 접속 유저 리스트 모든 유저한테 전달 (새로 로그인하는 유저가 생길때마다 이벤트 발생. 기존의 접속해있던 유저들의 친구목록 갱신을 위함 )
	 *  	파라미터 : #msg : 새로 로그인한 유저 ID  /  #index : JSKey_List
	 *  유형 3 : 로그아웃한 유저 ID 다른 유저들한테 전달
	 *  	파라미터 : #msg : 로그아웃한 유저 ID / #index : JSKey_LogOut
	 */
	public void sendMsg(String msg, String index) {

		String strL_recvID;
		JSONObject js;
		
		try {
			if (index.equals(JsonKeyList.Msg)) {		//클라이언트간 메시지 
				//JSON Key: sender, msg, receiver
				js = (JSONObject)new JSONParser().parse(msg);
				
				strL_recvID = js.get(JsonKeyList.Receiver).toString();
				
				clientsMap.get(strL_recvID).getOut().writeUTF(msg);
			}else if (index.equals(JsonKeyList.List)) {	//접속중인 모든 유저 ID 리스트를 모든 유저한테 전달
				 //Iterator 선택자 사용해서 맵에서 하나씩 꺼내온다.
				Iterator<String> it = clientsMap.keySet().iterator();
				String key;
				JSONObject sendObject = new JSONObject();
				JSONArray sendArray = new JSONArray();
				String strMsg ;
				
				//Map 에서 모든 유저 ID를 JsonArray 로 변환
				while(it.hasNext()) {
						key = it.next();
						JSONObject temp = new JSONObject();
						temp.put(JsonKeyList.UserList,  key);
						sendArray.add(temp);
				}
				sendObject.put(JsonKeyList.List,sendArray);
				strMsg = sendObject.toJSONString();
				
				//모든 유저한테 list 전달
				it = clientsMap.keySet().iterator();
				while(it.hasNext()) {
					key = it.next();
					print.p("Send to " + key , strMsg);
					clientsMap.get(key).getOut().writeUTF(strMsg);
				}
				
				System.out.println(sendObject.toJSONString());
			}else if(index.equals(JsonKeyList.LogOut)) {
				//로그아웃한 사용자 ID 전달
				String key = "";
				Iterator<String> it = clientsMap.keySet().iterator();
				while(it.hasNext()) {
					key = it.next();
					JSONObject json = new JSONObject();
					json.put(JsonKeyList.LogOut,  msg);
					clientsMap.get(key).getOut().writeUTF(json.toJSONString());
				}
				
			}
			
		} catch (IOException | ParseException e1) {
			e1.printStackTrace();
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------------
	/**
	 * @author	: 정동희    
	 * @date		: 2017. 11. 19.
	 * @desc		: 내부 클래스 생성 
	 * 					  접속한 클라이언트당 1개씩 생성
	 * 					  클라이언트로 부터 메시지 수신  
	 */
	class Receiver extends Thread{
		// 데이터 주고 받기 위한 스트림 정의
		private DataInputStream in;
		private DataOutputStream out;
		private String ID;
		JSONObject json = new JSONObject();
		Socket socket ;
		
		// **XXX 02. 리시버가 혼자서 네트워크 처리(계속 듣기)
		public Receiver(Socket socket) {
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				
				JSONObject js = new JSONObject();
				
				this.socket = socket;
				
				this.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		public void run() {
			String msg;
			Boolean bln_Connected  = true;
			try {
				
				//클라이언트 들로부터 들어오는 메시지 in 
				while(in !=null ) {
					if (!bln_Connected) {
						break;
					}
					msg = in.readUTF();
					System.out.println("Receiver[run] : " + msg);
					json = (JSONObject)new JSONParser().parse(msg);
					
					if (!json.getOrDefault(JsonKeyList.LoginACK, "").equals("")) {
						//접속한 클라이언트 ID 수신
						ID = (String) json.getOrDefault(JsonKeyList.LoginACK, "");
						System.out.println("Receiver[run] : ID 수신 완료 : " + ID);
						addClient(ID , socket);
						sendMsg(ID, JsonKeyList.List);
					}
					else if(!json.getOrDefault(JsonKeyList.LogOut, "").equals("")) {
						//로그아웃 알림. 로그아웃한 사용자ID 수신
						//print.p("종료?", ID);
						removeClient(ID);
						sendMsg(ID, JsonKeyList.LogOut);
						break;
						
					}else {
						
						sendMsg(msg,"msg");
						
					}
					
				}
			} catch (IOException | ParseException e) {
				//클라이언트 접속 종료시 여기서 에러 발생
				System.out.println("Server[Receiver.run.catch] : remove " + ID );
				
				e.printStackTrace();
			}
		}
	}

}




