package com.youth.wearables.externaldevices.domain;

import java.time.LocalDateTime;

public record DeviceConnection(
    String providerSlug, ConnectionStatus status, LocalDateTime updatedAt) {}
