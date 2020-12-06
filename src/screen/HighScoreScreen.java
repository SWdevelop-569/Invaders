package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import engine.Core;
import engine.Score;

import static engine.Core.forscore;

/**
 * Implements the high scores screen, it shows player records.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class HighScoreScreen extends Screen {

	/** List of past high scores. */
	private List<Score> highScores;
	String mode;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public HighScoreScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		this.returnCode = 1;

		try {
			switch (forscore){
				case 2://single-a
					this.highScores = Core.getFileManager().loadHighScores();
					mode="single-a";
					break;
				case 3://single-b
					this.highScores = Core.getFileManager().loadHighScores();
					mode="single-b";
					break;
				case 4://single-c
					this.highScores = Core.getFileManager().loadHighScores();
					mode="single-c";
					break;
				case 5://multi-a
					this.highScores = Core.getFileManager().loadHighScores_2p();
					mode="multi-a";
					break;
				case 6://multi-b
					this.highScores = Core.getFileManager().loadHighScores();
					mode="multi-b";
					break;
				case 7://multi-c
					this.highScores = Core.getFileManager().loadHighScores();
					mode="multi-c";
					break;
			}
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		draw();
		if (inputManager.isKeyDown(KeyEvent.VK_SPACE)
				&& this.inputDelay.checkFinished())
			this.isRunning = false;
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {

		drawManager.initDrawing(this);

		drawManager.drawHighScoreMenu(this,mode);
		drawManager.drawHighScores(this, this.highScores);

		drawManager.completeDrawing(this);
	}
}
