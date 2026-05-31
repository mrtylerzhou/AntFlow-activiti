package org.openoa.base.entity.jsonconf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Application category configuration JSON structure
 * Stored in bpm_process_app_application.category_config_json
 *
 * @author AntFlow
 * @since 1.7.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppCategoryConfigJson implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Category associations list
     */
    private List<CategoryItem> categories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * Category ID from bpm_process_category.id
         */
        private Long categoryId;

        /**
         * Sort order within the category
         */
        private Integer sort;

        /**
         * Is frequently used: 0=no, 1=yes
         */
        private Integer state;

        /**
         * Visibility state: 0=hidden, 1=visible
         */
        private Integer visbleState;

        /**
         * History ID (for commonly used tracking)
         */
        private Long historyId;

        /**
         * Common use state
         */
        private Integer commonUseState;
    }
}
