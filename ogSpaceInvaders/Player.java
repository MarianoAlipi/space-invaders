import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Player extends Sprite implements Commons {
	private final int START_X = 270;
	private final int START_Y = 280;

	private final String path = "./img/player.png";
	private int mWidth;

	public Player() {
		ImageIcon ii = new ImageIcon(path);
		setImage(ii.getImage());
		mWidth = ii.getImage().getWidth(null);
		setX(START_X);
		setY(START_Y);
	}

	public void act() {
		mX += mDx;
		if (mX <= 2) {
			mX = 2;
		}
		if (mX >= BOARD_WIDTH - 2*mWidth) {
			mX = BOARD_WIDTH - 2*mWidth;
		}
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_LEFT) {
			mDx = -2;
		}
		else if (key == KeyEvent.VK_RIGHT) {
			mDx = 2;
		}
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		if ((key == KeyEvent.VK_LEFT) || (key == KeyEvent.VK_RIGHT)) {
			mDx = 0;
		}
	}
}
