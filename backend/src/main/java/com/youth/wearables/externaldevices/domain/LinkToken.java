package com.youth.wearables.externaldevices.domain;

/**
 * Result of starting a device-connection flow: the {@code linkToken} for Junction's Link widget and
 * the hosted {@code linkWebUrl} the client can open directly.
 */
public record LinkToken(String linkToken, String linkWebUrl) {}
