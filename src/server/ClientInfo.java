package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
 
 /**
 * @author	: 정동희
 * @date		: 2017. 12. 2.
 * @desc 		: 서버에 접속한 클라이언트의 소켓 정보 저장. 
 * 					나중에 추가적으로 가지고 있어야 할 클라 정보는 여기에 추가 할 것
 */

public class ClientInfo {
	private DataInputStream in ;
	private DataOutputStream out;
	
	public ClientInfo(DataInputStream in, DataOutputStream out) {
		this.in = in;
		this.out = out;
	}

	public DataOutputStream getOut() {
		return out;
	}
}
