package com.evolveum.midpoint.rest.impl;

import static org.springframework.http.ResponseEntity.status;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.evolveum.midpoint.audit.api.AuditEventRecord;
import com.evolveum.midpoint.audit.api.AuditEventStage;
import com.evolveum.midpoint.audit.api.AuditEventType;
import com.evolveum.midpoint.audit.api.AuditService;
import com.evolveum.midpoint.model.impl.security.SecurityHelper;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.schema.constants.SchemaConstants;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.schema.result.OperationResultStatus;
import com.evolveum.midpoint.security.api.MidPointPrincipal;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.task.api.TaskManager;
import com.evolveum.midpoint.util.exception.*;
import com.evolveum.midpoint.util.logging.LoggingUtils;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_3.FocusType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.OperationResultType;

/**
 * Base REST controller class providing common (utility) methods and logger.
 */
class AbstractRestController {

    protected final Trace logger = TraceManager.getTrace(getClass());

    private final String opNamePrefix = getClass().getName() + ".";

    @Autowired protected AuditService auditService;
    @Autowired protected SecurityHelper securityHelper;
    @Autowired protected TaskManager taskManager;

    protected Task initRequest() {
        // No need to audit login. it was already audited during authentication
        Task task = taskManager.createTaskInstance(opNamePrefix + "restService");
        task.setChannel(SchemaConstants.CHANNEL_REST_URI);
        return task;
    }

    protected OperationResult createSubresult(Task task, String operation) {
        return task.getResult().createSubresult(opNamePrefix + operation);
    }

    protected ResponseEntity<?> createResponse(HttpStatus statusCode, OperationResult result) {
        return createResponse(statusCode, null, result, false);
    }

    protected <T> ResponseEntity<?> createResponse(
            HttpStatus httpStatus, T body, OperationResult result) {
        return createResponse(httpStatus, body, result, false);
    }

    protected <T> ResponseEntity<?> createResponse(HttpStatus httpStatus,
            T body, OperationResult result, boolean sendOriginObjectIfNotSuccess) {
        result.computeStatusIfUnknown();

        if (result.isPartialError()) {
            return createBody(status(250), sendOriginObjectIfNotSuccess, body, result);
        } else if (result.isHandledError()) {
            return createBody(status(240), sendOriginObjectIfNotSuccess, body, result);
        }

        return status(httpStatus).body(body);
    }

    protected ResponseEntity<?> createResponseWithLocation(
            HttpStatus httpStatus, URI location, OperationResult result) {
        result.computeStatusIfUnknown();

        if (result.isPartialError()) {
            return ResponseEntity.status(250).location(location).body(result);
        } else if (result.isHandledError()) {
            return ResponseEntity.status(240).location(location).body(result);
        }

        return location == null ? ResponseEntity.status(httpStatus).build()
                : ResponseEntity.status(httpStatus).location(location).build();
    }

    protected <T> ResponseEntity<?> createBody(ResponseEntity.BodyBuilder builder,
            boolean sendOriginObjectIfNotSuccess, T body, OperationResult result) {
        if (sendOriginObjectIfNotSuccess) {
            return builder.body(body);
        }
        return builder.body(result);
    }

    protected ResponseEntity<?> handleException(OperationResult result, Throwable t) {
        LoggingUtils.logUnexpectedException(logger, "Got exception while servicing REST request: {}", t,
                result != null ? result.getOperation() : "(null)");
        return handleExceptionNoLog(result, t);
    }

    protected ResponseEntity<?> handleExceptionNoLog(OperationResult result, Throwable t) {
        return createErrorResponseBuilder(result, t);
    }

    protected ResponseEntity<?> createErrorResponseBuilder(OperationResult result, Throwable t) {
        if (t instanceof ObjectNotFoundException) {
            return createErrorResponseBuilder(HttpStatus.NOT_FOUND, result);
        }

        if (t instanceof CommunicationException || t instanceof TunnelException) {
            return createErrorResponseBuilder(HttpStatus.GATEWAY_TIMEOUT, result);
        }

        if (t instanceof SecurityViolationException) {
            return createErrorResponseBuilder(HttpStatus.FORBIDDEN, result);
        }

        if (t instanceof ConfigurationException) {
            return createErrorResponseBuilder(HttpStatus.BAD_GATEWAY, result);
        }

        if (t instanceof SchemaException || t instanceof ExpressionEvaluationException) {
            return createErrorResponseBuilder(HttpStatus.BAD_REQUEST, result);
        }

        if (t instanceof PolicyViolationException
                || t instanceof ObjectAlreadyExistsException
                || t instanceof ConcurrencyException) {
            return createErrorResponseBuilder(HttpStatus.CONFLICT, result);
        }

        return createErrorResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR, result);
    }

    protected ResponseEntity<?> createErrorResponseBuilder(
            HttpStatus status, OperationResult result) {
        OperationResultType resultBean;
        if (result != null) {
            result.computeStatusIfUnknown();
            resultBean = result.createOperationResultType();
        } else {
            resultBean = null;
        }
        return status(status).body(resultBean);
    }

    protected void finishRequest() {
        auditEvent();
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private void auditEvent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        String name = null;
        if (principal instanceof MidPointPrincipal) {
            name = ((MidPointPrincipal) principal).getUsername();
        } else if (principal != null) {
            return;
        }
        PrismObject<? extends FocusType> user = principal != null ? ((MidPointPrincipal) principal).getFocus().asPrismObject() : null;

        Task task = taskManager.createTaskInstance();
        task.setOwner(user);
        task.setChannel(SchemaConstants.CHANNEL_REST_URI);

        AuditEventRecord record = new AuditEventRecord(AuditEventType.TERMINATE_SESSION, AuditEventStage.REQUEST);
        record.setInitiator(user);
        record.setParameter(name);

        record.setChannel(SchemaConstants.CHANNEL_REST_URI);
        record.setTimestamp(System.currentTimeMillis());
        record.setOutcome(OperationResultStatus.SUCCESS);

        auditService.audit(record, task);
    }
}
