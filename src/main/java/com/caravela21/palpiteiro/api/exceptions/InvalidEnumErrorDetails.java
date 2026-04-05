package com.caravela21.palpiteiro.api.exceptions;

import java.util.List;

public record InvalidEnumErrorDetails(
        String field,
        String rejectedValue,
        List<String> acceptedValues,
        String message
) {
}

