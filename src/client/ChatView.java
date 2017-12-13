package client;

import org.json.simple.JSONObject;

import client.controller.MainController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import shared.JsonKeyList;

/**
 * @author	: ������
 * @date		: 2017. 11. 23.
 * @desc 		: 1:1 ä��â
 * 					  �޽��� �ۼ��� �� ��ȭâ ��ǳ�� �߰�
 */

public class ChatView extends VBox{
    private ObservableList<Node> speechBubbles = FXCollections.observableArrayList();

    private Label contactHeader;
    
    private ScrollPane messageScroller;
    
    private VBox messageContainer;
    
    private HBox inputContainer;
    private Pane pan_title;
    
  //receiver ID
    private String recID;
    
    //sender ID
    private String senID;

    private MainController MainCon;
    Button sendMessageButton;
    
    public ChatView(String recID, String sendID, MainController main){
        super(5);
        this.recID  = recID;
		this.senID = sendID;
		this.MainCon = main;
        Init();
    }

    private void Init(){
        setHeader();
        setMessageDisplay();
        setInputDisplay();
        getChildren().setAll(pan_title, messageScroller, inputContainer);
        setPadding(new Insets(5));
    }

    /** ��� Label Init */
    private void setHeader(){
    	pan_title = new Pane();
        contactHeader = new Label(recID.toString()  + "�԰��� ��ȭ");
        contactHeader.setAlignment(Pos.CENTER);
        contactHeader.setFont(Font.font("Verdana",FontWeight.BOLD,  22));
        contactHeader.setTextFill(Color.WHITE);
        contactHeader.prefWidthProperty().bind(pan_title.widthProperty());
        contactHeader.prefHeightProperty().bind(pan_title.heightProperty());
        contactHeader.setAlignment(getAlignment().CENTER);
        contactHeader.setTextAlignment(TextAlignment.CENTER);
        
        pan_title.getChildren().add(contactHeader);
        pan_title.setStyle("-fx-background-color: #39A67F");
		pan_title.setPrefHeight(41);
		pan_title.setMinSize(getPrefWidth(), 41);
    }
    
    /** ��ȭâ ����  */
    private void setMessageDisplay(){
        messageContainer = new VBox(5);	//spacing 5
        Bindings.bindContentBidirectional(speechBubbles, messageContainer.getChildren());

        messageScroller = new ScrollPane(messageContainer);
        messageScroller.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        messageScroller.setHbarPolicy(ScrollBarPolicy.NEVER);
        messageScroller.setPrefHeight(520);
        messageScroller.prefWidthProperty().bind(messageContainer.prefWidthProperty().subtract(5));
        messageScroller.setFitToWidth(true);
        
        /* �޽����� �߰��ɶ� ��ũ���� �ڵ����� ���� ������ �����ִ� �κ� */
        speechBubbles.addListener((ListChangeListener<Node>) change -> {
             while (change.next()) {
                 if(change.wasAdded()){
                     messageScroller.setVvalue(messageScroller.getVmax());
                 }
             }
        });
    }
    
    /** ���� �޽��� �Է� �ؽ�Ʈ �� ������ ��ư ���� */
    private void setInputDisplay(){
        inputContainer = new HBox(5);

        TextField userInput = new TextField();
        userInput.setPromptText("Enter message");
        userInput.setPrefWidth(450);
        userInput.setOnKeyPressed( (e) -> {
        	if (e.getCode().equals(KeyCode.ENTER))
            {
        		sendMessageButton.disableProperty().bind(userInput.lengthProperty().isEqualTo(0));
                sendMessage(userInput.getText());
                userInput.setText("");
            }
        	
        });

        sendMessageButton = new Button("Send");
        sendMessageButton.disableProperty().bind(userInput.lengthProperty().isEqualTo(0));
        sendMessageButton.setOnAction(event-> {
            sendMessage(userInput.getText());
            userInput.setText("");
        });
        
        inputContainer.getChildren().setAll(userInput, sendMessageButton);
    }
    
    /** JSONString���� �޽��� �۽� */
    public void sendMessage(String message){
    	
    	new Thread(() -> {
		      Platform.runLater(() -> { 
		    	  //��ǳ�� ����
		          speechBubbles.add(new SpeechBox(message, 1 ) );
		      });   
		}).start();
    	
      	JSONObject json = new JSONObject();
  		json.put(JsonKeyList.Sender, senID);
  		json.put(JsonKeyList.Msg,  message);
  		json.put(JsonKeyList.Receiver, recID );
  	
  		//MainCon�� ������ String
  		MainCon.sendMsg(json.toJSONString());
        
    }

    public void receiveMessage(String message){
    	new Thread(() -> {
    	Platform.runLater(() -> { 
    				speechBubbles.add(new SpeechBox(message, 0));

				});   
			}).start();
    }
    
    /** Stage Close */
	public void exit() {
		Stage stage =  (Stage) getScene().getWindow();
		stage.close();
	}

    
}