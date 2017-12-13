package client.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import client.ClientMain;
import client.ChatView;
import client.MyUser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import shared.JsonKeyList;

/**
 * @author	: ������     
 * @date		: 2017. 11. 21.
 * @desc		: Main.fxml�� Controller Ŭ����
 * 					  ģ�� ����� ListView�� ����
 * 					  ä��â ����[ chat.fxml +  ChatController ]
 * 					  ������ ä��â�� [ key : ID / Value : ChatController ] ������ Map�� ����Ǿ� ���� 
 */

public class MainController implements Initializable  {
	private ClientMain Main;
	public MainController MainCon;
	
	//* key : ���� ID / Value : ��Ʈ�ѷ�  Map�� ���� */ 
	private Map<String,ChatView> conMap = new HashMap<String, ChatView>();
	private static String str_ID;
	
	//* ListView�� ����ȭ �Ǿ��ִ� ����Ʈ */
	private ObservableList<MyUser> myUsers =FXCollections.observableArrayList();
	
	//* Key ���� ID�� �����Ͽ� ObservableList���� ��ü�� ã�� ���� ����ϱ� ���� Map */
	private Map<String, MyUser> userMap = new HashMap<String,MyUser>();

	@FXML
	private VBox list_VBox;
	
	@FXML
	private ListView<MyUser> listV_User;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		this.MainCon = this;
		
