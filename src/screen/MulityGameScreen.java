package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import engine.*;
import entity.*;

/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class MulityGameScreen extends Screen {

    /** Milliseconds until the screen accepts user input. */
    private static final int INPUT_DELAY = 6000;
    /** Bonus score for each life remaining at the end of the level. */
    private static final int LIFE_SCORE = 100;
    /** Minimum time between bonus ship's appearances. */
    private static final int BONUS_SHIP_INTERVAL = 20000;
    /** Maximum variance in the time between bonus ship's appearances. */
    private static final int BONUS_SHIP_VARIANCE = 10000;
    /** Time until bonus ship explosion disappears. */
    private static final int BONUS_SHIP_EXPLOSION = 500;
    /** Time from finishing the level to screen change. */
    private static final int SCREEN_CHANGE_INTERVAL = 1500;
    /** Height of the interface separation line. */
    private static final int SEPARATION_LINE_HEIGHT = 40;
    /** 정지버튼이 전에 눌렸였는지 아닌지 검사**/
    boolean isNextpressed = false;
    boolean isPausepressed = false;

    /** Current game difficulty settings. */
    private GameSettings gameSettings;
    /** Current difficulty level number. */
    private int level;
    /** Formation of enemy ships. */
    private EnemyShipFormation enemyShipFormation;
    /** Player's ship. */
    private Ship ship;
    private Ship ship2;//2p
    /** Bonus enemy ship that appears sometimes. */
    private EnemyShip enemyShipSpecial;
    /** Minimum time between bonus ship appearances. */
    private Cooldown enemyShipSpecialCooldown;
    /** Time until bonus ship explosion disappears. */
    private Cooldown enemyShipSpecialExplosionCooldown;
    /** Time from finishing the level to screen change. */
    private Cooldown screenFinishedCooldown;
    /** Set of all bullets fired by on screen ships. */
    private Set<Bullet> bullets;
    private Set<Bullet> bullets2;
    /** Current score. */
    private int score;
    private int score2;
    /** Player lives left. */
    private int lives;
    private int lives2;
    /** Total bullets shot by the player. */
    private int bulletsShot;
    private int bulletsShot2;
    /** Total ships destroyed by the player. */
    private int shipsDestroyed;
    /** Moment the game starts. */
    private long gameStartTime;
    /** Checks if the level is finished. */
    private boolean levelFinished;
    /** Checks if a bonus life is received. */
    private boolean bonusLife;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param multiGameState
     *            Current ßgamße state.
     * @param gameSettings
     *            Current game settings.
     * @param bonusLife
     *            Checks if a bonus life is awarded this level.
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public MulityGameScreen(final MultiGameState multiGameState,
                      final GameSettings gameSettings, final boolean bonusLife,
                      final int width, final int height, final int fps) {
        super(width, height, fps);
        //for 2ps~~~
        this.gameSettings = gameSettings;
        this.bonusLife = bonusLife;
        this.level = multiGameState.getLevel();
        this.score = multiGameState.getScore()[0];
        this.score2 = multiGameState.getScore()[1];
        this.lives = multiGameState.getLivesRemaining()[0];
        this.lives2 = multiGameState.getLivesRemaining()[1];
        if (this.bonusLife){
            this.lives++;this.lives2++;}
        this.bulletsShot = multiGameState.getBulletsShot()[0];
        this.bulletsShot2 = multiGameState.getBulletsShot()[1];
        this.shipsDestroyed = multiGameState.getShipsDestroyed();
    }

    /**
     * Initializes basic screen properties, and adds necessary elements.
     */
    public final void initialize() {
        super.initialize();

        enemyShipFormation = new EnemyShipFormation(this.gameSettings);
        enemyShipFormation.attach(this);
        this.ship = new Ship(this.width / 2, this.height - 30, Color.GREEN);
        this.ship2 = new Ship(this.width / 2, this.height - 30, Color.BLUE);//2[용 배 객체 생성
        // Appears each 10-30 seconds.
        this.enemyShipSpecialCooldown = Core.getVariableCooldown(
                BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
        this.enemyShipSpecialCooldown.reset();
        this.enemyShipSpecialExplosionCooldown = Core
                .getCooldown(BONUS_SHIP_EXPLOSION);
        this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
        this.bullets = new HashSet<Bullet>();
        this.bullets2 = new HashSet<Bullet>();


        // Special input delay / countdown.
        this.gameStartTime = System.currentTimeMillis();
        this.inputDelay = Core.getCooldown(INPUT_DELAY);
        this.inputDelay.reset();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();

        this.score += LIFE_SCORE * (this.lives - 1);
        this.score2 += LIFE_SCORE * (this.lives2 - 1);

        this.logger.info("Screen cleared with a score of \n1p:" + this.score + "2p:"+this.score2);

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        if (this.inputDelay.checkFinished() && !this.levelFinished) {
            //1p는 a,d와 spacebar로 공격하도록 설정
            if (!this.ship.isDestroyed()||this.lives>0) {
                boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_D);
                boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_A);

                // del 키로 next level 로 넘김
                boolean gameNext = inputManager.isKeyDown(KeyEvent.VK_BACK_SPACE);
                // esc 키로 pause 시킴
                boolean gamePause = inputManager.isKeyDown(KeyEvent.VK_ESCAPE);


                boolean isRightBorder = this.ship.getPositionX()
                        + this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
                boolean isLeftBorder = this.ship.getPositionX()
                        - this.ship.getSpeed() < 1;

                if (moveRight && !isRightBorder) {
                    this.ship.moveRight();
                }
                if (moveLeft && !isLeftBorder) {
                    this.ship.moveLeft();
                }

                if (gameNext){
                    if (!isNextpressed){
                        isNextpressed = true;
                        this.isRunning = false;
                        this.logger.info("skip the current level");
                    }
                }
                if (gamePause){
                    // 약간의 딜레이를 주어 esc키의 중복입력을 방지
                    try{
                        TimeUnit.MILLISECONDS.sleep(100);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (isPausepressed){
                        isPausepressed = false;
                        this.logger.info("resuming game screen");
                    }
                    else {
                        isPausepressed = true;
                        this.logger.info("Pausing game screen");
                    }
                }

                if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
                    if (this.ship.shoot(this.bullets))
                        this.bulletsShot++;
            }
            //2p는 방향키와 l로 공격하도록 설 정
            if (!this.ship2.isDestroyed() || this.lives2 > 0) {
                boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
                boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT);

                // del 키로 next level 로 넘김
                boolean gameNext = inputManager.isKeyDown(KeyEvent.VK_BACK_SPACE);
                // esc 키로 pause 시킴
                boolean gamePause = inputManager.isKeyDown(KeyEvent.VK_ESCAPE);

                boolean isRightBorder = this.ship2.getPositionX()
                        + this.ship2.getWidth() + this.ship2.getSpeed() > this.width - 1;
                boolean isLeftBorder = this.ship2.getPositionX()
                        - this.ship2.getSpeed() < 1;

                if (moveRight && !isRightBorder) {
                    this.ship2.moveRight();
                }
                if (moveLeft && !isLeftBorder) {
                    this.ship2.moveLeft();
                }
                if (gameNext){
                    if (!isNextpressed){
                        isNextpressed = true;
                        this.isRunning = false;
                        this.logger.info("skip the current level");
                    }
                }
                if (gamePause){
                    // 약간의 딜레이를 주어 esc키의 중복입력을 방지
                    try{
                        TimeUnit.MILLISECONDS.sleep(100);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (isPausepressed){
                        isPausepressed = false;
                        this.logger.info("resuming game screen");
                    }
                    else {
                        isPausepressed = true;
                        this.logger.info("Pausing game screen");
                    }
                }

                if (inputManager.isKeyDown(KeyEvent.VK_L))
                    if (this.ship2.shoot(this.bullets2))
                        this.bulletsShot2++;
            }

            if (this.enemyShipSpecial != null) {
                if (!this.enemyShipSpecial.isDestroyed())
                    this.enemyShipSpecial.move(2, 0);
                else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
                    this.enemyShipSpecial = null;

            }
            if (this.enemyShipSpecial == null
                    && this.enemyShipSpecialCooldown.checkFinished()) {
                this.enemyShipSpecial = new EnemyShip();
                this.enemyShipSpecialCooldown.reset();
                this.logger.info("A special ship appears");
            }
            if (this.enemyShipSpecial != null
                    && this.enemyShipSpecial.getPositionX() > this.width) {
                this.enemyShipSpecial = null;
                this.logger.info("The special ship has escaped");
            }

            this.ship.update();
            this.ship2.update();

            /**
             * 죽었을경우 색깔 검정 + 오른쪽위로 옮겨서 안보이게 만들기 ^^
             */
            if(this.lives <= 0){
                this.ship = new Ship(0,0,0,0,Color.black);
            }
            if(this.lives2 <= 0){
                this.ship2 = new Ship(0,0,0,0,Color.black);
            }


            // 입력된 그래픽들을 적용해줌.
            this.enemyShipFormation.update();
            this.enemyShipFormation.shoot(this.bullets);
            this.enemyShipFormation.shoot(this.bullets2);
        }

        manageCollisions();
        cleanBullets();
        draw();

        if ((this.enemyShipFormation.isEmpty() || (this.lives <= 0 && this.lives2 <= 0))//둘다 죽으면
                && !this.levelFinished) {
            this.levelFinished = true;
            this.screenFinishedCooldown.reset();
        }

        if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
            this.isRunning = false;

    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawEntity(this.ship, this.ship.getPositionX(),
                this.ship.getPositionY());
        drawManager.drawEntity(this.ship2, this.ship2.getPositionX(),
                this.ship2.getPositionY());//두번째 배도 그린다.
        if (this.enemyShipSpecial != null)
            drawManager.drawEntity(this.enemyShipSpecial,
                    this.enemyShipSpecial.getPositionX(),
                    this.enemyShipSpecial.getPositionY());

        enemyShipFormation.draw();

        for (Bullet bullet : this.bullets) {
            drawManager.drawEntity(bullet, bullet.getPositionX(),
                    bullet.getPositionY());
        }
        for (Bullet bullet : this.bullets2) {
            drawManager.drawEntity(bullet, bullet.getPositionX(),
                    bullet.getPositionY());
        }
        // Interface.
        drawManager.drawScore(this, this.score);
        drawManager.drawScore2(this, this.score2);//for2p
        drawManager.drawLives(this, this.lives);
        drawManager.drawLives2(this, this.lives2);
        drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

        // Countdown to game start.
        if (!this.inputDelay.checkFinished()) {
            int countdown = (int) ((INPUT_DELAY
                    - (System.currentTimeMillis()
                    - this.gameStartTime)) / 1000);
            drawManager.drawCountDown(this, this.level, countdown,
                    this.bonusLife);
            drawManager.drawHorizontalLine(this, this.height / 2 - this.height
                    / 12);
            drawManager.drawHorizontalLine(this, this.height / 2 + this.height
                    / 12);
        }

        drawManager.completeDrawing(this);

        // Show pause UI and stop time
        if (isPausepressed) {
            drawManager.drawHorizontalLine(this, this.height / 2 - this.height
                    / 12);
            drawManager.drawPause(this);
            drawManager.drawHorizontalLine(this, this.height / 2 + this.height
                    / 12);
            drawManager.completeDrawing(this);
            try {
                while (!inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cleans bullets that go off screen.
     */
    private void cleanBullets() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets) {
            bullet.update();
            if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
                    || bullet.getPositionY() > this.height)
                recyclable.add(bullet);
        }

        for (Bullet bullet : this.bullets2) {
            bullet.update();
            if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
                    || bullet.getPositionY() > this.height)
                recyclable.add(bullet);
        }
        this.bullets2.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    /**
     * Manages collisions between bullets and ships.
     */
    private void manageCollisions() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets){
            if (bullet.getSpeed() > 0) {
                if (checkCollision(bullet, this.ship) && !this.levelFinished) {
                    recyclable.add(bullet);
                    if (!this.ship.isDestroyed()) {
                        this.ship.destroy();
                        this.lives--;
                        this.logger.info("Hit on player ship, " + this.lives
                                + " lives remaining.");
                    }
                }
                if (checkCollision(bullet, this.ship2) && !this.levelFinished) {
                    recyclable.add(bullet);
                    if (!this.ship2.isDestroyed()) {
                        this.ship2.destroy();
                        this.lives2--;
                        this.logger.info("Hit on player ship, " + this.lives2
                                + " lives remaining.");
                    }
                }
            } else {
                for (EnemyShip enemyShip : this.enemyShipFormation)
                    if (!enemyShip.isDestroyed()
                            && checkCollision(bullet, enemyShip)) {
                        this.score += enemyShip.getPointValue();
                        this.shipsDestroyed++;
                        this.enemyShipFormation.destroy(enemyShip);
                        recyclable.add(bullet);
                    }
                if (this.enemyShipSpecial != null
                        && !this.enemyShipSpecial.isDestroyed()
                        && checkCollision(bullet, this.enemyShipSpecial)) {
                    this.score += this.enemyShipSpecial.getPointValue();
                    this.shipsDestroyed++;
                    this.enemyShipSpecial.destroy();
                    this.enemyShipSpecialExplosionCooldown.reset();
                    recyclable.add(bullet);
                }
            }
        }

        for (Bullet bullet : this.bullets2) {
            if (bullet.getSpeed() > 0) {
                if (checkCollision(bullet, this.ship) && !this.levelFinished) {
                    recyclable.add(bullet);
                    if (!this.ship.isDestroyed()) {
                        this.ship.destroy();
                        this.lives--;
                        this.logger.info("Hit on player ship, " + this.lives
                                + " lives remaining.");
                    }
                }
                if (checkCollision(bullet, this.ship2) && !this.levelFinished) {
                    recyclable.add(bullet);
                    if (!this.ship2.isDestroyed()) {
                        this.ship2.destroy();
                        this.lives2--;
                        this.logger.info("Hit on player ship, " + this.lives2
                                + " lives remaining.");
                    }
                }
            } else {
                for (EnemyShip enemyShip : this.enemyShipFormation)
                    if (!enemyShip.isDestroyed()
                            && checkCollision(bullet, enemyShip)) {
                        this.score2 += enemyShip.getPointValue();
                        this.shipsDestroyed++;
                        this.enemyShipFormation.destroy(enemyShip);
                        recyclable.add(bullet);
                    }
                if (this.enemyShipSpecial != null
                        && !this.enemyShipSpecial.isDestroyed()
                        && checkCollision(bullet, this.enemyShipSpecial)) {
                    this.score2 += this.enemyShipSpecial.getPointValue();
                    this.shipsDestroyed++;
                    this.enemyShipSpecial.destroy();
                    this.enemyShipSpecialExplosionCooldown.reset();
                    recyclable.add(bullet);
                }
            }
        }

        this.bullets.removeAll(recyclable);
        this.bullets2.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    /**
     * Checks if two entities are colliding.
     *
     * @param a
     *            First entity, the bullet.
     * @param b
     *            Second entity, the ship.
     * @return Result of the collision test.
     */
    private boolean checkCollision(final Entity a, final Entity b) {
        // Calculate center point of the entities in both axis.
        int centerAX = a.getPositionX() + a.getWidth() / 2;
        int centerAY = a.getPositionY() + a.getHeight() / 2;
        int centerBX = b.getPositionX() + b.getWidth() / 2;
        int centerBY = b.getPositionY() + b.getHeight() / 2;
        // Calculate maximum distance without collision.
        int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
        int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
        // Calculates distance.
        int distanceX = Math.abs(centerAX - centerBX);
        int distanceY = Math.abs(centerAY - centerBY);

        return distanceX < maxDistanceX && distanceY < maxDistanceY;
    }

    /**
     * Returns a GameState object representing the status of the game.
     *
     * @return Current game state.
     */
    public final MultiGameState getGameState() {
        return new MultiGameState(this.level, this.score,this.score2, this.lives,
                this.lives2, this.bulletsShot, this.bulletsShot2,this.shipsDestroyed);
    }
}