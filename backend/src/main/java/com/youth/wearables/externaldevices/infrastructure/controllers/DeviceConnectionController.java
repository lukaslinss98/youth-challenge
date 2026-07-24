package com.youth.wearables.externaldevices.infrastructure.controllers;

import com.youth.wearables.config.CurrentUser;
import com.youth.wearables.externaldevices.application.DeviceConnectionService;
import com.youth.wearables.externaldevices.domain.AccountNotProvisionedException;
import com.youth.wearables.externaldevices.domain.DemoConnectionException;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import com.youth.wearables.externaldevices.domain.LinkToken;
import com.youth.wearables.externaldevices.domain.WearableProvider;
import com.youth.wearables.externaldevices.infrastructure.controllers.dto.DeviceConnectionResponseDto;
import com.youth.wearables.externaldevices.infrastructure.controllers.dto.LinkTokenResponseDto;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
class DeviceConnectionController {

  private final DeviceConnectionService deviceConnectionService;

  DeviceConnectionController(DeviceConnectionService deviceConnectionService) {
    this.deviceConnectionService = deviceConnectionService;
  }

  @GetMapping
  List<DeviceConnectionResponseDto> listDevices(@CurrentUser UUID userId) {
    return deviceConnectionService.listConnections(userId).stream()
        .map(
            connection ->
                new DeviceConnectionResponseDto(
                    connection.providerSlug(),
                    connection.status().name(),
                    connection.updatedAt()))
        .toList();
  }

  @PostMapping("/link-token")
  LinkTokenResponseDto createLinkToken(@CurrentUser UUID userId, @RequestParam String provider) {
    LinkToken linkToken =
        deviceConnectionService.createLinkToken(userId, WearableProvider.fromSlug(provider));
    return new LinkTokenResponseDto(linkToken.linkToken(), linkToken.linkWebUrl());
  }

  @PostMapping("/demo-connect")
  ResponseEntity<Void> demoConnect(@CurrentUser UUID userId, @RequestParam String provider) {
    deviceConnectionService.connectDemo(userId, WearableProvider.fromSlug(provider));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{provider}")
  ResponseEntity<Void> disconnect(@CurrentUser UUID userId, @PathVariable String provider) {
    deviceConnectionService.disconnect(userId, provider);
    return ResponseEntity.noContent().build();
  }

  @ExceptionHandler(AccountNotProvisionedException.class)
  ResponseEntity<Void> handleNotProvisioned() {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler(DemoConnectionException.class)
  ResponseEntity<Void> handleDemoConnectionFailed() {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Void> handleUnknownProvider() {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }

  @ExceptionHandler(JunctionApiException.class)
  ResponseEntity<Void> handleJunctionApiFailure() {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
  }
}
