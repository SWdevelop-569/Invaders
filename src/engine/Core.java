package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import screen.*;

import javax.swing.*;

/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class Core {
	public static int forscore;//점수확인 설정 옵션
	public static int gamemode;//게임모드가 무엇인지  234(single) 567(multi)

	/** Width of current screen. */
	private static final int WIDTH = 448;
	/** Height of current screen. */
	private static final int HEIGHT = 520;
	/** Max fps of current screen. */
	private static final int FPS = 60;

	/** Max lives. */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	private static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Total number of levels. */
	private static final int NUM_LEVELS = 7;
	
	/** Difficulty settings for level 1. */
	private static GameSettings SETTINGS_LEVEL_1;
	/** Difficulty settings for level 2. */
	private static GameSettings SETTINGS_LEVEL_2;
	/** Difficulty settings for level 3. */
	private static GameSettings SETTINGS_LEVEL_3;
	/** Difficulty settings for level 4. */
	private static GameSettings SETTINGS_LEVEL_4;
	/** Difficulty settings for level 5. */
	private static GameSettings SETTINGS_LEVEL_5;
	/** Difficulty settings for level 6. */
	private static GameSettings SETTINGS_LEVEL_6;
	/** Difficulty settings for level 7. */
	private static GameSettings SETTINGS_LEVEL_7;
	
	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Difficulty settings list. */
	private static List<GameSettings> gameSettings;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;


	/**
	 * Test implementation.
	 * 
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();

		GameSettingLevel(2);
		
		GameState gameState;////////////////////////////////check
		MultiGameState multiGameState;

		int returnCode = 1;
		do {
			gameState = new GameState(1, 0, MAX_LIVES, 0, 0);
			multiGameState = new MultiGameState(1,0,0,MAX_LIVES,MAX_LIVES,0,0,0);
			switch (returnCode) {
			case 1:
				// Main menu.
				currentScreen = new TitleScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " title screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing title screen.");
				break;
			case 2:
				// Game & score.
				GameSettingLevel(gamemode);//난이도에 맞는 게임 세팅
				do {
					// One extra live every few levels.
					boolean bonusLife = gameState.getLevel()
							% EXTRA_LIFE_FRECUENCY == 0
							&& gameState.getLivesRemaining() < MAX_LIVES;
					
					currentScreen = new GameScreen(gameState,
							gameSettings.get(gameState.getLevel() - 1),
							bonusLife, width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " game screen at " + FPS + " fps.");
					frame.setScreen(currentScreen);
					LOGGER.info("Closing game screen.");

					gameState = ((GameScreen) currentScreen).getGameState();

					/**
					 * gamemode 전역 변수를 이용하여 난이도
					 */
					gameState = new GameState(gameState.getLevel() + 1,
							gameState.getScore(),
							gameState.getLivesRemaining(),
							gameState.getBulletsShot(),
							gameState.getShipsDestroyed());

				} while (gameState.getLivesRemaining() > 0
						&& gameState.getLevel() <= NUM_LEVELS);

				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " score screen at " + FPS + " fps, with a score of "
						+ gameState.getScore() + ", "
						+ gameState.getLivesRemaining() + " lives remaining, "
						+ gameState.getBulletsShot() + " bullets shot and "
						+ gameState.getShipsDestroyed() + " ships destroyed.");
				/**
				 * gamemode 전역 변수를 이용하여 화면
				 */
				currentScreen = new ScoreScreen(width, height, FPS, gameState);
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing score screen.");
				break;
			case 3:
				// High scoresSelect.
				currentScreen = new HighScoreSelectScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " high score screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing high score screen.");
				break;
//				// High scores.
//				currentScreen = new HighScoreScreen(width, height, FPS);
//				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
//						+ " high score screen at " + FPS + " fps.");
//				returnCode = frame.setScreen(currentScreen);
//				LOGGER.info("Closing high score screen.");
//				break;
			case 4:
				//reset scores
				if(JOptionPane.showConfirmDialog(null,"reset the scores?","confirm",JOptionPane.YES_NO_OPTION)
						== JOptionPane.YES_OPTION){
					try {
						getFileManager().resetHighScores();
						getInputManager().keys[32] = false;
						LOGGER.info("Reset");
					} catch (NumberFormatException | IOException e) {
						LOGGER.info("Didn't Reset");
					}

				}
				else {
					LOGGER.info("Didn't Choose to Reset");
				}
				currentScreen = new TitleScreen(width, height, FPS);
				returnCode = frame.setScreen(currentScreen);
				break;
			case 5:
				//mode select
				currentScreen = new PlayModeScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " play mode screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing play mode screen");
				break;
			case 6:
