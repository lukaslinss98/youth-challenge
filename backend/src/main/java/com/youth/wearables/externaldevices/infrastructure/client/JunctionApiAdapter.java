package com.youth.wearables.externaldevices.infrastructure.client;

import com.junction.api.Junction;
import com.junction.api.core.ApiError;
import com.junction.api.core.JunctionException;
import com.junction.api.resources.link.requests.DemoConnectionCreationPayload;
import com.junction.api.resources.link.requests.LinkTokenExchange;
import com.junction.api.resources.user.requests.UserCreateBody;
import com.junction.api.types.ClientFacingUser;
import com.junction.api.types.DemoProviders;
import com.junction.api.types.LinkTokenExchangeResponse;
import com.junction.api.types.Providers;
import com.youth.wearables.externaldevices.application.ports.JunctionApi;
import com.youth.wearables.externaldevices.domain.DemoConnectionException;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import com.youth.wearables.externaldevices.domain.LinkToken;
import com.youth.wearables.externaldevices.domain.WearableProvider;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class JunctionApiAdapter implements JunctionApi {

  private final Junction junction;

  JunctionApiAdapter(Junction junction) {
    this.junction = junction;
  }

  @Override
  public UUID createUser(UUID clientUserId) {
    try {
      ClientFacingUser created =
          junction
              .user()
              .create(UserCreateBody.builder().clientUserId(clientUserId.toString()).build());
      return UUID.fromString(created.getUserId());
    } catch (ApiError e) {
      throw new JunctionApiException(
          "Junction createUser failed (status %d)".formatted(e.statusCode()), e);
    } catch (JunctionException e) {
      throw new JunctionApiException("Junction createUser failed", e);
    }
  }

  @Override
  public LinkToken createLinkToken(UUID junctionUserId, WearableProvider provider) {
    try {
      LinkTokenExchange linkTokenExchange =
          LinkTokenExchange.builder()
              .userId(junctionUserId.toString())
              .provider(toJunctionProvider(provider))
              .build();

      LinkTokenExchangeResponse res = junction.link().token(linkTokenExchange);

      return new LinkToken(res.getLinkToken(), res.getLinkWebUrl());
    } catch (ApiError e) {
      throw new JunctionApiException(
          "Junction createLinkToken failed (status %d)".formatted(e.statusCode()), e);
    } catch (JunctionException e) {
      throw new JunctionApiException("Junction createLinkToken failed", e);
    }
  }

  @Override
  public void connectDemo(UUID junctionUserId, WearableProvider provider) {
    try {
      junction
          .link()
          .connectDemoProvider(
              DemoConnectionCreationPayload.builder()
                  .userId(junctionUserId.toString())
                  .provider(toDemoProvider(provider))
                  .build());
    } catch (ApiError e) {
      throw new DemoConnectionException(String.valueOf(e.body()), e);
    } catch (JunctionException e) {
      throw new DemoConnectionException("Junction demo connect failed", e);
    }
  }

  @Override
  public void deregisterProvider(UUID junctionUserId, String providerSlug) {
    try {
      junction.user().deregisterProvider(junctionUserId.toString(), Providers.valueOf(providerSlug));
    } catch (ApiError e) {
      throw new JunctionApiException(
          "Junction deregisterProvider failed (status %d)".formatted(e.statusCode()), e);
    } catch (JunctionException e) {
      throw new JunctionApiException("Junction deregisterProvider failed", e);
    }
  }

  private static Providers toJunctionProvider(WearableProvider provider) {
    return switch (provider) {
      case WHOOP -> Providers.WHOOP_V_2;
      case OURA -> Providers.OURA;
      case APPLE_HEALTH_KIT -> Providers.APPLE_HEALTH_KIT;
    };
  }

  private static DemoProviders toDemoProvider(WearableProvider provider) {
    return switch (provider) {
      case OURA -> DemoProviders.OURA;
      case APPLE_HEALTH_KIT -> DemoProviders.APPLE_HEALTH_KIT;
      case WHOOP ->
          throw new IllegalArgumentException("Provider does not support demo connections: whoop");
    };
  }
}