		/** Custom ListView 
		 *   Cell�� �ٽ� ����. 
		 *   MyUser : �����ID + �������̹��� */
		listV_User.setCellFactory(new Callback<ListView<MyUser>,ListCell<MyUser>>(){
			@Override
			public ListCell<MyUser> call(ListView<MyUser> param) {
				ListCell<MyUser> cell = new ListCell<MyUser>() {
					protected void updateItem(MyUser user, boolean bln) {
						super.updateItem(user,bln);
						if(user!=null) {
							Image img = new Image(getClass().getResource( user.getImagePath()).toExternalForm());
							ImageView imgView = new ImageView(img);
							setGraphic(imgView);
							setText(user.getID());
						}else {
							setGraphic(null);
							setText(null);
						}
					}
				};
				return cell;
			}
			
		});
	}
	
	
	public void setMain(ClientMain main) {
		this.Main = main;
	}
	
	public void AddUser(String jsonString) {
		
		new Thread(() -> {
		      Platform.runLater(() -> {
		    	JSONArray jsonArr = null;
				try {
					jsonArr = (JSONArray)new JSONParser().parse(jsonString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				List list = JSONArrayToList(jsonArr);
				
				//�޾ƿ� ���� ����Ʈ ListView�� �߰� 
				if (list.size() > 0 ) {
					
					for (int i = 0 ; i < list.size(); i++) {
						if (myUsers.size() > 0 ) {
							if(!userMap.containsKey(list.get(i).toString())) {
								MyUser user = new MyUser(list.get(i).toString());
								myUsers.add(user);
								userMap.put(list.get(i).toString(), user);
								
							}
						}else {
							MyUser user = new MyUser(list.get(i).toString());
							myUsers.add(user);
							userMap.put(list.get(i).toString(), user);
						}
					}
					
					listV_User.setItems(myUsers);
				}
		      });   
		}).start();
		
	}
	
	
	/** JSONArray -> List ��ȯ */ 
	public static List JSONArrayToList(JSONArray array) {
        
		List list = new ArrayList();
        String ID;
        
        for (int i = 0; i < array.size(); i++) {
        	JSONObject json = (JSONObject)array.get(i);
        	ID = json.get("ID").toString();
        	
        	if(!ID.equals(str_ID)) {
        		System.out.println("[" + str_ID + "] Add " + ID );
        		list.add(json.get("ID"));
        	}
        }
        return list;
    }
	
	public void setID(String str_ID) {
		this.str_ID = str_ID;
	}
	
	@FXML
	public void listV_MouseClicked(MouseEvent click) {	
		/** ListView Double Click Event 
		 * 	 ����Ŭ���� ��ȭ�� ����
		 * */
		if(click.getClickCount() == 2 ) {
			String str_Temp = "";
			
			ObservableList<MyUser> UID;
			
			UID = listV_User.getSelectionModel().getSelectedItems();
			
			for (MyUser id : UID ) {
				str_Temp = id.getID();
			}
			
			createChatControllerAndRec(str_Temp, "");
			
			System.out.println("Double Clicked ID : " + str_Temp);
		}
	}


	/** �޽��� �۽�. this -> ClientMain */
	public void sendMsg(String jsonString) {
		Main.sendMsg(jsonString);
	}

	/** �޽��� ����. this -> ChatController */
	public void recMsg(String jsonString) {
		try {
			String str_sender;
			String str_msg;
			String str_receiver;
			
			JSONObject json = (JSONObject)new JSONParser().parse(jsonString);
			str_sender = json.get(JsonKeyList.Sender).toString();
			str_msg = json.get(JsonKeyList.Msg).toString();
			str_receiver =json.get(JsonKeyList.Receiver).toString();
			System.out.println("MainController [recMsg]  msg : " + jsonString );
			
			if (conMap.containsKey(str_sender)) {
				conMap.get(str_sender).receiveMessage(str_msg);
			}else {
				createChatControllerAndRec(str_sender,  str_msg);
			}
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	//�ٸ� �����κ��� �޽����� ������ ȭ�� ���� �޽��� �����ִ� �κ�
	private void createChatControllerAndRec(String ID, String msg) {
		
		if (!conMap.containsKey(ID)) {
				
			new Thread(() -> {
			      Platform.runLater(() -> { 
			    	  	/*
			    	  	FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../view/chat.fxml"));
						ChatController chatCon = null;
						StackPane root1 = null;
						try {
							root1 = (StackPane) fxmlLoader.load();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						chatCon = (ChatController)fxmlLoader.getController();
						
						System.out.println("Controller ex: " +  chatCon);
			    	  	chatCon.Init(ID,str_ID, this);
			    	  	*/
			    	  
						//Map�� ID���� Controller ����
			    	  	StackPane root1 =  new StackPane();
			    	  	ChatView chat = new ChatView(ID,str_ID, this); 
			    	  	
						conMap.put(ID, chat);
						
						root1.getChildren().add(chat);
						Scene newScene = new Scene(root1,500, 600 );
						Stage stage = new Stage();
						stage.setScene(newScene);
						stage.getIcons().add(new Image(getClass().getResourceAsStream("../../resources/chat.png")));
						stage.resizableProperty().setValue(Boolean.FALSE);
						stage.setTitle(str_ID + " <-> " +  ID);
						
						//â �ݴºκ� �߰�	. (����)
						stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
							@Override
							public void handle(WindowEvent event) {
								removeChat(ID);
								
							}
						});
						
						stage.show();
						//stage.hide();
						
						if (!msg.equals("")) {
							if(conMap.containsKey(ID)) {
								conMap.get(ID).receiveMessage(msg);
							}else {
								System.out.println("str_sender : " + ID);
								System.out.println("Map Size : " + conMap.size());	
							}
						}

			      });   
			}).start();
				
				
			}else {
				
			}
	}
	
	/*�α׾ƿ��� ���� ID ����
	 * listView ���� ���� �� �����ִ� ��Ʈ�ѷ� ����
	 */
	public void removeID(String id) {
		
		new Thread(() -> {
		      Platform.runLater(() -> {
		    	  myUsers.remove(userMap.get(id));
		    	  userMap.remove(id);
		    	  
		    	  /** log out �� �������� ��ȭâ�� �������� �������ش� */
		    	  if(conMap.containsKey(id)) {
		    		  removeChat(id);
		    	  }
		    	  
		      });   
		}).start();
		
	}
	
	/** ä��â �ݾ����� ó�� */
	private void removeChat(String id) {
		conMap.get(id).exit();
		conMap.remove(id);
	}
	
}
