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
 * @author	: ������     
 * @date		: 2017. 11. 19.
 * @desc		: Ŭ���̾�Ʈ�� ���� ��� �ϴ� Ŭ����
 * 					  Ŭ���̾�Ʈ�� ������ ����ؼ� �޾� �ִ� ���
 * 					  ������ Ŭ���̾�Ʈ�� ���� Ŭ������ Receiver �� ������ȭ �ؼ� �޽����� ���� �޴´�.
 */
public class ServerBackground {
	
	// ���� ����
	private ServerSocket serverSocket;
	private Socket socket;
	
	//**XXX 01.����ڵ��� ������ �����ϴ� �� ����
	private Map<String,ClientInfo> clientsMap = new HashMap<String, ClientInfo>();
	
	private Print print  = (String title, String content) -> System.out.println("[" + title + "] : " + content);
	
	
	/** ����Socket ���� �� ����� ���� ����  */
	public void setting() {
		try {
			Collections.synchronizedMap(clientsMap);		//Ŭ���̾�Ʈ ���� �������� 
			serverSocket = new ServerSocket(7777);
			
			System.out.println("Client ��ٸ�����...");
			while(true) {
				// �����ϴ� Ŭ���̾�Ʈ �޾��ִ� �κ�.
				socket = serverSocket.accept();		//���� ������ ������ ��� �ݺ��ؼ� ����ڸ� �޴´�.
				System.out.println(socket.getInetAddress()+"���� �����߽��ϴ�.");

				//���⼭ ���ο� ����� ������ Ŭ���� �����ؼ� ���������� �����Ѵ�.
				Receiver receiver = new Receiver(socket);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

	// ���� ����(Ŭ���̾�Ʈ) ����� ����
	public void addClient(String ID, Socket socket) {
		
		DataInputStream in;
		DataOutputStream out;
		try {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			ClientInfo Cinfo = new ClientInfo(in, out);
			clientsMap.put(ID, Cinfo );
			System.out.println("ServerBack[addClient] : Map.put " + ID + "�Ϸ�"  );
			
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
	
	
	
	/*  ���� 1 : Ŭ���̾�Ʈ�� �޽��� �ۼ���
	 *  	�Ķ���� : #msg : json ���� String  ( key : msg, receiver, sender ) / #index : JSKey_Msg 
	 *  ���� 2 : ���� ���� ����Ʈ ��� �������� ���� (���� �α����ϴ� ������ ���涧���� �̺�Ʈ �߻�. ������ �������ִ� �������� ģ����� ������ ���� )
	 *  	�Ķ���� : #msg : ���� �α����� ���� ID  /  #index : JSKey_List
	 *  ���� 3 : �α׾ƿ��� ���� ID �ٸ� ���������� ����
	 *  	�Ķ���� : #msg : �α׾ƿ��� ���� ID / #index : JSKey_LogOut
	 */
	public void sendMsg(String msg, String index) {

		String strL_recvID;
		JSONObject js;
		
		try {
			if (index.equals(JsonKeyList.Msg)) {		//Ŭ���̾�Ʈ�� �޽��� 
				//JSON Key: sender, msg, receiver
				js = (JSONObject)new JSONParser().parse(msg);
				
				strL_recvID = js.get(JsonKeyList.Receiver).toString();
				
				clientsMap.get(strL_recvID).getOut().writeUTF(msg);
			}else if (index.equals(JsonKeyList.List)) {	//�������� ��� ���� ID ����Ʈ�� ��� �������� ����
				 //Iterator ������ ����ؼ� �ʿ��� �ϳ��� �����´�.
				Iterator<String> it = clientsMap.keySet().iterator();
				String key;
				JSONObject sendObject = new JSONObject();
				JSONArray sendArray = new JSONArray();
				String strMsg ;
				
				//Map ���� ��� ���� ID�� JsonArray �� ��ȯ
				while(it.hasNext()) {
						key = it.next();
						JSONObject temp = new JSONObject();
						temp.put(JsonKeyList.UserList,  key);
						sendArray.add(temp);
				}
				sendObject.put(JsonKeyList.List,sendArray);
				strMsg = sendObject.toJSONString();
				
				//��� �������� list ����
				it = clientsMap.keySet().iterator();
				while(it.hasNext()) {
					key = it.next();
					print.p("Send to " + key , strMsg);
					clientsMap.get(key).getOut().writeUTF(strMsg);
				}
				
				System.out.println(sendObject.toJSONString());
			}else if(index.equals(JsonKeyList.LogOut)) {
				//�α׾ƿ��� ����� ID ����
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
	 * @author	: ������    
	 * @date		: 2017. 11. 19.
	 * @desc		: ���� Ŭ���� ���� 
	 * 					  ������ Ŭ���̾�Ʈ�� 1���� ����
	 * 					  Ŭ���̾�Ʈ�� ���� �޽��� ����  
	 */
	class Receiver extends Thread{
		// ������ �ְ� �ޱ� ���� ��Ʈ�� ����
		private DataInputStream in;
		private DataOutputStream out;
		private String ID;
		JSONObject json = new JSONObject();
		Socket socket ;
		
		// **XXX 02. ���ù��� ȥ�ڼ� ��Ʈ��ũ ó��(��� ���)
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
				
				//Ŭ���̾�Ʈ ��κ��� ������ �޽��� in 
				while(in !=null ) {
					if (!bln_Connected) {
						break;
					}
					msg = in.readUTF();
					System.out.println("Receiver[run] : " + msg);
					json = (JSONObject)new JSONParser().parse(msg);
					
					if (!json.getOrDefault(JsonKeyList.LoginACK, "").equals("")) {
						//������ Ŭ���̾�Ʈ ID ����
						ID = (String) json.getOrDefault(JsonKeyList.LoginACK, "");
						System.out.println("Receiver[run] : ID ���� �Ϸ� : " + ID);
						addClient(ID , socket);
						sendMsg(ID, JsonKeyList.List);
					}
					else if(!json.getOrDefault(JsonKeyList.LogOut, "").equals("")) {
						//�α׾ƿ� �˸�. �α׾ƿ��� �����ID ����
						//print.p("����?", ID);
						removeClient(ID);
						sendMsg(ID, JsonKeyList.LogOut);
						break;
						
					}else {
						
						sendMsg(msg,"msg");
						
					}
					
				}
			} catch (IOException | ParseException e) {
				//Ŭ���̾�Ʈ ���� ����� ���⼭ ���� �߻�
				System.out.println("Server[Receiver.run.catch] : remove " + ID );
				
				e.printStackTrace();
			}
		}
	}

}




