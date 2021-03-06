package com.sequenceiq.cloudbreak.service.credential;

import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.controller.BadRequestException;
import com.sequenceiq.cloudbreak.domain.Credential;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;

@Component
public class OpenSshPublicKeyValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenSshPublicKeyValidator.class);

    public void validate(Credential credential) {
        MDCBuilder.buildMdcContext(credential);
        try {
            PublicKey load = PublicKeyReaderUtil.loadOpenSsh(credential.getPublicKey());
        } catch (Exception e) {
            String errorMessage = String.format("Could not validate publickey certificate [certificate: '%s'], detailed message: %s",
                    credential.getPublicKey(), e.getMessage());
            LOGGER.error(errorMessage, e);
            throw new BadRequestException(errorMessage, e);
        }
    }
}
