package com.javanewb.entity;

import com.javanewb.thread.tools.ThreadHolder;

/**
 * <p>
 * Description: com.javanewb.entity
 * </p>
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * <p>
 * Company: 客如云
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
