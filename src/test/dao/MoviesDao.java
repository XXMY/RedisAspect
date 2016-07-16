package test.dao;

import cfw.model.Descriptions;
import cfw.model.Movies;
import cfw.redis.annotation.RedisCacheable;
import cfw.redis.annotation.RedisField;
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
        movie.setName("Test Movie2");

        Descriptions description = new Descriptions();
        description.setId(101L);
        description.setAbstract_("description_abstract");
        description.setDescription("description_description");
        description.setIsdeleted(true);

        movie.setDescription(description);

        return movie;
    }

    @RedisCacheable(key = "movie", keyType = RedisCacheable.KeyType.HASH)
    public Movies getMovieName(@RedisID Long mid,@RedisField String field){
        Movies movie = new Movies();
        movie.setName("叶问3");
        movie.setId(mid);

        return movie;
    }

    @RedisCacheable(key = "movie:name")
    public String getMovieName(@RedisID  Long mid){
        return "YE WEN";
    }
}
