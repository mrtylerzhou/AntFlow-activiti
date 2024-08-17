package org.openoa;

import org.junit.jupiter.api.Test;
import org.openoa.entity.BizDemo;
import org.openoa.service.impl.BizDemoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AntFlowApplicationTests {
    @Autowired
    private BizDemoServiceImpl _bizDemoImpl;
    @Test
    void contextLoads() {
    }
	@Test
	void testGetBizDemo() {
        String flowkey = "111";
        BizDemo demo = _bizDemoImpl.getBizDemoByFlowKey(flowkey);
		System.out.println(demo);
	}
    @Test
    void testAddBizDemo() {

        BizDemo _bizdemo=new BizDemo();
        _bizdemo.setFlowKey("2");
        _bizdemo.setBizFormJson("222222");
        Boolean result = _bizDemoImpl.addBizDemo(_bizdemo);
        System.out.println(result);
    }
}
