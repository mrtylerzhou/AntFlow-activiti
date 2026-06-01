package org.openoa;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@DisplayName("BpmnConf Edit Integration Tests")
class BpmnConfEditIT extends IntegrationBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("第三方账号申请 - edit success")
    void editThirdPartyAccountApplication() throws Exception {
        String json = "{\n" +
                "  \"bpmnName\": \"第三方账号申请\",\n" +
                "  \"bpmnCode\": \"PROJECT_AWIRG91\",\n" +
                "  \"bpmnType\": 1,\n" +
                "  \"formCode\": \"DSFZH_WMA\",\n" +
                "  \"remark\": \"\",\n" +
                "  \"effectiveStatus\": 0,\n" +
                "  \"deduplicationType\": 1,\n" +
                "  \"viewPageButtons\": {\"viewPageStart\": [], \"viewPageOther\": []},\n" +
                "  \"nodes\": [\n" +
                "    {\n" +
                "      \"nodeId\": \"HKNRG91\",\n" +
                "      \"nodeName\": \"李四审批\",\n" +
                "      \"nodeDisplayName\": \"李四\",\n" +
                "      \"nodeType\": 4,\n" +
                "      \"nodeFrom\": \"S1KRG91\",\n" +
                "      \"nodeTo\": [],\n" +
                "      \"setType\": 5,\n" +
                "      \"signType\": 1,\n" +
                "      \"isSignUp\": 1,\n" +
                "      \"directorLevel\": 1,\n" +
                "      \"noHeaderAction\": 0,\n" +
                "      \"error\": false,\n" +
                "      \"property\": {\n" +
                "        \"emplIds\": [\"2\"],\n" +
                "        \"emplList\": [{\"id\": \"2\", \"name\": \"李四\"}],\n" +
                "        \"roleIds\": [],\n" +
                "        \"roleList\": [],\n" +
                "        \"hrbpConfType\": 0,\n" +
                "        \"assignLevelGrade\": 0,\n" +
                "        \"signType\": 1,\n" +
                "        \"signUpType\": 1,\n" +
                "        \"afterSignUpWay\": 1\n" +
                "      },\n" +
                "      \"lfFieldControlVOs\": [],\n" +
                "      \"buttons\": {\"startPage\": [1], \"approvalPage\": [3,4,18,19,21], \"viewPage\": [0]},\n" +
                "      \"templateVos\": [],\n" +
                "      \"nodeProperty\": 5\n" +
                "    },\n" +
                "    {\n" +
                "      \"nodeId\": \"S1KRG91\",\n" +
                "      \"nodeName\": \"自己审批\",\n" +
                "      \"nodeDisplayName\": \"发起人自己\",\n" +
                "      \"nodeType\": 4,\n" +
                "      \"nodeFrom\": \"Gb2\",\n" +
                "      \"nodeTo\": [\"HKNRG91\"],\n" +
                "      \"setType\": 12,\n" +
                "      \"signType\": 1,\n" +
                "      \"isSignUp\": 1,\n" +
                "      \"directorLevel\": 1,\n" +
                "      \"noHeaderAction\": 0,\n" +
                "      \"error\": false,\n" +
                "      \"property\": {\n" +
                "        \"emplIds\": [],\n" +
                "        \"emplList\": [],\n" +
                "        \"roleIds\": [],\n" +
                "        \"roleList\": [],\n" +
                "        \"hrbpConfType\": 0,\n" +
                "        \"assignLevelGrade\": 0,\n" +
                "        \"signType\": 1,\n" +
                "        \"signUpType\": 1,\n" +
                "        \"afterSignUpWay\": 1\n" +
                "      },\n" +
                "      \"lfFieldControlVOs\": [],\n" +
                "      \"buttons\": {\"startPage\": [1], \"approvalPage\": [3,4,18,19,21], \"viewPage\": [0]},\n" +
                "      \"templateVos\": [],\n" +
                "      \"nodeProperty\": 12\n" +
                "    },\n" +
                "    {\n" +
                "      \"confId\": 1,\n" +
                "      \"nodeId\": \"Gb2\",\n" +
                "      \"nodeType\": 1,\n" +
                "      \"nodeProperty\": 1,\n" +
                "      \"nodeFrom\": \"\",\n" +
                "      \"prevId\": [],\n" +
                "      \"batchStatus\": 0,\n" +
                "      \"approvalStandard\": 2,\n" +
                "      \"nodeName\": \"发起人\",\n" +
                "      \"nodeDisplayName\": \"发起人\",\n" +
                "      \"isDeduplication\": 0,\n" +
                "      \"remark\": \"\",\n" +
                "      \"isDel\": 0,\n" +
                "      \"nodeTo\": [\"S1KRG91\"],\n" +
                "      \"buttons\": {\"startPage\": [], \"approvalPage\": [], \"viewPage\": null},\n" +
                "      \"conditionNodes\": []\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        mockMvc.perform(post("/bpmnConf/edit")
                        .header("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
