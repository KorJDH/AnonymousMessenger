package client;

/**
 * @author	: ������
 * @date		: 2017. 11. 25.
 * @desc 		: ListView�� Cell    
 */

public class MyUser {
	private String ID;
	private String ImagePath =  "../../resources/user.png";	//������ ���� ��� 

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
