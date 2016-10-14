package test.dao;

import cfw.model.Descriptions;
import cfw.model.Movies;
import cfw.redis.annotation.*;
import cfw.redis.util.Direction;
import cfw.redis.util.KeyType;
import cfw.redis.util.ListOrder;
import cfw.redis.util.StoredSetOrder;
import org.springframework.stereotype.Repository;
import test.bo.GetDescriptionsBo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Duskrain on 2016/7/14.
 */
@Repository
public class MoviesDao {

    @RedisCacheable(key = "movie",keyType = KeyType.Hash)
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

    @RedisCacheable(key = "movie", keyType = KeyType.Hash)
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

   @RedisCacheable(key = "movie:10:descriptions",
            keyType = KeyType.List,
            direction = Direction.LEFT,
            listOrder = ListOrder.RANGE,
            expire = 0)
    public List<Descriptions> getDescriptions(@RedisStart Long start, @RedisEnd Long end){
        return this.createTestDescription();
    }

    @RedisCacheable(key = "movie:10:descriptions",
            keyType = KeyType.StoredSet,
            direction = Direction.LEFT,
            storedSetOrder = StoredSetOrder.ZRANGE,
            expire = 100)
    public List<Descriptions> getDescriptions(@RedisParam GetDescriptionsBo getDescriptionsBo){
        return this.createTestDescription();
    }


    /**
     * @author Fangwei_Cai
     * @time since 2016-10-14 17:29:20
     * @return
     */
    private List<Descriptions> createTestDescription(){
        List<Descriptions> descriptions = new ArrayList<>();
        for(int i=0;i<1;i++){
            Descriptions description = new Descriptions();
            description.setId((long)i);
            description.setAbstract_("习近平组织中央深改组会议-" + i);
            description.setDescription("习近平主持召开中央全面深化改革领导小组第二十六次会议-" + i);
            description.setIsdeleted(true);
            descriptions.add(description);
        }

        return descriptions;
    }

}
