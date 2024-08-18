package org.openoa.base.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;
import org.openoa.base.dto.PageDto;
import org.openoa.base.util.PageUtils;


/**
 *@Author JimuOffice
 * @Description request vo
 * @since 0.5
 * @Param
 * @return
 * @Version 1.0
 */
@Getter
@Setter
public class DetailRequestDto {
    private PageDto pageDto= PageUtils.getPageDto(new Page());
    private TaskMgmtVO taskMgmtVO;
}
