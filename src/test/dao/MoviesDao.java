package test.dao;

import cfw.model.Movies;
import cfw.redis.annotation.RedisCacheable;
import cfw.redis.annotation.RedisID;
import org.springframework.stereotype.Repository;

/**
 * Created by Duskrain on 2016/7/14.
 */
@Repository
public class MoviesDao {

    @RedisCacheable(key = "movie",keyType = RedisCacheable.KeyType.HASH)
    public Movies getMovieById(@RedisID Long mid){
        Movies movie = new Movies();
        movie.setId(mid);
        movie.setName("Test Movie");

        return movie;
    }
}
