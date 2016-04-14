package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.ResponseSetting;
import com.github.dreamhead.moco.Runner;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public abstract class BaseServerRunner<T extends ResponseSetting<T>> extends Runner {
    protected abstract BaseActualServer<T> serverSetting();
    protected abstract ChannelInitializer<? extends Channel> channelInitializer();

    private final MocoServer server = new MocoServer();

    @Override
    public void start() {
        BaseActualServer<T> setting = serverSetting();
        int port = this.server.start(setting.getPort().or(0), channelInitializer());
        setting.setPort(port);
    }

    @Override
    public void stop() {
        server.stop();
    }
}
