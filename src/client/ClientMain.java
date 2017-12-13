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
 * @author	: 정동희     
 * @date		: 2017-11-20
 * @desc		: 클라이언트 시작 클래스. 
 * 					서버와 통신하는 클래스 ClientBackground 객체 관리  
 * 					UI를 나타내는 Main.fxml 및 Controller 인 MainController 클래스 객체 관리  
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
			
			//사용자 ID 입력 받는다.
			/*
			Scanner scan = new Scanner(System.in);
			System.out.println("사용할 ID를 입력하세요 : ");
			str_ID = scan.nextLine();*/
			str_ID =  getRandomID();
			
			FXMLLoader loader  = new FXMLLoader();
			
			Parent root =  loader.load(getClass().getResource("view/Main.fxml").openStream());
			
			/** 컨트롤러 받아오고 Init */
			Con = (client.controller.MainController)loader.getController();
			Con.setMain(this);
			Con.setID(str_ID);
			
			/** 백그라운드에서 서버 연결 */
			Back.setStr_ID(str_ID);
			Back.setMain(main);
			Back.connet();
			
	        Scene scene = new Scene(root);
	        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("../resources/title.png")));
	        primaryStage.setTitle(str_ID);
	        scene.getStylesheets().add(getClass().getResource("view/myStyle.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.resizableProperty().setValue(Boolean.FALSE);
			
			
			//창 닫는부분 추가	. (종료)
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					print.p(str_ID, "접속 종료");
					// Back 에 SendMsg(Json 형태) 로 사용자 접속 종료 알림
					JSONObject json = new JSONObject();
					json.put(JsonKeyList.LogOut, str_ID);
					
					Back.SetThreadStop();	//Background Thread 종료 플래그 전달
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

	/** msg 송신 . this -> ClientBackground */
	public void sendMsg(String jsonString) {
		Back.sendMsg(jsonString);
	}
	
	/** msg 수신. this -> MainController */
	public void recMsg(String jsonString) {
		Con.recMsg(jsonString);
	}

	/** 로그아웃한 유저 ID 전달 */
	public void removeID(String id) {
		Con.removeID(id);
	}
	
	
}
