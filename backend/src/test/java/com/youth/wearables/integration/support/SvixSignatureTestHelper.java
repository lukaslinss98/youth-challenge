package com.youth.wearables.integration.support;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public final class SvixSignatureTestHelper {

  public static final String TEST_WEBHOOK_SECRET = "whsec_MfKQ9r8GKYqrTwjUPD8ILPZIo2LaLaSw";

  private static final String WHSEC_PREFIX = "whsec_";

  private SvixSignatureTestHelper() {}

  public static String sign(String svixId, String svixTimestamp, byte[] payload) {
    return sign(TEST_WEBHOOK_SECRET, svixId, svixTimestamp, payload);
  }

  public static String sign(String webhookSecret, String svixId, String svixTimestamp, byte[] payload) {
    String keyPart =
        webhookSecret.startsWith(WHSEC_PREFIX)
            ? webhookSecret.substring(WHSEC_PREFIX.length())
            : webhookSecret;
    byte[] key = Base64.getDecoder().decode(keyPart);

    byte[] prefix = (svixId + "." + svixTimestamp + ".").getBytes(StandardCharsets.UTF_8);
    byte[] signedContent = new byte[prefix.length + payload.length];
    System.arraycopy(prefix, 0, signedContent, 0, prefix.length);
    System.arraycopy(payload, 0, signedContent, prefix.length, payload.length);

    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(key, "HmacSHA256"));
      String encoded = Base64.getEncoder().encodeToString(mac.doFinal(signedContent));
      return "v1," + encoded;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to compute test webhook signature", e);
    }
  }

  public static String currentTimestamp() {
    return String.valueOf(Instant.now().getEpochSecond());
  }
}
