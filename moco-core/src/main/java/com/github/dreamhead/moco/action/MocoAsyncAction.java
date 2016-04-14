package com.github.dreamhead.moco.action;

import com.github.dreamhead.moco.MocoConfig;
import com.github.dreamhead.moco.MocoEventAction;
import com.github.dreamhead.moco.procedure.LatencyProcedure;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MocoAsyncAction implements MocoEventAction {
    private final MocoEventAction action;
    private final LatencyProcedure procedure;
    private final ExecutorService service = Executors.newCachedThreadPool();

    public MocoAsyncAction(final MocoEventAction action, final LatencyProcedure procedure) {
        this.action = action;
        this.procedure = procedure;
    }

    @Override
    public void execute() {
        service.execute(new Runnable() {
            @Override
            public void run() {
                procedure.execute();
                action.execute();
            }
        });
    }

    @Override
    public MocoEventAction apply(final MocoConfig config) {
        MocoEventAction appliedAction = this.action.apply(config);
        if (this.action != appliedAction) {
            return new MocoAsyncAction(appliedAction, procedure);
        }

        return this;
    }
}
