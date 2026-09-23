package by.parakhnevich.media.redis.key;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.security.SecureRandom;
import java.time.Duration;

@ApplicationScoped
public class MediaKeyStore {

    private static final String PREFIX = "media:key:";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final HashCommands<String, String, MediaKey> hash;
    private final KeyCommands<String> keys;

    @Inject
    public MediaKeyStore(RedisDataSource dataSource) {
        this.hash = dataSource.hash(MediaKey.class);
        this.keys = dataSource.key();
    }

    public void store(String key, MediaKey value, Duration ttl) {
        String redisKey = PREFIX + key;
        hash.hset(redisKey, "mediaKey", value);
        keys.expire(redisKey, ttl);
    }

    public MediaKey consume(String key) {
        String redisKey = PREFIX + key;
        MediaKey value = hash.hget(redisKey, "mediaKey");
        if (value == null) {
            return null;
        }
        keys.del(redisKey);
        return value;
    }

    public MediaKey get(String key) {
        return hash.hget(PREFIX + key, "mediaKey");
    }

}
