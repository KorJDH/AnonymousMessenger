package shared;


/**
 * @author	: ������     
 * @date		: 2017. 11. 27.
 * @desc		: ������ Ŭ���̾�Ʈ ��ſ� ���Ǵ� JSONObject�� Key�� ���� �����ϴ� Ŭ����.
 * 						��� JSONKey ���� �� Ŭ������ �����Ѵ�.
 */
public class JsonKeyList {
	/**  
	 * List : (���� -> Ŭ��) ������ ��� ���� ID ������ �� ���. used by [ ServerBackground, ClientBackground ]
	 * LoginACK : (Ŭ�� -> ����) �α����� ������ ������ �ڽ��� �α����� �˸��� ���. used by [ ClientBackground ]
	 * Sender : (Ŭ�� -> ���� -> Ŭ��) �޽��� �ۼ��ſ� ���Ǵ� '�۽� ���� ID'. used by [ ChatController(�۽�), MainController(����) ]
	 * Msg : (Ŭ�� -> ���� -> Ŭ��) �޽��� �ۼ��ſ� ���Ǵ� '�޽��� ����'. used by [ ChatController(�۽�), MainController(����) ]
	 * Receiver : (Ŭ�� -> ���� -> Ŭ��) �޽��� �ۼ��ſ� ���Ǵ� '���� ���� ID'. used by [ ChatController(�۽�), MainController(����) ]
	 * UserList : (���� -> Ŭ��) ��� ���� List ������ ��  jsonArray �� ���޵Ǵµ� �� �� Array �ȿ��� ID Key������ ���. used by [ ServerBackground, ClientBackground ]
	 * LogOut : (Ŭ�� -> ����) ������ �α׾ƿ� �� �� ������ �˸��� �뵵�� ���. used by [ ClientMain, ClientBack, ServerBack ]
	 * */
	public final static String List = "List";
	public final static String LoginACK = "LoginACK";
	public final static String Sender = "sender";
	public final static String Msg = "msg";
	public final static String Receiver = "receiver";
	public final static String UserList = "ID";
	public final static String LogOut = "LogOut";

}
