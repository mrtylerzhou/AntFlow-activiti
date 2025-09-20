package org.openoa.base.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * verify Attachment vo
 * @author lidonghui
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BpmVerifyAttachmentVo implements Serializable {
    private String id;
    private Long verifyInfoId;
    private String filePath;
    private String newFileName;
    private String originalFileName;
    private Integer fileSize;
    private String fileType;
    private String fileUrl;
    private Date createTime;
    private String remark;
}
