package org.openoa.base.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openoa.BaseTest;
import org.openoa.base.entity.BpmnNode;
import org.openoa.base.entity.jsonconf.BpmnNodeConfigJson;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BpmnNodeVoTest extends BaseTest {

    @Nested
    @DisplayName("setPrevId")
    class SetPrevIdTest {
        @Test
        @DisplayName("should set prevId and sync nodeFroms")
        void shouldSyncNodeFroms() {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.setPrevId(Arrays.asList("node1", "node2", "node3"));
            assertEquals("node1,node2,node3", vo.getNodeFroms());
            assertEquals(3, vo.getPrevId().size());
        }

        @Test
        @DisplayName("should set single prevId")
        void shouldSetSinglePrevId() {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.setPrevId(Arrays.asList("start"));
            assertEquals("start", vo.getNodeFroms());
        }

        @Test
        @DisplayName("should not sync nodeFroms for empty prevId")
        void shouldNotSyncForEmpty() {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.setNodeFroms("original");
            vo.setPrevId(Arrays.asList());
            assertEquals("original", vo.getNodeFroms());
        }
    }

    @Nested
    @DisplayName("setNodeFroms")
    class SetNodeFromsTest {
        @Test
        @DisplayName("should set nodeFroms and sync prevId")
        void shouldSyncPrevId() {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.setNodeFroms("node1,node2");
            assertEquals(2, vo.getPrevId().size());
            assertEquals("node1", vo.getPrevId().get(0));
            assertEquals("node2", vo.getPrevId().get(1));
        }

        @Test
        @DisplayName("should handle single nodeFroms")
        void shouldHandleSingle() {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.setNodeFroms("start");
            assertEquals(1, vo.getPrevId().size());
            assertEquals("start", vo.getPrevId().get(0));
        }

        @Test
        @DisplayName("should not sync prevId for empty nodeFroms")
        void shouldNotSyncForEmpty() {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.setPrevId(Arrays.asList("original"));
            vo.setNodeFroms("");
            assertEquals(Arrays.asList("original"), vo.getPrevId());
        }
    }

    @Nested
    @DisplayName("setOrAddLabelList")
    class SetOrAddLabelListTest {
        @Test
        @DisplayName("should create list when null and add label")
        void shouldCreateListWhenNull() {
            BpmnNodeVo vo = new BpmnNodeVo();
            assertNull(vo.getLabelList());
            BpmnNodeLabelVO label = new BpmnNodeLabelVO();
            label.setLabelName("test");
            vo.setOrAddLabelList(label);
            assertNotNull(vo.getLabelList());
            assertEquals(1, vo.getLabelList().size());
        }

        @Test
        @DisplayName("should append to existing list")
        void shouldAppendToExisting() {
            BpmnNodeVo vo = new BpmnNodeVo();
            BpmnNodeLabelVO label1 = new BpmnNodeLabelVO();
            label1.setLabelName("label1");
            BpmnNodeLabelVO label2 = new BpmnNodeLabelVO();
            label2.setLabelName("label2");
            vo.setOrAddLabelList(label1);
            vo.setOrAddLabelList(label2);
            assertEquals(2, vo.getLabelList().size());
        }
    }

    @Nested
    @DisplayName("getOrCreateNodeConfigJson")
    class GetOrCreateNodeConfigJsonTest {
        @Test
        @DisplayName("should create new config when null")
        void shouldCreateWhenNull() {
            BpmnNodeVo vo = new BpmnNodeVo();
            assertNull(vo.getNodeConfigJsonObj());
            BpmnNodeConfigJson config = vo.getOrCreateNodeConfigJson();
            assertNotNull(config);
            assertSame(config, vo.getNodeConfigJsonObj());
        }

        @Test
        @DisplayName("should return existing config")
        void shouldReturnExisting() {
            BpmnNodeVo vo = new BpmnNodeVo();
            BpmnNodeConfigJson first = vo.getOrCreateNodeConfigJson();
            BpmnNodeConfigJson second = vo.getOrCreateNodeConfigJson();
            assertSame(first, second);
        }
    }

    @Nested
    @DisplayName("serializeNodeConfigJson")
    class SerializeNodeConfigJsonTest {
        @Test
        @DisplayName("should return null when nodeConfigJsonObj is null")
        void shouldReturnNullWhenNull() {
            BpmnNodeVo vo = new BpmnNodeVo();
            assertNull(vo.serializeNodeConfigJson());
        }

        @Test
        @DisplayName("should return JSON string when nodeConfigJsonObj is set")
        void shouldReturnJsonWhenSet() {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.getOrCreateNodeConfigJson();
            String json = vo.serializeNodeConfigJson();
            assertNotNull(json);
            assertTrue(json.length() > 0);
        }
    }

    @Nested
    @DisplayName("Jackson serialization")
    class JacksonSerializationTest {
        private final ObjectMapper mapper = new ObjectMapper();

        @Test
        @DisplayName("should not include orCreateNodeConfigJson in JSON output")
        void shouldNotIncludeOrCreateNodeConfigJson() throws Exception {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.getOrCreateNodeConfigJson();
            String json = mapper.writeValueAsString(vo);
            assertFalse(json.contains("orCreateNodeConfigJson"),
                    "JSON should not contain 'orCreateNodeConfigJson' field, but got: " + json);
        }

        @Test
        @DisplayName("should not include nodeConfigJsonObj in JSON output")
        void shouldNotIncludeNodeConfigJsonObj() throws Exception {
            BpmnNodeVo vo = new BpmnNodeVo();
            vo.getOrCreateNodeConfigJson();
            String json = mapper.writeValueAsString(vo);
            assertFalse(json.contains("nodeConfigJsonObj"),
                    "JSON should not contain 'nodeConfigJsonObj' field, but got: " + json);
        }

        @Test
        @DisplayName("BeanUtils.copyProperties should parse nodeConfigJson into nodeConfigJsonObj")
        void beanUtilsShouldParseNodeConfigJson() {
            BpmnNode node = new BpmnNode();
            node.setNodeConfigJson("{\"approverConf\":null,\"conditionsConf\":null,\"buttonSignConf\":null,\"templateConf\":null,\"lowCodeConf\":null}");
            BpmnNodeVo vo = new BpmnNodeVo();
            BeanUtils.copyProperties(node, vo);
            assertNotNull(vo.getNodeConfigJsonObj(),
                    "nodeConfigJsonObj should be populated from nodeConfigJson via BeanUtils.copyProperties");
        }

        @Test
        @DisplayName("BeanUtils.copyProperties should handle null nodeConfigJson gracefully")
        void beanUtilsShouldHandleNullNodeConfigJson() {
            BpmnNode node = new BpmnNode();
            node.setNodeConfigJson(null);
            BpmnNodeVo vo = new BpmnNodeVo();
            BeanUtils.copyProperties(node, vo);
            assertNull(vo.getNodeConfigJsonObj());
        }
    }
}
