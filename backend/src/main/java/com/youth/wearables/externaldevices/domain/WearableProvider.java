package com.youth.wearables.externaldevices.domain;

public enum WearableProvider {
  WHOOP("whoop", "whoop_v2"),
  OURA("oura", "oura"),
  APPLE_HEALTH_KIT("apple_health_kit", "apple_health_kit");

  private final String slug;
  private final String junctionSlug;

  WearableProvider(String slug, String junctionSlug) {
    this.slug = slug;
    this.junctionSlug = junctionSlug;
  }

  public String slug() {
    return slug;
  }

  public String junctionSlug() {
    return junctionSlug;
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
