package com.youth.wearables.externaldevices.domain;

public enum WearableProvider {
  WHOOP("whoop");

  private final String slug;

  WearableProvider(String slug) {
    this.slug = slug;
  }

  public String slug() {
    return slug;
  }

  public static WearableProvider fromSlug(String slug) {
    for (WearableProvider provider : values()) {
      if (provider.slug.equalsIgnoreCase(slug)) {
        return provider;
      }
    }
    throw new IllegalArgumentException("Unknown wearable provider: %s".formatted(slug));
  }
}
