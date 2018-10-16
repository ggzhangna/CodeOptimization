package com.zn.args;


import com.zn.args.second.Args;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 测试类
 *
 * @author zhangna12
 * @date 2018-10-16
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ArgsTest extends TestCase {

    @Test
    public void missingStringArgumentTest() throws Exception {
        Args args = new Args("x",new String[]{"-x"});
        assertEquals(1,args.cardinality());
        assertTrue(args.has('x'));
        assertEquals("param",args.getString('x'));
    }
}
