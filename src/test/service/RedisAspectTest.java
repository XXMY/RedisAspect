package test.service;

import cfw.model.Movies;
import org.junit.BeforeClass;
import org.junit.Test;
import test.dao.MoviesDao;
import test.utils.SpringUtil;

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

    @Test
    public void testGetMovieName(){
        String name = moviesDao.getMovieName(10L,"name");

        System.out.println(name);
    }
}
