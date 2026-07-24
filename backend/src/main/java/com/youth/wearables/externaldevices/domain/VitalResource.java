package com.youth.wearables.externaldevices.domain;

import java.util.Optional;

public enum VitalResource {
  HEART_RATE("heartrate"),
  HEART_RATE_VARIABILITY("hrv"),
  RESPIRATORY_RATE("respiratory_rate"),
  BLOOD_OXYGEN("blood_oxygen"),
  BLOOD_PRESSURE("blood_pressure");

  private final String slug;

  VitalResource(String slug) {
    this.slug = slug;
  }

  public String slug() {
    return slug;
  }

  public static Optional<VitalResource> fromSlug(String slug) {
    for (VitalResource resource : values()) {
      if (resource.slug.equals(slug)) {
        return Optional.of(resource);
      }
    }
    return Optional.empty();
  }
}
