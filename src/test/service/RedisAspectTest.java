package test.service;

import test.model.Descriptions;
import test.model.Movies;
import org.junit.BeforeClass;
import org.junit.Test;
import test.bo.GetDescriptionsBo;
import test.dao.MoviesDao;
import test.utils.SpringUtil;

import java.util.List;

/**
 * Created by Duskrain on 2016/7/14.
 */
public class RedisAspectTest {

    private static MoviesDao moviesDao;

    @BeforeClass
    public static void beforeClass(){
        moviesDao = (MoviesDao)SpringUtil.getBean("moviesDao");
    }

    //@Test
    public void testGetMovieById(){
        Movies movie = moviesDao.getMovieById(10L);
        System.out.println(movie);
    }

    //@Test
    public void testGetMovieName(){
        /*Movies movie = moviesDao.getMovieName(10L,"name");
        System.out.println(movie);*/

        String movieName = moviesDao.getMovieName(10L);
        System.out.println(movieName);
    }

    @Test
    public void testGetDescriptions(){
        GetDescriptionsBo bo = new GetDescriptionsBo();
        bo.setStart(0L);
        List<Descriptions> descriptions = moviesDao.getDescriptions(bo);

        System.out.println(descriptions);
    }

}
