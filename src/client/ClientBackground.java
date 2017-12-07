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
 * @author	: ������     
 * @date		: 2017-11-19
 * @desc		: ������ ���� ��� ��� Ŭ���� 
 * 					������ ���� ������ �޽������� �����ؼ� ClientMainŬ������ �����ϴ� ���� ����
 * 					�������� �޽����� JSONString���� �ְ� ����  
 */

public class ClientBackground {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private ClientMain Main;
	private String msg;
	private static String str_ID ;
	private static List list_users = new ArrayList<>();	//������ ���̵� ��� �ִ� list
	
	private Receiver receiver;
	

	public void setStr_ID(String str_ID) {
		this.str_ID = str_ID;
	}

	public void setMain(ClientMain main) {
		this.Main = main;
	}

	//������
	public ClientBackground() {

	}
	public void SetThreadStop() {
		receiver.setStop();
	}
	
	public void connet() {
		try {
			
			socket = new Socket("localhost", 7777);
//			socket = new Socket("192.168.55.0", 7777);
			
			System.out.println("���� ���� ��");
			
			JSONObject json = new JSONObject();
			
			//����� �� �ִ� ��� ����
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
				
			//����� ID ����
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
	
	/** �α��� ������ ����� ID ���� �ܼ� ��¿� */
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
	 * @author	: ������
	 * @date		: 2017. 11. 19.
	 * @desc 		: ����Ŭ����. �����κ��� ������ �޽��� ó��
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
			
			// �����κ��� ������ �޽��� ǥ��
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
					
					//��ü ���� ����Ʈ �޾ƿ���
					if (!json.getOrDefault(JsonKeyList.List, "").equals("")) {
						//ID = (String) js.getOrDefault(JsonKeyList.LoginACK, "");
						JSONArray recArray = (JSONArray) json.get(JsonKeyList.List);
						System.out.println( recArray.toJSONString());
						
						Main.AddUser(recArray.toJSONString());
					}else if (!json.getOrDefault(JsonKeyList.LogOut, "").equals("")) {	
						// �α׾ƿ��� ���� ID ����.
						String id =  json.get(JsonKeyList.LogOut).toString();
						Main.removeID(id);
						
					}else {	//Ŭ���̾�Ʈ �޽��� ���� �κ�. Background -> ClientMain
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
