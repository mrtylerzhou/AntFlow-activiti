package org.openoa.base.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class VoRound19ATest extends BaseTest {

    @Nested
    @DisplayName("BaseKeyValueStruVo")
    class BaseKeyValueStruVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            BaseKeyValueStruVo vo = new BaseKeyValueStruVo();
            assertNull(vo.getKey());
            assertNull(vo.getValue());
            assertNull(vo.getType());
            assertNull(vo.getRemark());
            assertNull(vo.getCreateTime());
            assertFalse(vo.getHasStarUserChooseModule());
            assertNull(vo.getProcessNotices());
            assertNull(vo.getTemplateVos());
        }

        @Test
        @DisplayName("all-arg constructor")
        void allArgConstructor() {
            Date now = new Date();
            BaseKeyValueStruVo vo = new BaseKeyValueStruVo("k1", "v1", "str", "rem", now, true, null, null);
            assertEquals("k1", vo.getKey());
            assertEquals("v1", vo.getValue());
            assertEquals("str", vo.getType());
            assertEquals("rem", vo.getRemark());
            assertEquals(now, vo.getCreateTime());
            assertTrue(vo.getHasStarUserChooseModule());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            BaseKeyValueStruVo vo = BaseKeyValueStruVo.builder()
                    .key("bk")
                    .value("bv")
                    .hasStarUserChooseModule(true)
                    .build();
            assertEquals("bk", vo.getKey());
            assertEquals("bv", vo.getValue());
            assertTrue(vo.getHasStarUserChooseModule());
        }

        @Test
        @DisplayName("setter overrides default hasStarUserChooseModule")
        void setterOverridesDefault() {
            BaseKeyValueStruVo vo = new BaseKeyValueStruVo();
            assertFalse(vo.getHasStarUserChooseModule());
            vo.setHasStarUserChooseModule(true);
            assertTrue(vo.getHasStarUserChooseModule());
        }
    }

    @Nested
    @DisplayName("IdsVo")
    class IdsVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            IdsVo vo = new IdsVo();
            assertNull(vo.getId());
            assertNull(vo.getPowerId());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            IdsVo vo = new IdsVo();
            vo.setId(42);
            vo.setPowerId("pw-1");
            assertEquals(42, vo.getId());
            assertEquals("pw-1", vo.getPowerId());
        }
    }

    @Nested
    @DisplayName("EnumerateVo")
    class EnumerateVoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            EnumerateVo vo = new EnumerateVo();
            assertNull(vo.getCode());
            assertNull(vo.getDesc());
            assertNull(vo.getIsInNode());
            assertNull(vo.getNodeType());
            assertNull(vo.getVo());
            assertNull(vo.getInformIdList());
            assertNull(vo.getProcessCode());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            EnumerateVo vo = EnumerateVo.builder()
                    .code(1)
                    .desc("test")
                    .isInNode(true)
                    .nodeType(4)
                    .processCode("PC001")
                    .build();
            assertEquals(1, vo.getCode());
            assertEquals("test", vo.getDesc());
            assertTrue(vo.getIsInNode());
            assertEquals(4, vo.getNodeType());
            assertEquals("PC001", vo.getProcessCode());
        }

        @Test
        @DisplayName("all-arg constructor")
        void allArgConstructor() {
            EnumerateVo vo = new EnumerateVo(2, "desc2", false, 1, null, Arrays.asList(1, 2), "PC002");
            assertEquals(2, vo.getCode());
            assertEquals("desc2", vo.getDesc());
            assertFalse(vo.getIsInNode());
            assertEquals(1, vo.getNodeType());
            assertEquals(Arrays.asList(1, 2), vo.getInformIdList());
        }
    }

    @Nested
    @DisplayName("SendParam")
    class SendParamTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            SendParam p = new SendParam();
            assertNull(p.getUserId());
            assertNull(p.getTitle());
            assertNull(p.getContent());
            assertNull(p.getUrl());
            assertNull(p.getNode());
            assertNull(p.getParams());
            assertNull(p.getUrlParams());
            assertNull(p.getAppUrl());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            UrlParams urlParams = new UrlParams();
            SendParam p = SendParam.builder()
                    .userId("u1")
                    .title("t1")
                    .content("c1")
                    .url("http://x")
                    .node("n1")
                    .params("p1")
                    .urlParams(urlParams)
                    .appUrl("app://y")
                    .build();
            assertEquals("u1", p.getUserId());
            assertEquals("t1", p.getTitle());
            assertEquals("c1", p.getContent());
            assertEquals("http://x", p.getUrl());
            assertEquals("n1", p.getNode());
            assertEquals("p1", p.getParams());
            assertSame(urlParams, p.getUrlParams());
            assertEquals("app://y", p.getAppUrl());
        }
    }

    @Nested
    @DisplayName("UrlParams")
    class UrlParamsTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            UrlParams p = new UrlParams();
            assertNull(p.getBusinessId());
            assertNull(p.getCode());
            assertNull(p.getNodeType());
            assertNull(p.getTaskId());
            assertNull(p.getNewsId());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            UrlParams p = new UrlParams();
            p.setBusinessId("b1");
            p.setCode("c1");
            p.setNodeType("2");
            p.setTaskId("t1");
            p.setNewsId("n1");
            assertEquals("b1", p.getBusinessId());
            assertEquals("c1", p.getCode());
            assertEquals("2", p.getNodeType());
            assertEquals("t1", p.getTaskId());
            assertEquals("n1", p.getNewsId());
        }
    }

    @Nested
    @DisplayName("Entrust")
    class EntrustTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            Entrust e = new Entrust();
            assertNull(e.getBeginTime());
            assertNull(e.getPowerId());
            assertNull(e.getEndTime());
            assertNull(e.getSender());
            assertNull(e.getName());
            assertNull(e.getId());
            assertNull(e.getReceiverId());
            assertNull(e.getReceiverName());
            assertNull(e.getCreateTime());
        }

        @Test
        @DisplayName("setter and getter")
        void setterGetter() {
            Date now = new Date();
            Entrust e = new Entrust();
            e.setBeginTime(now);
            e.setPowerId("pw1");
            e.setEndTime(now);
            e.setSender(1);
            e.setName("test");
            e.setId(10);
            e.setReceiverId("r1");
            e.setReceiverName("RName");
            e.setCreateTime(now);
            assertEquals(now, e.getBeginTime());
            assertEquals("pw1", e.getPowerId());
            assertEquals(1, e.getSender());
            assertEquals("test", e.getName());
            assertEquals(10, e.getId());
            assertEquals("r1", e.getReceiverId());
            assertEquals("RName", e.getReceiverName());
        }
    }

    @Nested
    @DisplayName("MessageInfo")
    class MessageInfoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            MessageInfo mi = new MessageInfo();
            assertNull(mi.getSender());
            assertNull(mi.getReceiver());
            assertNull(mi.getContent());
            assertNull(mi.getSign());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            MessageInfo mi = MessageInfo.builder()
                    .sender("s1")
                    .receiver("13800001111")
                    .content("hello")
                    .sign("sign")
                    .build();
            assertEquals("s1", mi.getSender());
            assertEquals("13800001111", mi.getReceiver());
            assertEquals("hello", mi.getContent());
            assertEquals("sign", mi.getSign());
        }
    }

    @Nested
    @DisplayName("MailInfo")
    class MailInfoTest {

        @Test
        @DisplayName("no-arg constructor defaults")
        void noArgDefaults() {
            MailInfo mi = new MailInfo();
            assertNull(mi.getReceiver());
            assertNull(mi.getReceivers());
            assertNull(mi.getContent());
            assertNull(mi.getTitle());
            assertNull(mi.getCc());
            assertNull(mi.getFileName());
            assertNull(mi.getFile());
            assertNull(mi.getFileInputStream());
        }

        @Test
        @DisplayName("builder pattern")
        void builder() {
            MailInfo mi = MailInfo.builder()
                    .receiver("a@b.com")
                    .receivers(Arrays.asList("x@y.com"))
                    .content("body")
                    .title("subj")
                    .cc(new String[]{"cc@d.com"})
                    .fileName("att.txt")
                    .build();
            assertEquals("a@b.com", mi.getReceiver());
            assertEquals(1, mi.getReceivers().size());
            assertEquals("body", mi.getContent());
            assertEquals("subj", mi.getTitle());
            assertArrayEquals(new String[]{"cc@d.com"}, mi.getCc());
            assertEquals("att.txt", mi.getFileName());
        }
    }
}
