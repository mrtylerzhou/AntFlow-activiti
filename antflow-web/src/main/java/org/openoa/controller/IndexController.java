package org.openoa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class IndexController
{
    /**
     * 访问首页，提示语
     */
    @RequestMapping(value = "/", produces = "text/html;charset=UTF-8")
    public String index()
    {
        return String.format("欢迎使用Antflow流程引擎框架，当前版本：v1.2.2，请通过前端地址访问。");
    }

}