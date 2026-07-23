package com.youth.wearables.externaldevices.infrastructure.client;

import com.junction.api.Junction;
import com.junction.api.core.ApiError;
import com.junction.api.core.JunctionException;
import com.junction.api.resources.link.requests.LinkTokenExchange;
import com.junction.api.resources.user.requests.UserCreateBody;
import com.junction.api.types.ClientFacingUser;
import com.junction.api.types.LinkTokenExchangeResponse;
import com.youth.wearables.externaldevices.application.ports.JunctionApi;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import com.youth.wearables.externaldevices.domain.LinkToken;
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
  public LinkToken createLinkToken(UUID junctionUserId) {
    try {
      LinkTokenExchange linkTokenExchange =
          LinkTokenExchange.builder().userId(junctionUserId.toString()).build();

      LinkTokenExchangeResponse res = junction.link().token(linkTokenExchange);

      return new LinkToken(res.getLinkToken(), res.getLinkWebUrl());
    } catch (ApiError e) {
      throw new JunctionApiException(
          "Junction createLinkToken failed (status %d)".formatted(e.statusCode()), e);
    } catch (JunctionException e) {
      throw new JunctionApiException("Junction createLinkToken failed", e);
    }
  }
}
