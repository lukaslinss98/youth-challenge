package com.youth.wearables.externaldevices.domain;

/**
 * Raised when a call to the external wearable provider fails. Wraps provider-SDK errors so the
 * application layer never sees SDK types.
 */
public class JunctionApiException extends RuntimeException {

  public JunctionApiException(String message, Throwable cause) {
    super(message, cause);
  }
}
