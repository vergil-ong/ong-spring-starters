package com.github.ong.util;

import java.util.Random;
import java.util.UUID;

/**
 * @Author vergil ong
 * @Email  865208597@qq.com
 * @Date   2021/4/28 14:11
 * @Description 随机工具类
 */
public class RandomUtil {

    private static final class RandomNumberGeneratorHolder {
        static final Random randomNumberGenerator = new Random();
    }

    public static int getRandomInt(int bound){
        return RandomNumberGeneratorHolder.randomNumberGenerator.nextInt(bound);
    }

    public static String getUUID(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
