package com.youth.wearables.externaldevices.infrastructure.client;

import com.junction.api.core.ApiError;
import com.junction.api.core.JunctionException;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import java.util.function.Supplier;

final class JunctionCalls {

  private JunctionCalls() {}

  static <T> T execute(Supplier<T> operation, String errorMessage) {
    try {
      return operation.get();
    } catch (ApiError e) {
      throw new JunctionApiException("%s (status %d)".formatted(errorMessage, e.statusCode()), e);
    } catch (JunctionException e) {
      throw new JunctionApiException(errorMessage, e);
    }
  }
}
