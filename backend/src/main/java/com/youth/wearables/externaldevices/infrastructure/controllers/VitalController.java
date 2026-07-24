package com.youth.wearables.externaldevices.infrastructure.controllers;

import com.youth.wearables.config.CurrentUser;
import com.youth.wearables.externaldevices.application.VitalQueryService;
import com.youth.wearables.externaldevices.application.VitalSyncService;
import com.youth.wearables.externaldevices.domain.AccountNotProvisionedException;
import com.youth.wearables.externaldevices.domain.VitalReading;
import com.youth.wearables.externaldevices.infrastructure.controllers.dto.VitalReadingResponseDto;
import com.youth.wearables.externaldevices.infrastructure.controllers.dto.VitalSyncResponseDto;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vitals")
class VitalController {

  private final VitalQueryService vitalQueryService;
  private final VitalSyncService vitalSyncService;

  VitalController(VitalQueryService vitalQueryService, VitalSyncService vitalSyncService) {
    this.vitalQueryService = vitalQueryService;
    this.vitalSyncService = vitalSyncService;
  }

  @GetMapping
  List<VitalReadingResponseDto> latest(@CurrentUser UUID userId) {
    return vitalQueryService.latestReadingsByProvider(userId).entrySet().stream()
        .flatMap(entry -> toDtos(entry.getKey(), entry.getValue()))
        .toList();
  }

  private static Stream<VitalReadingResponseDto> toDtos(
      String provider, List<VitalReading> readings) {
    return readings.stream()
        .map(
            reading ->
                new VitalReadingResponseDto(
                    provider,
                    reading.metric().name(),
                    reading.value(),
                    reading.unit(),
                    reading.measuredAt()));
  }

  @PostMapping("/sync")
  VitalSyncResponseDto sync(
      @CurrentUser UUID userId, @RequestParam(required = false) String provider) {
    return new VitalSyncResponseDto(vitalSyncService.syncUser(userId, provider));
  }

  @ExceptionHandler(AccountNotProvisionedException.class)
  ResponseEntity<Void> handleNotProvisioned() {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }
}
