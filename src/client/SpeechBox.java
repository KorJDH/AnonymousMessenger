package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

enum SpeechDirection{
    LEFT, RIGHT
}

/**
 * @author	: 정동희
 * @date		: 2017. 11. 25.
 * @desc 		: 채팅창 내의 말풍선
 */

public class SpeechBox extends HBox{
    private Color DEFAULT_SENDER_COLOR = Color.GOLD;
    private Color DEFAULT_RECEIVER_COLOR = Color.LIMEGREEN;
    private Background DEFAULT_SENDER_BACKGROUND, DEFAULT_RECEIVER_BACKGROUND;

    private String message;
    private SpeechDirection direction;

    private Label displayedText;
    
    /** String 을 SVGpath data로 파싱하여 만드는 간단한 Shape */
    private SVGPath directionIndicator;

    public SpeechBox(String message, int direction){
        this.message = message;
        
        if (direction  == 0 ) {
        	this.direction = this.direction.LEFT;
        }else {
        	this.direction = this.direction.RIGHT;
        }
        initialiseDefaults();
        setupElements();
    }
    
    /** Sender, Receiver의 말풍선 배경 사각형 세팅  */
    private void initialiseDefaults(){
        DEFAULT_SENDER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_SENDER_COLOR, new CornerRadii(5,0,5,5,false), Insets.EMPTY));
        DEFAULT_RECEIVER_BACKGROUND = new Background(
                new BackgroundFill(DEFAULT_RECEIVER_COLOR, new CornerRadii(0,5,5,5,false), Insets.EMPTY));
    }
    
    /** 메시지 내용 라벨링 */
    private void setupElements(){
        displayedText = new Label(message);
        displayedText.setPadding(new Insets(5));	//Top,Right,Bottom,Left offsets 을 5로 세팅
        displayedText.setWrapText(true);
        directionIndicator = new SVGPath();

        if(direction == SpeechDirection.LEFT){
            configureForReceiver();
        }
        else{
            configureForSender();
        }
    }

    /** 라벨 배경색 지정 및 SVGPath로 도형 생성 및 말풍선 완성 */
    private void configureForSender(){
        displayedText.setBackground(DEFAULT_SENDER_BACKGROUND);
        displayedText.setAlignment(Pos.CENTER_RIGHT);
        
        directionIndicator.setContent("M10 0 L0 10 L0 0 Z");
        directionIndicator.setFill(DEFAULT_SENDER_COLOR);
        
        HBox container = new HBox(displayedText, directionIndicator);
        //메시지를 표시하기 위해 SpeechBox에 제공된 width의 최대 75 %를 사용
        container.maxWidthProperty().bind(widthProperty().multiply(0.75));
        getChildren().setAll(container);
        setAlignment(Pos.CENTER_RIGHT);
    }

    private void configureForReceiver(){
        displayedText.setBackground(DEFAULT_RECEIVER_BACKGROUND);
        displayedText.setAlignment(Pos.CENTER_LEFT);
        directionIndicator.setContent("M0 0 L10 0 L10 10 Z");
        directionIndicator.setFill(DEFAULT_RECEIVER_COLOR);

        HBox container = new HBox(directionIndicator, displayedText);
        //메시지를 표시하기 위해 SpeechBox에 제공된 width의 최대 75 %를 사용
        container.maxWidthProperty().bind(widthProperty().multiply(0.75));
        getChildren().setAll(container);
        setAlignment(Pos.CENTER_LEFT);
    }
}