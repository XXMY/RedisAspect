package test.bo;

import cfw.redis.annotation.RedisEnd;
import cfw.redis.annotation.RedisStart;

/**
 * Created by Cfw on 2016/9/2.
 */
public class GetDescriptionsBo {

    @RedisStart
    private Long start;

    @RedisEnd
    private Long end;

    public GetDescriptionsBo(){
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }
}
