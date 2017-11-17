package com.javanewb.entity;

import com.javanewb.thread.tools.ThreadHolder;

/**
 * <p>
 * Description: com.javanewb.entity
 * </p>

 * date：2017/11/3
 *
 * @author Dean.Hwang
 */
public class TestThreadHolder extends ThreadHolder<String> {
    public TestThreadHolder() {
        setDefaultData("{\"result\":\"failed\"}");
    }

    @Override
    protected String proData() {
        return "{\"result\":\"success\"}";
    }
}
