package com.walmartlabs.concord.server.process.pipelines.processors;

import com.walmartlabs.concord.server.api.process.ProcessStatus;
import com.walmartlabs.concord.server.metrics.WithTimer;
import com.walmartlabs.concord.server.process.Payload;
import com.walmartlabs.concord.server.process.queue.ProcessQueueDao;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class EnqueueingProcessor implements PayloadProcessor {

    private final ProcessQueueDao queueDao;

    @Inject
    public EnqueueingProcessor(ProcessQueueDao queueDao) {
        this.queueDao = queueDao;
    }

    @Override
    @WithTimer
    public Payload process(Chain chain, Payload payload) {
        String instanceId = payload.getInstanceId();
        queueDao.update(instanceId, ProcessStatus.ENQUEUED);
        return chain.process(payload);
    }
}
