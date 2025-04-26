package org.openoa.engine.vo;

import lombok.Data;

@Data
public class ProcBaseCallBackVo extends CallbackReqVo {

    public ProcBaseCallBackVo() {

    }
    public ProcBaseCallBackVo(String formData) {

        this.formData = formData;
    }
    /**
     * 表单数据Json字符串
     */
    private String formData;

}
