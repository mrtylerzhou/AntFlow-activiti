package org.openoa.base.vo;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author AntFlow
 * @since  0.0.1
 */
@Data
public class DataVo implements Serializable {

    private List<IdsVo> ids;
    private String sender;
    private String receiverId;
    /**
     * entrust user name
     */
    private String receiverName;

    /**
     * start time
     */
    @JSONField(format = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date beginTime;

    /**
     * end time
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
}
