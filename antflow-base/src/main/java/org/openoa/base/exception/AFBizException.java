package org.openoa.base.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Classname AFBizException
 * @Description TODO
 * @Date 2021-10-31 11:06
 * @Created by AntOffice
 */
@Getter
public class AFBizException extends RuntimeException {
    private final String code;
    private final String message;
    private final Object errT;
    @Setter
    private Boolean isLog = true;

    private Integer type;

    private Integer errLevel;

    private String subMessage;

    public AFBizException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
        this.code = "1";
        this.errT = null;
    }

    public AFBizException(String code, String message, Object errT) {
        super(message);
        this.code = code;
        this.message = message;
        this.errT = errT;
    }

    public AFBizException(String code, String message) {
        this(code, message, null);
    }

    public AFBizException(String message) {
        this("1", message);
    }

    public AFBizException(Integer type, String code, String message, String subMessage) {
        this(code, message);
        this.type = type;
        this.subMessage = subMessage;
    }

    public AFBizException(Integer type, String message, String subMessage) {
        this("1", message);
        this.type = type;
        this.subMessage = subMessage;
    }

    public AFBizException(Integer type, Integer errLevel, String message, String subMessage) {
        this("1", message);
        this.type = type;
        this.errLevel = errLevel;
        this.subMessage = subMessage;
    }

    public AFBizException(String code, Integer type, Integer errLevel, String message, String subMessage) {
        this(code, message);
        this.type = type;
        this.errLevel = errLevel;
        this.subMessage = subMessage;
    }
}
