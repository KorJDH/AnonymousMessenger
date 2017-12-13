package shared;


/**
 * @author	: 정동희     
 * @date		: 2017. 11. 27.
 * @desc		: 서버와 클라이언트 통신에 사용되는 JSONObject의 Key값 들을 저장하는 클래스.
 * 						모든 JSONKey 들은 이 클래스에 기입한다.
 */
public class JsonKeyList {
	/**  
	 * List : (서버 -> 클라) 접속한 모든 유저 ID 전달할 때 사용. used by [ ServerBackground, ClientBackground ]
	 * LoginACK : (클라 -> 서버) 로그인한 유저가 서버에 자신의 로그인을 알릴때 사용. used by [ ClientBackground ]
	 * Sender : (클라 -> 서버 -> 클라) 메시지 송수신에 사용되는 '송신 유저 ID'. used by [ ChatController(송신), MainController(수신) ]
	 * Msg : (클라 -> 서버 -> 클라) 메시지 송수신에 사용되는 '메시지 내용'. used by [ ChatController(송신), MainController(수신) ]
	 * Receiver : (클라 -> 서버 -> 클라) 메시지 송수신에 사용되는 '수신 유저 ID'. used by [ ChatController(송신), MainController(수신) ]
	 * UserList : (서버 -> 클라) 모든 유저 List 전달할 때  jsonArray 로 전달되는데 그 때 Array 안에서 ID Key값으로 사용. used by [ ServerBackground, ClientBackground ]
	 * LogOut : (클라 -> 서버) 유저가 로그아웃 할 때 서버에 알리는 용도로 사용. used by [ ClientMain, ClientBack, ServerBack ]
	 * */
	public final static String List = "List";
	public final static String LoginACK = "LoginACK";
	public final static String Sender = "sender";
	public final static String Msg = "msg";
	public final static String Receiver = "receiver";
	public final static String UserList = "ID";
	public final static String LogOut = "LogOut";

}
