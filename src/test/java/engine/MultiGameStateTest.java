package engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultiGameStateTest {
    int score1;
    int score2;
    int level;
    int liveremaining1;
    int liveremaining2;
    int bshot1;
    int bshot2;
    int shipdestroyed;
    MultiGameState ms;

    @BeforeEach
    void init(){
        int level = 1;
        score1 = 100;
        score2 = 200;
        liveremaining1 = 3;
        liveremaining2 = 2;
        bshot1 = 5;
        bshot2 = 4;
        shipdestroyed = 2;
        ms = new MultiGameState(level,score1,score2,liveremaining1,liveremaining2,bshot1, bshot2, shipdestroyed);
    }
    @Test
    public void multiScore(){
        assertArrayEquals(new int[]{score1, score2}, ms.getScore());
    }
    @Test
    public void multiLive(){
        assertArrayEquals(new int[]{liveremaining1,liveremaining2}, ms.getLivesRemaining());
    }
    @Test
    public void multiBulletshot(){
        assertArrayEquals(new int[]{bshot1,bshot2}, ms.getBulletsShot());
    }
    @Test
    public void multishipsDestroyed(){
        assertEquals(shipdestroyed, ms.getShipsDestroyed());
    }
}