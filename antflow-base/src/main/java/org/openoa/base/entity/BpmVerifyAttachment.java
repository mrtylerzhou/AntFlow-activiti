package org.openoa.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openoa.base.interf.TenantField;

import java.io.Serializable;
import java.util.Date;

/**
 * verify attachment
 * @author lidonghui
 * @since 0.5
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("bpm_verify_attachment")
public class BpmVerifyAttachment implements TenantField, Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("verify_info_id")
    private Long verifyInfoId;
    @TableField("file_path")
    private String filePath;
    @TableField("new_file_name")
    private String newFileName;
    @TableField("original_file_name")
    private String originalFileName;
    @TableField("file_size")
    private Integer fileSize;
    @TableField("file_type")
    private String fileType;
    @TableField("file_url")
    private String fileUrl;
    @TableField("create_time")
    private Date createTime;
    @TableField("remark")
    private String remark;
    @TableField("is_del")
    private Integer isDel;
    @TableField("tenant_id")
    private String tenantId;
}