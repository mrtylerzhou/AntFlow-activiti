package org.openoa.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@TableName("t_bizdemo")
public class BizDemo {
    @TableId(value = "id", type = IdType.AUTO)
    public Integer id;
    public String flowkey;
    public String bizformjson;

    public String getFlowKey() {
        return flowkey;
    }
    public String getBizFormJson() {
        return bizformjson;
    }
    public void setFlowKey(String _flowkey) {
        this.flowkey = _flowkey;
    }
    public void setBizFormJson(String _bizformjson) {
        this.bizformjson = _bizformjson;
    }
}

