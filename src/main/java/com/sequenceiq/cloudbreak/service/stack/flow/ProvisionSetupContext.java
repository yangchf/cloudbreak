package com.sequenceiq.cloudbreak.service.stack.flow;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.conf.ReactorConfig;
import com.sequenceiq.cloudbreak.domain.CloudPlatform;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;
import com.sequenceiq.cloudbreak.repository.StackRepository;
import com.sequenceiq.cloudbreak.service.stack.connector.ProvisionSetup;
import com.sequenceiq.cloudbreak.service.stack.event.StackOperationFailure;

import reactor.core.Reactor;
import reactor.event.Event;

@Service
public class ProvisionSetupContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionSetupContext.class);

    @Autowired
    private StackRepository stackRepository;

    @Resource
    private Map<CloudPlatform, ProvisionSetup> provisionSetups;

    @Autowired
    private Reactor reactor;

    public void setupProvisioning(CloudPlatform cloudPlatform, Long stackId) {
        Stack stack = stackRepository.findById(stackId);
        MDCBuilder.buildMdcContext(stack);
        try {
            ProvisionSetup provisionSetup = provisionSetups.get(cloudPlatform);
            provisionSetup.setupProvisioning(stackRepository.findById(stackId));
        } catch (Exception e) {
            LOGGER.error("Unhandled exception occurred while setting up provisioning.", e);
            LOGGER.info("Publishing {} event", ReactorConfig.STACK_CREATE_FAILED_EVENT);
            reactor.notify(ReactorConfig.STACK_CREATE_FAILED_EVENT, Event.wrap(new StackOperationFailure(stackId,
                    "Internal server error occurred while setting up provisioning.")));
        }
    }

}
