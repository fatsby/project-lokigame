package com.theliems.lokigame.infrastructure.exception;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.theliems.lokigame.infrastructure.exception.exceptions.AppException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ExceptionFactory {
     public AppException createNotFoundException(String entityType, String entityAttribute, Object entityValue,
            ErrorCodeInterface errorCodeInterface) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("entityType", entityType);
        context.put(entityAttribute, entityValue);
        return new AppException(errorCodeInterface, context);
    }

    public AppException createNotFoundException(String entityType, Object entityValue,
            ErrorCodeInterface errorCodeInterface) {
        return createNotFoundException(entityType, "entityId", entityValue, errorCodeInterface);
    }

    public AppException createAlreadyExistsException(String entityType, String field, Object value,
            ErrorCodeInterface errorCodeInterface) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("entityType", entityType);
        context.put("conflictField", field);
        context.put("conflictValue", value);

        return new AppException(errorCodeInterface, context);
    }

    public AppException createValidationException(String entityType, String field, Object value,
            ErrorCodeInterface errorCodeInterface) {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("entityType", entityType);
        context.put("invalidField", field);
        context.put("invalidValue", value);

        return new AppException(errorCodeInterface, context);
    }

    public AppException createCustomException(String entityType, List<String> errorNames, List<Object> errorValues,
            ErrorCodeInterface errorCodeInterface) {
        Map<String, Object> context = new LinkedHashMap<>();
        if (entityType != null) {
            context.put("entityType", entityType);
        }
        for (int i = 0; i < errorNames.size() && i < errorValues.size(); i++) {
            context.put(errorNames.get(i), errorValues.get(i));
        }

        return new AppException(errorCodeInterface, context);
    }

    public AppException createCustomException(List<String> errorNames, List<Object> errorValues,
            ErrorCodeInterface errorCodeInterface) {
        return createCustomException(null, errorNames, errorValues, errorCodeInterface);
    }

    public AppException createCustomException(ErrorCodeInterface errorCodeInterface) {
        return new AppException(errorCodeInterface);
    }
}
