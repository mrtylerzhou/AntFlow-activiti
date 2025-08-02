package org.openoa.engine.factory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.openoa.base.constant.enums.CallbackTypeEnum;
import org.openoa.base.exception.AFBizException;
import org.openoa.base.util.DateUtil;
import org.openoa.base.util.SecurityUtils;
import org.openoa.base.util.SpringBeanUtils;
import org.openoa.base.vo.BaseIdTranStruVo;
import org.openoa.base.vo.BpmVerifyInfoVo;
import org.openoa.base.vo.BpmnConfVo;
import org.openoa.base.entity.OutSideBpmCallbackUrlConf;
import org.openoa.engine.bpmnconf.service.biz.BpmVerifyInfoBizServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmBusinessPartyServiceImpl;
import org.openoa.engine.bpmnconf.service.impl.OutSideBpmCallbackUrlConfServiceImpl;
import org.openoa.engine.utils.JsonUtils;
import org.openoa.engine.vo.CallbackReqVo;
import org.openoa.engine.vo.CallbackRespVo;
import org.openoa.engine.vo.OutSideBpmAccessProcessRecordVo;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Data
public class ThirdPartyCallbackFactory {

    private static volatile ThirdPartyCallbackFactory thirdPartyCallbackFactory;

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private ThirdPartyCallbackFactory() {

    }

    /**
     * CallbackFactory构建方法
     *
     * @return
     */
    public static ThirdPartyCallbackFactory build() {

        if (thirdPartyCallbackFactory==null) {
            synchronized (ThirdPartyCallbackFactory.class){
                if(thirdPartyCallbackFactory==null){
                    thirdPartyCallbackFactory = new ThirdPartyCallbackFactory();
                }
            }
        }
        return thirdPartyCallbackFactory;
    }

