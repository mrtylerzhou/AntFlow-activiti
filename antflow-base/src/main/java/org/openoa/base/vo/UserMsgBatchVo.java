package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.constant.enums.MessageSendTypeEnum;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMsgBatchVo {

    /**
     * user message vo
     */
    public UserMsgVo userMsgVo;

    /**
     * message types
     */
    public List<MessageSendTypeEnum> messageSendTypeEnums;
}
