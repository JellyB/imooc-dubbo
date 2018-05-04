package com.imooc.curator.utils;

import org.apache.curator.framework.CuratorFramework;

public class ZKCurator {

    private CuratorFramework client;

    public ZKCurator(CuratorFramework client) {
        this.client = client;
    }
}
