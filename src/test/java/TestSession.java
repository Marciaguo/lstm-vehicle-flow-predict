import com.udgrp.utils.DBUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

/**
 * @author kejw
 * @version V1.0
 * @Project ud-vehicle-flow-predict
 * @Description: TODO
 * @date 2018/3/19
 */
public class TestSession {
    @Test
    public void Test() {
        SqlSession session = DBUtil.getSession();
        System.out.println(session);
    }
}