    /**
     * 执行回调
     *
     * @param callbackTypeEnum
     * @param bpmnConfVo
     * @param <T>
     * @return
     */
    public <T> T doCallback(CallbackTypeEnum callbackTypeEnum, BpmnConfVo bpmnConfVo,
                            String processNum, String businessId) {

        //true代表回调调,false代表关闭回调
        boolean callBackSwitch = true;
        Environment environment = SpringBeanUtils.getBean(Environment.class);
        String callbackSwitchStr = environment.getProperty("outside.callback.switch");
        if(!StringUtils.isEmpty(callbackSwitchStr)){
            callBackSwitch = Boolean.parseBoolean(callbackSwitchStr);
        }


        CallbackReqVo callbackReqVo = null;

        String resultJson = StringUtils.EMPTY;

        //设置请求头
        Map<String, String> heads = Maps.newHashMap();

        try {

            if (ObjectUtils.isEmpty(bpmnConfVo.getBusinessPartyId())) {
                throw new AFBizException("业务方缺失，操作失败！");
            }

            CallbackAdaptor callbackAdaptor = getCallbackAdaptor(callbackTypeEnum.getBeanId());

            if (callbackAdaptor==null) {
                return null;
            }

            //关闭回调逻辑
            if (!callBackSwitch) {
                CallbackRespVo newRespObj = callbackAdaptor.getNewRespObj();
                if (!StringUtils.isEmpty(businessId)) {
                    Integer rendomNum = Math.abs(random.nextInt(Integer.MAX_VALUE));
                    newRespObj.setBusinessId(String.valueOf(rendomNum));
                }
                return (T) newRespObj;
            }

            callbackReqVo = callbackAdaptor.formatRequest(bpmnConfVo);

            //事件类型
            callbackReqVo.setEventType(callbackTypeEnum.getMark());

            //查询业务方标识
            OutSideBpmBusinessPartyServiceImpl bean = SpringBeanUtils.getBean(OutSideBpmBusinessPartyServiceImpl.class);
            String businessPartyMarkById = bean.getBusinessPartyMarkById(bpmnConfVo.getBusinessPartyId());

            //设置入参业务方标识
            callbackReqVo.setBusinessPartyMark(businessPartyMarkById);

            //表单编号
            callbackReqVo.setFormCode(formatFormCode(businessPartyMarkById, bpmnConfVo.getFormCode()));

            //流程编号
            callbackReqVo.setProcessNum(processNum);

            //如果流程编号不为空则根据流程编号查询流程路径
            if (!StringUtils.isEmpty(processNum)) {
                BpmVerifyInfoBizServiceImpl bpmVerifyInfoNewService = SpringBeanUtils.getBean(BpmVerifyInfoBizServiceImpl.class);
                boolean finishFlag = callbackTypeEnum.equals(CallbackTypeEnum.PROC_FINISH_CALL_BACK);
                List<BpmVerifyInfoVo> bpmVerifyInfoVos = bpmVerifyInfoNewService.getBpmVerifyInfoVos(processNum,finishFlag);
                callbackReqVo.setProcessRecord(bpmVerifyInfoVos
                        .stream()
                        .map(o -> OutSideBpmAccessProcessRecordVo
                                .builder()
                                .nodeName(o.getTaskName())
                                .approvalTime(Optional.ofNullable(o.getVerifyDate())
                                        .map(od -> DateUtil.SDF_DATETIME_PATTERN.format(od))
                                        .orElse(StringUtils.EMPTY))
                                .approvalStatusName(o.getVerifyStatusName())
                                .approvalUserName(o.getVerifyUserName())
                                .build())
                        .collect(Collectors.toList()));
            }

            //对接方返回业务编号
            callbackReqVo.setBusinessId(businessId);

            //获取当前登录人信息
            BaseIdTranStruVo loginedEmployee = SecurityUtils.getLogInEmpInfo();
            if (ObjectUtils.isEmpty(loginedEmployee)){
                loginedEmployee = new BaseIdTranStruVo();
            }
            //查询外部流程url配置获取"用户标识"及"api-key"
            OutSideBpmCallbackUrlConfServiceImpl outSideBpmCallbackUrlConfService = SpringBeanUtils.getBean(OutSideBpmCallbackUrlConfServiceImpl.class);
            OutSideBpmCallbackUrlConf outSideBpmCallbackUrlConf = outSideBpmCallbackUrlConfService.getOutSideBpmCallbackUrlConf(bpmnConfVo.getId(), bpmnConfVo.getBusinessPartyId());

            String url="";
            heads.put("central-service", getCurrentSysDomain());//域名
            if (outSideBpmCallbackUrlConf!=null) {
                url = outSideBpmCallbackUrlConf.getBpmFlowCallbackUrl();
                String apiClientId = outSideBpmCallbackUrlConf.getApiClientId();
                String apiClientSecret = outSideBpmCallbackUrlConf.getApiClientSecret();
                heads.put("api-client-id", apiClientId);//用户应用标识
                String jsonString = JSON.toJSONString(callbackReqVo);
                String md5Hex = DigestUtils.md5Hex(jsonString + apiClientSecret);
                String sign = Base64.encodeBase64String(md5Hex.getBytes(StandardCharsets.UTF_8));
                heads.put("api-workflow-sign",sign);
            }
            heads.put("sso-uid", loginedEmployee.getId());//当前登录人username
            heads.put("sso-name", URLEncoder.encode(loginedEmployee.getName(), "UTF-8"));//当前登录人真实姓名
            log.info("执行外部工作流回调,request：{} , processNumber:{} , callBackUrl:{} , 操作人：{} ,请求参数:{}",callbackTypeEnum.getDesc() , processNum, url, loginedEmployee.getName(), JsonUtils.transfer2JsonString(callbackReqVo));

            resultJson = doPost(url, heads, callbackReqVo);
            log.info("执行外部工作流回调,response：{} , processNumber:{} , callBackUrl:{} , 操作人：{} ,请求参数:{} , response:{}",callbackTypeEnum.getDesc() , processNum, url, loginedEmployee.getName(), JsonUtils.transfer2JsonString(callbackReqVo), resultJson);

            if (StringUtils.isEmpty(resultJson)) {
                return null;
            }

            JSONObject resultObject = JSON.parseObject(resultJson);

            Object status = resultObject.get("status");

            String successMark = "000000";

            if (!ObjectUtils.isEmpty(status) && successMark.equals(status.toString())) {
                CallbackRespVo callbackRespVo = callbackAdaptor.formatResponce(resultJson);
                if (callbackRespVo!=null) {

                    //设置出参业务方标识
                    callbackRespVo.setBusinessPartyMark(businessPartyMarkById);

                    //返回结果
                    return (T) callbackRespVo;
                }
            } else {

                Object message = resultObject.get("message");

                String messageStr = StringUtils.EMPTY;
                if (!ObjectUtils.isEmpty(message)) {
                    messageStr = message.toString();
                }

                if (!StringUtils.isEmpty(messageStr)) {
                    throw new AFBizException(messageStr);
                } else {
                    throw new AFBizException("工作流对外服务回调失败");
                }

            }

        } catch (AFBizException e) {
            log.error("工作流对外服务回调失败，回调类型：{}，请求头信息{}，入参：{}，出参：{}",
                    callbackTypeEnum.getMark(),
                    JSON.toJSONString(heads),
                    JSON.toJSONString(Optional.ofNullable(callbackReqVo).orElse(new CallbackReqVo())),
                    resultJson, e);
            //throw new JiMuBizException(e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("工作流对外服务回调失败，回调类型：{}，请求头信息{}，入参：{}，出参：{}",
                    callbackTypeEnum.getMark(),
                    JSON.toJSONString(heads),
                    JSON.toJSONString(Optional.ofNullable(callbackReqVo).orElse(new CallbackReqVo())),
                    resultJson, e);
            return null;
        }

