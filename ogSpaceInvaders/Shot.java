import javax.swing.ImageIcon;

public class Shot extends Sprite {
	private String path = "./img/shot.png";
	private final int H_SPACE = 4;
	private final int V_SPACE = 10;

	public Shot() {
	}

	public Shot(int x, int y) {
		ImageIcon ii = new ImageIcon(path);
		setImage(ii.getImage());
		setX(x + H_SPACE);
		setY(y + V_SPACE);
	}
}

