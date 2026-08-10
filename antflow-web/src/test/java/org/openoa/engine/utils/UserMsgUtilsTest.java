package org.openoa.engine.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.entity.UserMessage;
import org.openoa.base.vo.BaseMsgInfo;
import org.openoa.base.vo.MailInfo;
import org.openoa.base.vo.MessageInfo;
import org.openoa.base.vo.UserMsgVo;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class UserMsgUtilsTest extends BaseTest {

    @Nested
    @DisplayName("buildBaseMsgInfo")
    class BuildBaseMsgInfoTest {
        @Test
        @DisplayName("should build BaseMsgInfo from UserMsgVo")
        void shouldBuildBaseMsgInfo() {
            UserMsgVo vo = UserMsgVo.builder()
                    .title("test title")
                    .content("test content")
                    .appPushUrl("http://app.url")
                    .build();

            BaseMsgInfo result = UserMsgUtils.buildBaseMsgInfo(vo);

            assertNotNull(result);
            assertEquals("test title", result.getMsgTitle());
            assertEquals("test content", result.getContent());
            assertEquals("http://app.url", result.getUrl());
        }

        @Test
        @DisplayName("should handle null fields gracefully")
        void shouldHandleNullFields() {
            UserMsgVo vo = UserMsgVo.builder().build();

            BaseMsgInfo result = UserMsgUtils.buildBaseMsgInfo(vo);

            assertNotNull(result);
            assertNull(result.getMsgTitle());
            assertNull(result.getContent());
            assertNull(result.getUrl());
        }

        @Test
        @DisplayName("should map title to msgTitle")
        void shouldMapTitleToMsgTitle() {
            UserMsgVo vo = UserMsgVo.builder()
                    .title("Approval Notification")
                    .build();

            BaseMsgInfo result = UserMsgUtils.buildBaseMsgInfo(vo);

            assertEquals("Approval Notification", result.getMsgTitle());
        }
    }

    @Nested
    @DisplayName("buildMessageInfo")
    class BuildMessageInfoTest {
        @Test
        @DisplayName("should build MessageInfo from UserMsgVo")
        void shouldBuildMessageInfo() {
            UserMsgVo vo = UserMsgVo.builder()
                    .mobile("13800138000")
                    .content("Your approval is needed")
                    .build();

            MessageInfo result = UserMsgUtils.buildMessageInfo(vo);

            assertNotNull(result);
            assertEquals("13800138000", result.getReceiver());
            assertEquals("Your approval is needed", result.getContent());
        }

        @Test
        @DisplayName("should handle null mobile gracefully")
        void shouldHandleNullMobile() {
            UserMsgVo vo = UserMsgVo.builder()
                    .content("test")
                    .build();

            MessageInfo result = UserMsgUtils.buildMessageInfo(vo);

            assertNotNull(result);
            assertNull(result.getReceiver());
            assertEquals("test", result.getContent());
        }
    }

    @Nested
    @DisplayName("buildMailInfo")
    class BuildMailInfoTest {
        @Test
        @DisplayName("should build MailInfo from UserMsgVo with email URL")
        void shouldBuildMailInfoWithEmailUrl() {
            UserMsgVo vo = UserMsgVo.builder()
                    .email("user@example.com")
                    .cc(new String[]{"cc1@example.com", "cc2@example.com"})
                    .title("Approval Required")
                    .content("Please review")
                    .ssoSessionDomain("sso.example.com")
                    .emailUrl("/detail?id=123")
                    .build();

            MailInfo result = UserMsgUtils.buildMailInfo(vo);

            assertNotNull(result);
            assertEquals("user@example.com", result.getReceiver());
            assertArrayEquals(new String[]{"cc1@example.com", "cc2@example.com"}, result.getCc());
            assertEquals("Approval Required", result.getTitle());
            assertTrue(result.getContent().contains("Please review"));
            assertTrue(result.getContent().contains("点击查看详情"));
        }

        @Test
        @DisplayName("should build MailInfo without email URL")
        void shouldBuildMailInfoWithoutEmailUrl() {
            UserMsgVo vo = UserMsgVo.builder()
                    .email("user@example.com")
                    .title("Test")
                    .content("Hello")
                    .build();

            MailInfo result = UserMsgUtils.buildMailInfo(vo);

            assertNotNull(result);
            assertEquals("user@example.com", result.getReceiver());
            assertEquals("Hello", result.getContent());
            assertFalse(result.getContent().contains("点击查看详情"));
        }

        @Test
        @DisplayName("should handle null cc gracefully")
        void shouldHandleNullCc() {
            UserMsgVo vo = UserMsgVo.builder()
                    .email("user@example.com")
                    .title("Test")
                    .content("Hello")
                    .build();

            MailInfo result = UserMsgUtils.buildMailInfo(vo);

            assertNull(result.getCc());
        }
    }

    @Nested
    @DisplayName("joinEmailUrl")
    class JoinEmailUrlTest {
        @Test
        @DisplayName("should join email URL with domain and content when emailUrl is not empty")
        void shouldJoinEmailUrlWhenNotEmpty() throws Exception {
            String result = invokeJoinEmailUrl("sso.example.com", "Hello ", "/detail?id=1");

            assertEquals("Hello <a href='http://sso.example.com#/detail?id=1'>点击查看详情</a>", result);
        }

        @Test
        @DisplayName("should return content only when emailUrl is empty")
        void shouldReturnContentOnlyWhenEmailUrlEmpty() throws Exception {
            String result = invokeJoinEmailUrl("sso.example.com", "Hello ", "");

            assertEquals("Hello ", result);
        }

        @Test
        @DisplayName("should return content only when emailUrl is null")
        void shouldReturnContentOnlyWhenEmailUrlNull() throws Exception {
            String result = invokeJoinEmailUrl("sso.example.com", "Hello ", null);

            assertEquals("Hello ", result);
        }

        private String invokeJoinEmailUrl(String systemDomain, String content, String emailUrl) throws Exception {
            Method method = UserMsgUtils.class.getDeclaredMethod("joinEmailUrl", String.class, String.class, String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, systemDomain, content, emailUrl);
        }
    }

    @Nested
    @DisplayName("buildUserMessage")
    class BuildUserMessageTest {
        @Test
        @DisplayName("should build UserMessage with all fields populated")
        void shouldBuildUserMessageWithAllFields() throws Exception {
            UserMsgVo vo = UserMsgVo.builder()
                    .userId("user123")
                    .title("Test Title")
                    .content("Test Content")
                    .url("http://msg.url")
                    .appPushUrl("http://app.url")
                    .taskId("task456")
                    .source(1)
                    .build();

            UserMessage result = invokeBuildUserMessage(vo);

            assertNotNull(result);
            assertEquals("user123", result.getUserId());
            assertEquals("Test Title", result.getTitle());
            assertEquals("Test Content", result.getContent());
            assertEquals("http://msg.url", result.getUrl());
            assertEquals("http://app.url", result.getAppUrl());
            assertEquals("task456", result.getNode());
            assertEquals(1, result.getSource());
            assertFalse(result.getIsRead());
        }

        @Test
        @DisplayName("should use content as title when title is null and source is 0")
        void shouldUseContentAsTitleWhenTitleNullAndSourceZero() throws Exception {
            UserMsgVo vo = UserMsgVo.builder()
                    .userId("user123")
                    .content("Test Content")
                    .source(0)
                    .build();

            UserMessage result = invokeBuildUserMessage(vo);

            assertEquals("Test Content", result.getTitle());
        }

        @Test
        @DisplayName("should use -999 as userId when userId is null")
        void shouldUseDefaultUserIdWhenNull() throws Exception {
            UserMsgVo vo = UserMsgVo.builder()
                    .content("Test")
                    .build();

            UserMessage result = invokeBuildUserMessage(vo);

            assertEquals("-999", result.getUserId());
        }

        @Test
        @DisplayName("should set source to 0 when source is null")
        void shouldSetSourceToZeroWhenNull() throws Exception {
            UserMsgVo vo = UserMsgVo.builder()
                    .userId("user123")
                    .content("Test")
                    .build();

            UserMessage result = invokeBuildUserMessage(vo);

            assertEquals(0, result.getSource());
        }

        @Test
        @DisplayName("should keep title when source is not 0")
        void shouldKeepTitleWhenSourceNotZero() throws Exception {
            UserMsgVo vo = UserMsgVo.builder()
                    .userId("user123")
                    .title("Original Title")
                    .content("Test Content")
                    .source(1)
                    .build();

            UserMessage result = invokeBuildUserMessage(vo);

            assertEquals("Original Title", result.getTitle());
        }

        private UserMessage invokeBuildUserMessage(UserMsgVo vo) throws Exception {
            Method method = UserMsgUtils.class.getDeclaredMethod("buildUserMessage", UserMsgVo.class);
            method.setAccessible(true);
            return (UserMessage) method.invoke(null, vo);
        }
    }
}
