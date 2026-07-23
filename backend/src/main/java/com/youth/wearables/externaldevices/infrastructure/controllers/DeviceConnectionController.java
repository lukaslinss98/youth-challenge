package com.youth.wearables.externaldevices.infrastructure.controllers;

import com.youth.wearables.config.CurrentUser;
import com.youth.wearables.externaldevices.application.DeviceConnectionService;
import com.youth.wearables.externaldevices.domain.AccountNotProvisionedException;
import com.youth.wearables.externaldevices.domain.LinkToken;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
class DeviceConnectionController {

  private final DeviceConnectionService deviceConnectionService;

  DeviceConnectionController(DeviceConnectionService deviceConnectionService) {
    this.deviceConnectionService = deviceConnectionService;
  }

  @PostMapping("/link-token")
  LinkToken createLinkToken(@CurrentUser UUID userId) {
    return deviceConnectionService.createLinkToken(userId);
  }

  @ExceptionHandler(AccountNotProvisionedException.class)
  ResponseEntity<Void> handleNotProvisioned() {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }
}