//				multi play mode
				GameSettingLevel(gamemode);//난이도에 맞는 게임 세팅
				do {
					// One extra live every few levels.
					int lives[] = multiGameState.getLivesRemaining();

					boolean bonusLife = multiGameState.getLevel()
							% EXTRA_LIFE_FRECUENCY == 0
							&& (lives[0] < MAX_LIVES || lives[1] < MAX_LIVES); // 오류 수정해야함

					currentScreen = new MulityGameScreen(multiGameState,
							gameSettings.get(multiGameState.getLevel() - 1),
							bonusLife, width, height, FPS);
					LOGGER.info("2P Starting " + WIDTH + "x" + HEIGHT
							+ " game screen at " + FPS + " fps.");
					frame.setScreen(currentScreen);
					LOGGER.info("Closing game screen.");

					multiGameState = ((MulityGameScreen) currentScreen).getGameState();
					/**
					 * gamemode 전역 변수를 이용하여 인게임 난이도 설정
					 */
					multiGameState = new MultiGameState(multiGameState.getLevel() + 1,
							multiGameState.getScore()[0],multiGameState.getScore()[1],
							multiGameState.getLivesRemaining()[0],multiGameState.getLivesRemaining()[1],
							multiGameState.getBulletsShot()[0],multiGameState.getBulletsShot()[1],
							multiGameState.getShipsDestroyed());

				} while ((multiGameState.getLivesRemaining()[0] > 0 || multiGameState.getLivesRemaining()[1]>0)
						&& multiGameState.getLevel() <= NUM_LEVELS);

				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " score screen at " + FPS + " fps, with a score of "
						+ multiGameState.getScore() + ", "
						+ multiGameState.getLivesRemaining() + " lives remaining, "
						+ multiGameState.getBulletsShot() + " bullets shot and "
						+ multiGameState.getShipsDestroyed() + " ships destroyed.");
				/**
				 * 밑에 줄이 게임 끝나면 화면띄우는 코드인데 2인용은 따로 저장되게 만들면 될듯하옵니다
				 */
				/**
				 * gamemode 전역 변수를 이용하여 점수저장, 화면 띄우기에 이용
				 */
				currentScreen = new ScoreScreen_2p(width, height, FPS, multiGameState,0);
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing score_2p screen.");
				break;
			case 7://2p점수저장을 위함
				currentScreen = new ScoreScreen_2p(width, height, FPS, multiGameState,1);
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing score_2p screen.");
				break;
			case 8:
					//highscore by mode
				currentScreen = new HighScoreScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " high score screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing high score screen.");
				break;
			default:
				break;
			}
		} while (returnCode != 0);

		fileHandler.flush();
		fileHandler.close();
		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 * 
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
			final int variance) {
		return new Cooldown(milliseconds, variance);
	}

	public static void GameSettingLevel(int mode){
		SetLevels(mode);
		gameSettings = new ArrayList<GameSettings>();
		gameSettings.add(SETTINGS_LEVEL_1);
		gameSettings.add(SETTINGS_LEVEL_2);
		gameSettings.add(SETTINGS_LEVEL_3);
		gameSettings.add(SETTINGS_LEVEL_4);
		gameSettings.add(SETTINGS_LEVEL_5);
		gameSettings.add(SETTINGS_LEVEL_6);
		gameSettings.add(SETTINGS_LEVEL_7);
	}


	public static void SetLevels(int mode){///게임 생성 전 난이도 생성먼저
		if(mode ==2||mode == 5){//A_easy(default)
			/** Difficulty settings for level 1. */
			SETTINGS_LEVEL_1 =
					new GameSettings(5, 4, 60, 2000);
			/** Difficulty settings for level 2. */
			SETTINGS_LEVEL_2 =
					new GameSettings(5, 5, 50, 2500);
			/** Difficulty settings for level 3. */
			SETTINGS_LEVEL_3 =
					new GameSettings(6, 5, 40, 1500);
			/** Difficulty settings for level 4. */
			SETTINGS_LEVEL_4 =
					new GameSettings(6, 6, 30, 1500);
			/** Difficulty settings for level 5. */
			SETTINGS_LEVEL_5 =
					new GameSettings(7, 6, 20, 1000);
			/** Difficulty settings for level 6. */
			SETTINGS_LEVEL_6 =
					new GameSettings(7, 7, 10, 1000);
			/** Difficulty settings for level 7. */
			SETTINGS_LEVEL_7 =
					new GameSettings(8, 7, 2, 500);
		}
		else if(mode == 3|| mode == 6){//B_normal
			/** Difficulty settings for level 1. */
			SETTINGS_LEVEL_1 =
					new GameSettings(5, 4, 60, 2000);
			/** Difficulty settings for level 2. */
			SETTINGS_LEVEL_2 =
					new GameSettings(5, 5, 50, 2500);
			/** Difficulty settings for level 3. */
			SETTINGS_LEVEL_3 =
					new GameSettings(6, 5, 40, 1500);
			/** Difficulty settings for level 4. */
			SETTINGS_LEVEL_4 =
					new GameSettings(6, 6, 30, 1500);
			/** Difficulty settings for level 5. */
			SETTINGS_LEVEL_5 =
					new GameSettings(7, 6, 20, 1000);
			/** Difficulty settings for level 6. */
			SETTINGS_LEVEL_6 =
					new GameSettings(7, 7, 10, 1000);
			/** Difficulty settings for level 7. */
			SETTINGS_LEVEL_7 =
					new GameSettings(8, 7, 2, 500);
		}
		else{//C_hard
			/** Difficulty settings for level 1. */
			SETTINGS_LEVEL_1 =
					new GameSettings(5, 4, 60, 2000);
			/** Difficulty settings for level 2. */
			SETTINGS_LEVEL_2 =
					new GameSettings(5, 5, 50, 2500);
			/** Difficulty settings for level 3. */
			SETTINGS_LEVEL_3 =
					new GameSettings(6, 5, 40, 1500);
			/** Difficulty settings for level 4. */
			SETTINGS_LEVEL_4 =
					new GameSettings(6, 6, 30, 1500);
			/** Difficulty settings for level 5. */
			SETTINGS_LEVEL_5 =
					new GameSettings(7, 6, 20, 1000);
			/** Difficulty settings for level 6. */
			SETTINGS_LEVEL_6 =
					new GameSettings(7, 7, 10, 1000);
			/** Difficulty settings for level 7. */
			SETTINGS_LEVEL_7 =
					new GameSettings(8, 7, 2, 500);
		}

	}
}