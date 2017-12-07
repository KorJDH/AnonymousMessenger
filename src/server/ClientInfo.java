package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
 
 /**
 * @author	: ������
 * @date		: 2017. 12. 2.
 * @desc 		: ������ ������ Ŭ���̾�Ʈ�� ���� ���� ����. 
 * 					���߿� �߰������� ������ �־�� �� Ŭ�� ������ ���⿡ �߰� �� ��
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
