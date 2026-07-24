package com.youth.wearables.externaldevices.infrastructure.controllers.dto;

import java.time.LocalDateTime;

public record DeviceConnectionResponseDto(
    String provider, String status, LocalDateTime updatedAt) {}
