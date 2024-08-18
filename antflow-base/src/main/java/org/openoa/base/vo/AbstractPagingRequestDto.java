package org.openoa.base.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import org.openoa.base.dto.PageDto;
import org.openoa.base.util.PageUtils;

/**
 * @Author TylerZhou
 * @since 0.5
 * @Version 1.0
 */
@Data
public class AbstractPagingRequestDto<TEntity> {
    private PageDto pageDto= PageUtils.getPageDto(new Page());
    private TEntity entity;
}
