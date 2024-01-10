package com.hdu;

import org.junit.Test;

import static org.junit.Assert.*;

import com.hdu.api.UserAgentPool;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String randomUserAgent = new UserAgentPool().getRandomUserAgent();
        System.out.println(randomUserAgent);
    }
}