package cfw.redis.util;

/**
 * Created by Duskrain on 2016/10/10.
 */
public enum StoredSetOrder {
    ZADD,
    ZCARD,
    ZCOUNT,
    ZINCRBY,
    ZRANGEBYSCORE,
    ZRANK,
    ZREM,
    ZREMRANGEBYRANK,
    ZREMRANGEBYSCORE,
    ZREVRANGE,
    ZREVRANGEBYSCORE,
    ZREVRANK,
    ZSCORE,
    ZUNIONSTORE,
    ZINTERSTORE,
    ZSCAN
}
