package client;

/**
 * @author	: 정동희
 * @date		: 2017. 11. 25.
 * @desc 		: ListView의 Cell    
 */

public class MyUser {
	private String ID;
	private String ImagePath =  "../../resources/user.png";	//프로필 사진 경로 

	public MyUser(String ID ) {
		this.ID = ID;
	}

	public String getID() {
		return ID;
	}
	
	public String getImagePath() {
		return ImagePath;
	}

}
