package org.openoa.base.interf;

import org.openoa.base.vo.UserMsgVo;

import java.util.List;

public interface ProcessNoticeAdaptor extends AdaptorService{
    void sendMessageBatchByType(List<UserMsgVo> userMsgVos);
}
