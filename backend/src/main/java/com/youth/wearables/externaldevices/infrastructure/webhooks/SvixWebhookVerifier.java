package com.youth.wearables.externaldevices.infrastructure.webhooks;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
class SvixWebhookVerifier {

  private static final String WHSEC_PREFIX = "whsec_";
  private static final long TOLERANCE_SECONDS = 300;

  private final byte[] secret;

  SvixWebhookVerifier(@Value("${junction.webhook.secret}") String secret) {
    String key = secret.startsWith(WHSEC_PREFIX) ? secret.substring(WHSEC_PREFIX.length()) : secret;
    this.secret = Base64.getDecoder().decode(key);
  }

  boolean verify(byte[] payload, String svixId, String svixTimestamp, String svixSignature) {
    if (svixId == null || svixTimestamp == null || svixSignature == null) {
      return false;
    }
    if (!timestampWithinTolerance(svixTimestamp)) {
      return false;
    }

    String expected = sign(svixId, svixTimestamp, payload);
    byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);

    for (String part : svixSignature.split(" ")) {
      String[] versionAndSignature = part.split(",", 2);
      if (versionAndSignature.length == 2 && "v1".equals(versionAndSignature[0])) {
        byte[] provided = versionAndSignature[1].getBytes(StandardCharsets.UTF_8);
        if (MessageDigest.isEqual(expectedBytes, provided)) {
          return true;
        }
      }
    }
    return false;
  }

  private String sign(String svixId, String svixTimestamp, byte[] payload) {
    byte[] prefix = (svixId + "." + svixTimestamp + ".").getBytes(StandardCharsets.UTF_8);
    byte[] signedContent = new byte[prefix.length + payload.length];
    System.arraycopy(prefix, 0, signedContent, 0, prefix.length);
    System.arraycopy(payload, 0, signedContent, prefix.length, payload.length);

    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret, "HmacSHA256"));
      return Base64.getEncoder().encodeToString(mac.doFinal(signedContent));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to compute webhook signature", e);
    }
  }

  private boolean timestampWithinTolerance(String svixTimestamp) {
    try {
      long timestamp = Long.parseLong(svixTimestamp);
      long now = Instant.now().getEpochSecond();
      return Math.abs(now - timestamp) <= TOLERANCE_SECONDS;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
