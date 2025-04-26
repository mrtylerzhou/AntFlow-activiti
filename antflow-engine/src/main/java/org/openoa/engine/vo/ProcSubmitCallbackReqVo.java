package org.openoa.engine.vo;

import lombok.Data;

@Data
public class ProcSubmitCallbackReqVo extends CallbackReqVo {

    public ProcSubmitCallbackReqVo(){

    }
    public ProcSubmitCallbackReqVo(String formData){

        this.formData = formData;
    }
    /**
     * 表单数据Json字符串
     */
    private String formData;

}
