package client;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import org.json.simple.JSONObject;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import shared.JsonKeyList;
import shared.Print;

/**
 * @author	: ������     
 * @date		: 2017-11-20
 * @desc		: Ŭ���̾�Ʈ ���� Ŭ����. 
 * 					������ ����ϴ� Ŭ���� ClientBackground ��ü ����  
 * 					UI�� ��Ÿ���� Main.fxml �� Controller �� MainController Ŭ���� ��ü ����  
 */

public class ClientMain extends Application {

	private static ClientBackground Back  = new ClientBackground();
	private static client.controller.MainController Con;
	private static String str_ID = "";
	private static ClientMain main;
	private Print print  = (String title, String content) -> System.out.println("[" + title + "] : " + content);
	
	@Override
	public void start(Stage primaryStage) {
		try {
						
			main = this;
			
			//����� ID �Է� �޴´�.
			/*
			Scanner scan = new Scanner(System.in);
			System.out.println("����� ID�� �Է��ϼ��� : ");
			str_ID = scan.nextLine();*/
			str_ID =  getRandomID();
			
			FXMLLoader loader  = new FXMLLoader();
			
			Parent root =  loader.load(getClass().getResource("view/Main.fxml").openStream());
			
			/** ��Ʈ�ѷ� �޾ƿ��� Init */
			Con = (client.controller.MainController)loader.getController();
			Con.setMain(this);
			Con.setID(str_ID);
			
			/** ��׶��忡�� ���� ���� */
			Back.setStr_ID(str_ID);
			Back.setMain(main);
			Back.connet();
			
	        Scene scene = new Scene(root);
	        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../resources/title.png")));
	        primaryStage.setTitle(str_ID);
	        scene.getStylesheets().add(getClass().getResource("view/myStyle.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.resizableProperty().setValue(Boolean.FALSE);
			
			
			//â �ݴºκ� �߰�	. (����)
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					print.p(str_ID, "���� ����");
					// Back �� SendMsg(Json ����) �� ����� ���� ���� �˸�
					JSONObject json = new JSONObject();
					json.put(JsonKeyList.LogOut, str_ID);
					
					Back.SetThreadStop();	//Background Thread ���� �÷��� ����
					Back.sendMsg(json.toJSONString());
					Back = null;
					System.exit(0);
					
				}
			});
			
			primaryStage.show();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	private String getRandomID() {
		Random rnd =new Random();
		StringBuffer buf =new StringBuffer();

		for(int i=0;i<6;i++){
		    if(rnd.nextBoolean()){
		        buf.append((char)((int)(rnd.nextInt(26))+97));
		    }else{
		        buf.append((rnd.nextInt(10)));
		    }
		}
		
		return buf.toString();
	}
	
	
	public void AddUser(String jsonString) {
		Con.AddUser(jsonString);
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	/** msg �۽� . this -> ClientBackground */
	public void sendMsg(String jsonString) {
		Back.sendMsg(jsonString);
	}
	
	/** msg ����. this -> MainController */
	public void recMsg(String jsonString) {
		Con.recMsg(jsonString);
	}

	/** �α׾ƿ��� ���� ID ���� */
	public void removeID(String id) {
		Con.removeID(id);
	}
	
	
}
