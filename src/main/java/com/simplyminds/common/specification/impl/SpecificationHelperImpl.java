package com.simplyminds.common.specification.impl;

import com.simplyminds.common.dto.SpecificationResponseDto;
import com.simplyminds.common.specification.SpecificationHelper;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
public class SpecificationHelperImpl implements SpecificationHelper {

    public SpecificationHelperImpl() {
    }

    @Override
    public <T, S, R> SpecificationResponseDto getHelp(Class<T> sourceEntity, Class<S> targetEntity, Root<R> root, String field) {
        SpecificationResponseDto specificationResponseDto = new SpecificationResponseDto();

        try {
            // Validate if field exists in source entity
            Field declaredField = sourceEntity.getDeclaredField(field);

            if (declaredField == null) {
                throw new NoSuchFieldException("Field '" + field + "' does not exist in " + sourceEntity.getSimpleName());
            }

            // Perform the join
            Join<T, S> join = root.join(field, JoinType.INNER);

            // Set successful response
            specificationResponseDto.setSuccess(true);
            specificationResponseDto.setIsJoinPresent(true);
            specificationResponseDto.setJoin(join);

        } catch (NoSuchFieldException e) {
            specificationResponseDto.setSuccess(false);
            specificationResponseDto.setErrorMessage("Invalid field: " + e.getMessage());
            specificationResponseDto.setErrorCode("FIELD_NOT_FOUND");
        } catch (IllegalArgumentException e) {
            specificationResponseDto.setSuccess(false);
            specificationResponseDto.setErrorMessage("Join operation failed: " + e.getMessage());
            specificationResponseDto.setErrorCode("JOIN_ERROR");
        } catch (Exception e) {
            specificationResponseDto.setSuccess(false);
            specificationResponseDto.setErrorMessage("Unexpected error: " + e.getMessage());
            specificationResponseDto.setErrorCode("UNKNOWN_ERROR");
        }

        return specificationResponseDto;
    }
}