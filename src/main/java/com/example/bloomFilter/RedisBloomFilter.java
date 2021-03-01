package com.example.bloomFilter;

import com.google.common.hash.Funnels;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zhang
 * date: 2020/12/7 11:22
 * description:
 */
public class RedisBloomFilter {
    /**
     * 获取到Spring容器的stringRedisTemplate
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 保存到Redis的key的前缀
     */
    private static final String BF_KEY_PREFIX = "bf:";

    /**
     * 预计元素的数量n
     */
    private final int numApproxElements;
    /**
     * 误差率p
     */
    private final double fpp;
    /**
     * 哈希函数的个数k
     */
    private final int numHashFunctions;
    /**
     * 位数组的长度m
     */
    private final int bitmapLength;


    /**
     * 构造布隆过滤器。注意：在同一业务场景下，三个参数务必相同
     *
     * @param stringRedisTemplate
     * @param numApproxElements 预估元素数量
     * @param fpp               可接受的最大误差（假阳性率）
     */
    public RedisBloomFilter(StringRedisTemplate stringRedisTemplate, int numApproxElements, double fpp) {
        this.stringRedisTemplate = stringRedisTemplate;
        //获取预估数量n
        this.numApproxElements = numApproxElements;
        //获取误差率p
        this.fpp = fpp;
        //获取到位数组长度m
        bitmapLength = (int) (-numApproxElements * Math.log(fpp) / (Math.log(2) * Math.log(2)));
        //获取哈希函数个数k
        numHashFunctions = Math.max(1, (int) Math.round((double) bitmapLength / numApproxElements * Math.log(2)));
    }

    /**
     * 取得自动计算的最优哈希函数个数
     */
    public int getNumHashFunctions() {
        return numHashFunctions;
    }

    /**
     * 取得自动计算的最优Bitmap长度
     */
    public int getBitmapLength() {
        return bitmapLength;
    }

    public int getNumApproxElements() {
        return numApproxElements;
    }

    public double getFpp() {
        return fpp;
    }

    /**
     * 计算一个元素值哈希后映射到Bitmap的哪些bit上。
     *
     * @param element 元素值
     * @return bit下标的数组
     */
    private long[] getBitIndices(String element) {
        long[] indices = new long[numHashFunctions];

        byte[] bytes = Hashing.murmur3_128()
                .hashObject(element, Funnels.stringFunnel(Charset.forName("UTF-8")))
                .asBytes();

        long lowerHash = Longs.fromBytes(
                bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]
        );
        long upperHash = Longs.fromBytes(
                bytes[15], bytes[14], bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8]
        );

        long combinedHash = lowerHash;
        for (int i = 0; i < numHashFunctions; i++) {
            indices[i] = (combinedHash & Long.MAX_VALUE) % bitmapLength;
            combinedHash += upperHash;
        }

        return indices;
    }


    /**
     * 插入元素
     *
     * @param key       原始Redis键，会自动加上'bf:'前缀
     * @param element   元素值，字符串类型
     * @param expireDate 失效时间，在expireDate时间失效
     */
    public void insert(String key, String element, Date expireDate) {
        if (key == null || element == null) {
            throw new RuntimeException("键值均不能为空");
        }
        String actualKey = BF_KEY_PREFIX.concat(key);
        long[] bitIndices = getBitIndices(element);
        stringRedisTemplate.executePipelined((RedisCallback) connection -> {
            for (long index : bitIndices) {
                connection.setBit(actualKey.getBytes(), index, true);
            }
            return null;
        });
        //设置失效时间
        stringRedisTemplate.expireAt(actualKey,expireDate);
    }
    /**
     * 获取当天23点59分59秒毫秒数
     *
     * @return
     */
    public static Date getTwelveTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                23,
                59,
                59);
        return calendar.getTime();
    }

    /**
     * 检查元素在集合中是否（可能）存在
     *
     * @param key     原始Redis键，会自动加上'bf:'前缀
     * @param element 元素值，字符串类型
     */
    public boolean mayExist(String key, String element) {
        if (key == null || element == null) {
            throw new RuntimeException("键值均不能为空");
        }
        String actualKey = BF_KEY_PREFIX.concat(key);
        long[] bitIndices = getBitIndices(element);
        List<Object> list = stringRedisTemplate.executePipelined((RedisCallback<Boolean>) connection -> {
            for (long index : bitIndices) {
                connection.getBit(actualKey.getBytes(), index);
            }
            return null;
        });
        return !list.contains(Boolean.valueOf(false));
    }
}
