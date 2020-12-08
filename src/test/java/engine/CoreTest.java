package engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoreTest {
    @Test
    public void test1(){
        String check = new String();
        check = "testcheck";
        assertEquals("testcheck", check);
    }
}