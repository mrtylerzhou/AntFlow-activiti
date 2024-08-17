package org.openoa.base.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author AntFlow
 * @since 0.0.1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailInfo implements Serializable {


    /**
     * receiver's email
     */
    private String receiver;

    /**
     * receiver email list
     */
    private List<String> receivers;

    /**
     * content
     */
    private String content;

    /**
     * title
     */
    private String title;

    /**
     * carbon copy array
     */
    private String[] cc;

    /**
     * attache file name
     */
    private String fileName;

    /**
     * attach file
     */
    private File file;

    /**
     * attachment stream
     */
    private InputStream fileInputStream;

}
