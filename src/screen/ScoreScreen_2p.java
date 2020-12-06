package screen;

import engine.*;
import sun.rmi.runtime.Log;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen_2p extends Screen {
	private int who;
	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 7;
	/** Code of first mayus character. */
	private static final int FIRST_CHAR = 65;
	/** Code of last mayus character. */
	private static final int LAST_CHAR = 90;

	/** Current score. */
	private int score[];
	/** Player lives left. */
	private int livesRemaining[];
	/** Total bullets shot by the player. */
	private int bulletsShot[];
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** List of past high scores. */
	private List<Score> highScores;
	/** Checks if current score is a new high score. */
	private boolean isNewRecord[] = new boolean[2];
	/** Player name for record input. */
	private char[] name;
	/** Character of players name selected for change. */
	private int nameCharSelected;
	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;
	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *            Current game state.
	 */
	public ScoreScreen_2p(final int width, final int height, final int fps,
						  final MultiGameState gameState, int who) {
		super(width, height, fps);
		this.who = who;
		this.score = gameState.getScore();
		this.livesRemaining = gameState.getLivesRemaining();
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.isNewRecord[0] = false;
		this.isNewRecord[1] = false;
		this.name = "AAA".toCharArray();
		this.nameCharSelected = 0;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();

		try {
			int i = 1;
			this.highScores = Core.getFileManager().loadHighScores_2p();//나중에 highscore를 파일별로 나눠줘야함
			if(this.score[0] < this.score[1]) i=2;
			if (highScores.size() < MAX_HIGH_SCORE_NUM//1p가 신기록인지 검사
					|| highScores.get(highScores.size() - i).getScore()
					< this.score[0])
				this.isNewRecord[0] = true;
			i=1;
			if(this.score[0] >= this.score[1] && isNewRecord[0]) i=2;
			if (highScores.size() < MAX_HIGH_SCORE_NUM//2p가 신기록인지 검사
					|| highScores.get(highScores.size() - i).getScore()
					< this.score[1])
				this.isNewRecord[1] = true;

		} catch (IOException e) {
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
			draw(who);
			if (this.inputDelay.checkFinished()) {

				if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
					// Return to main menu.
					if(who == 1) {//두번째에 esc면 나가줌
						who =0;//다음을 위해 0으로 다시
						this.returnCode = 1;
						this.isRunning = false;
						if (this.isNewRecord[who])
							saveScore(who);
					}
					else{//첫번째에는 1p입력받음
						who =1;//2p차례
						this.returnCode = 7;
						this.isRunning = false;
						if (this.isNewRecord[who])
							saveScore(who);
					}
				} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
					// Play again.
					if(who == 1) {//두번째에 스페이스
						who=0;//다음을 위해 0으로 다시
						this.returnCode = 6;
						this.isRunning = false;
						if (this.isNewRecord[who])
							saveScore(who);
					}
					else{
						who=1;
						this.returnCode = 7;
						this.isRunning = false;
						if (this.isNewRecord[who])
							saveScore(who);
					}

				}

				if (this.isNewRecord[who] && this.selectionCooldown.checkFinished()) {
					if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
						this.nameCharSelected = this.nameCharSelected == 2 ? 0
								: this.nameCharSelected + 1;
						this.selectionCooldown.reset();
					}
					if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
						this.nameCharSelected = this.nameCharSelected == 0 ? 2
								: this.nameCharSelected - 1;
						this.selectionCooldown.reset();
					}
					if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
						this.name[this.nameCharSelected] =
								(char) (this.name[this.nameCharSelected]
										== LAST_CHAR ? FIRST_CHAR
										: this.name[this.nameCharSelected] + 1);
						this.selectionCooldown.reset();
					}
					if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
						this.name[this.nameCharSelected] =
								(char) (this.name[this.nameCharSelected]
										== FIRST_CHAR ? LAST_CHAR
										: this.name[this.nameCharSelected] - 1);
						this.selectionCooldown.reset();
					}
				}
			}
	}

	/**
	 * Saves the score as a high score.
	 */
	private void saveScore(int who) {//확인도 highscore 확인
		//highSCores를 scores_2p에서 받아와야함
		highScores.add(new Score(new String(this.name), score[who]));
		Collections.sort(highScores);
		if (highScores.size() > MAX_HIGH_SCORE_NUM)
			highScores.remove(highScores.size() - 1);

		try {
			Core.getFileManager().saveHighScores_2p(highScores);
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw(int who) {//1,2p 같이쓰기 위함
		drawManager.initDrawing(this);

		drawManager.drawGameOver_2p(this, this.inputDelay.checkFinished(),
				this.isNewRecord[who],who);
		drawManager.drawResults(this, this.score[who], this.livesRemaining[who],
				this.shipsDestroyed, (float) this.shipsDestroyed
						/ this.bulletsShot[who], this.isNewRecord[who]);

		if (this.isNewRecord[who])
			drawManager.drawNameInput(this, this.name, this.nameCharSelected);

		drawManager.completeDrawing(this);
	}
}
