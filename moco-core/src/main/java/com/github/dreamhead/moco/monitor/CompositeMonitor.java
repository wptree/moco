package com.github.dreamhead.moco.monitor;

import com.github.dreamhead.moco.MocoMonitor;
import com.github.dreamhead.moco.Request;
import com.github.dreamhead.moco.Response;

public class CompositeMonitor implements MocoMonitor {
    private final MocoMonitor[] monitors;

    public CompositeMonitor(final MocoMonitor[] monitors) {
        this.monitors = monitors;
    }

    @Override
    public void onMessageArrived(final Request request) {
        for (MocoMonitor monitor : monitors) {
            monitor.onMessageArrived(request);
        }
    }

    @Override
    public void onException(final Throwable t) {
        for (MocoMonitor monitor : monitors) {
            monitor.onException(t);
        }
    }

    @Override
    public void onMessageLeave(final Response response) {
        for (MocoMonitor monitor : monitors) {
            monitor.onMessageLeave(response);
        }
    }

    @Override
    public void onUnexpectedMessage(final Request request) {
        for (MocoMonitor monitor : monitors) {
            monitor.onUnexpectedMessage(request);
        }
    }
}
