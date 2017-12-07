package client.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;

import client.SpeechBox;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import shared.JsonKeyList;

/**
 * @author	: ������     
 * @date		: 2017. 11. 23.
 * @desc		: chat.fxml�� Controller Ŭ����
 * 					 ä��â�� ��Ʈ�� �ϴ� ����
 */

public class ChatController implements Initializable{
	
	@FXML
    private VBox vbox_main;
	
    @FXML
    private StackPane root;

    @FXML
    private Label lblTop;

    @FXML
    private VBox vbox_Chat;

    @FXML
    private TextField txtMsg;

    @FXML
    private Button btnSend;
    
    //receiver ID
    private String recID;
    
    //sender ID
    private String senID;
    
	private MainController MainCon;
	
	private ChatController myCon;
	
	private ObservableList<Node> speechBubbles = FXCollections.observableArrayList();
	
	@FXML
	private ScrollPane messageScroller;
	private HBox inputContainer;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("ChatControll init");
	}
	
	public void Init(String recID, String sendID, MainController main ) {
		this.recID  = recID;
		this.senID = sendID;
		this.MainCon = main;
		setupMessageDisplay();		//��ȭâ ����
		InitControll();
	}
	
	/** ��ȭâ ���� */
	private void setupMessageDisplay() {
		vbox_Chat.setSpacing(5);
		Bindings.bindContentBidirectional(speechBubbles, vbox_Chat.getChildren());
		
		messageScroller = new ScrollPane(vbox_Chat);
		
        messageScroller.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        messageScroller.setHbarPolicy(ScrollBarPolicy.NEVER);
        messageScroller.setPrefHeight(300);
        messageScroller.prefWidthProperty().bind(vbox_Chat.prefWidthProperty().subtract(5));
        messageScroller.setFitToWidth(true);
        messageScroller.setFitToHeight(true);
        
        //** �޽��� ���������� ��ũ���� �ڵ����� �����ִ� ��� *//* 
        speechBubbles.addListener((ListChangeListener<Node>) change -> {
             while (change.next()) {
                 if(change.wasAdded()){
                     //messageScroller.setVvalue(messageScroller.getVmax());
                 }
             }
        });
		
	}
	private void InitControll() {
		txtMsg.setPromptText("Enter message");
		lblTop.setText(recID.toString() + "�� ���� ��ȭ");
	}
	
	@FXML
    void sendButton_Clicked(MouseEvent event) {
		btnSend.disableProperty().bind(txtMsg.lengthProperty().isEqualTo(0));
        sendMessage(txtMsg.getText());
        txtMsg.setText("");
    }
	
	public void sendMessage(String message){
		//��ǳ�� ����
        speechBubbles.add(new SpeechBox(message, 1 ) );
        
        //JSON Key: sender, msg, receiver
    	JSONObject json = new JSONObject();
		String re ;
		json.put(JsonKeyList.Sender, senID);
		json.put(JsonKeyList.Msg,  message);
		json.put(JsonKeyList.Receiver, recID );
		
		//MainCon�� ������ String
		MainCon.sendMsg(json.toJSONString());
    }

	//�޽��� ���� ó��.
    public void receiveMessage(String message){
    	new Thread(() -> {
		      Platform.runLater(() -> { 
		    	  		speechBubbles.add(new SpeechBox(message, 0));
        
					});   
				}).start();
    }
    
    @FXML
    void txtMsg_OnKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER))
        {
        	btnSend.disableProperty().bind(txtMsg.lengthProperty().isEqualTo(0));
            sendMessage(txtMsg.getText());
            txtMsg.setText("");
        }
    }
    
    public String getSenID() {
		return senID;
	}
    
	public String getRecID() {
		return recID;
	}
	
	/** Stage Close And Controller null */
	public void exit() {
		Stage stage =  (Stage) root.getScene().getWindow();
		stage.close();
		myCon = null;
	}

}
