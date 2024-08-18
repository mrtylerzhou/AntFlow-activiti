package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.entity.UserMessage;

import java.io.Serializable;

/**
 * @author AntFlow
 * @since 0.5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendInfo implements Serializable {
    //email
    MailInfo mail;
    //sms
    MessageInfo messageInfo;
    //user system message
    UserMessage userMessage;
    //appPush
    BaseMsgInfo baseMsgInfo;


}
