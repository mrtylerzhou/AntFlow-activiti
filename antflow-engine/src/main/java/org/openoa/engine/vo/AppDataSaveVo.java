package org.openoa.engine.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * app version related data save vo
 *
 * @see org.openoa.base.constant.enums.AppApplicationType
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppDataSaveVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * version id
     */
    private Long versionId;

    /**
     * related data type(1:app 2:app data 3:quick entry)
     */
    private Integer type;

    /**
     * related items,ordered by sort
     */
    private List<AppDataItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppDataItem implements Serializable {

        private static final long serialVersionUID = 1L;
        /**
         * related object id(bpm_process_app_application id or quick_entry id)
         */
        private String id;
        /**
         * related object name
         */
        private String name;
        /**
         * sort order,start from 1
         */
        private Integer sort;
    }
}
