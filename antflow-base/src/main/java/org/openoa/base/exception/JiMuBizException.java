package org.openoa.base.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Classname JiMuBizException
 * @Description TODO
 * @Date 2021-10-31 11:06
 * @Created by AntOffice
 */
public class JiMuBizException extends RuntimeException {
    @Getter
    private final String code;
    @Getter
    private final String message;
    @Getter
    private final Object errT;
    @Setter
    @Getter
    private Boolean isLog = true;

    @Getter
    private Integer type;

    @Getter
    private Integer errLevel;

    @Getter
    private String subMessage;

    public JiMuBizException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = "1";
        this.errT = null;
    }

    public JiMuBizException(String code, String message, Object errT) {
        super(message);
        this.code = code;
        this.message = message;
        this.errT = errT;
    }

    public JiMuBizException(String code, String message) {
        this(code, message, null);
    }

    public JiMuBizException(String message) {
        this("1", message);
    }

    public JiMuBizException(Integer type, String code, String message, String subMessage) {
        this(code, message);
        this.type = type;
        this.subMessage = subMessage;
    }

    public JiMuBizException(Integer type, String message, String subMessage) {
        this("1", message);
        this.type = type;
        this.subMessage = subMessage;
    }

    public JiMuBizException(Integer type, Integer errLevel, String message, String subMessage) {
        this("1", message);
        this.type = type;
        this.errLevel = errLevel;
        this.subMessage = subMessage;
    }

    public JiMuBizException(String code, Integer type, Integer errLevel, String message, String subMessage) {
        this(code, message);
        this.type = type;
        this.errLevel = errLevel;
        this.subMessage = subMessage;
    }
}