        return null;
    }

    /**
     * 格式化表单编号
     *
     * @param businessPartyMarkById
     * @param formCode
     * @return
     */
    public String formatFormCode(String businessPartyMarkById, String formCode) {
        if (formCode.startsWith(businessPartyMarkById)) {
            return formCode;
        }
        return StringUtils.join(businessPartyMarkById, "_", formCode);
    }

    /**
     * 获得回调适配
     *
     * @param eventType
     * @return
     */
    private CallbackAdaptor getCallbackAdaptor(String eventType) {
        return SpringBeanUtils.getBean(eventType, CallbackAdaptor.class);
    }

    /**
     * 获得OA域名
     *
     * @return
     */
    private String getCurrentSysDomain() {

        HttpServletRequest request = getHttpServletRequest();

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        return basePath;
    }

    /**
     * 获得HttpServletRequest对象
     *
     * @return
     */
    private HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    /**
     * 调用POST接口调用
     *
     * @param url
     * @param heads
     * @param object
     * @return
     * @throws Exception
     */
    private String doPost(String url, Map<String, String> heads, Object object) throws Exception {
        String resultStr;

        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 创建http POST请求
        HttpPost httpPost = new HttpPost(url);

        CloseableHttpResponse response = null;

        try {

            //头部配置Map不为空则向头部追加参数
            if (!CollectionUtils.isEmpty(heads)) {
                heads.forEach((key, val) -> {
                    httpPost.addHeader(key, val);
                });
            }

            httpPost.addHeader(HTTP.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

            String input = object instanceof String? (String) object :JSON.toJSONString(object);

            StringEntity stringEntity = new StringEntity(input, "UTF-8");
            stringEntity.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

            //设置参数到请求对象中
            httpPost.setEntity(stringEntity);

            response = httpclient.execute(httpPost);

            resultStr = EntityUtils.toString(response.getEntity(), "UTF-8");

        } catch (Exception e) {
            log.error("工作流对外服务回调POST接口失败:", e);
            throw new Exception(e);
        } finally {
            if (response!=null) {
                response.close();
            }
            httpclient.close();
        }

        return resultStr;
    }

}
