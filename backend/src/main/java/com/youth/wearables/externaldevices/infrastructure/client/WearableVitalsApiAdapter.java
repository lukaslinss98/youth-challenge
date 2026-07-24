package com.youth.wearables.externaldevices.infrastructure.client;

import com.junction.api.Junction;
import com.junction.api.core.ApiError;
import com.junction.api.core.JunctionException;
import com.junction.api.resources.vitals.requests.BloodOxygenVitalsRequest;
import com.junction.api.resources.vitals.requests.BloodPressureVitalsRequest;
import com.junction.api.resources.vitals.requests.HeartrateVitalsRequest;
import com.junction.api.resources.vitals.requests.HrvVitalsRequest;
import com.junction.api.resources.vitals.requests.RespiratoryRateVitalsRequest;
import com.youth.wearables.externaldevices.application.ports.WearableVitalsApi;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import com.youth.wearables.externaldevices.domain.VitalResource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class WearableVitalsApiAdapter implements WearableVitalsApi {

  private final Junction junction;

  WearableVitalsApiAdapter(Junction junction) {
    this.junction = junction;
  }

  @Override
  public List<VitalReading> fetch(
      UUID junctionUserId,
      String providerSlug,
      VitalResource resource,
      LocalDate start,
      LocalDate end) {

    String userId = junctionUserId.toString();
    String startDate = start.toString();
    String endDate = end.toString();

    try {
      return switch (resource) {
        case HEART_RATE ->
            junction
                .vitals()
                .heartrate(
                    userId,
                    HeartrateVitalsRequest.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .provider(providerSlug)
                        .build())
                .stream()
                .map(
                    point ->
                        new VitalReading(
                            VitalMetric.HEART_RATE,
                            point.getTimestamp(),
                            point.getValue(),
                            point.getUnit()))
                .toList();
        case HEART_RATE_VARIABILITY ->
            junction
                .vitals()
                .hrv(
                    userId,
                    HrvVitalsRequest.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .provider(providerSlug)
                        .build())
                .stream()
                .map(
                    point ->
                        new VitalReading(
                            VitalMetric.HEART_RATE_VARIABILITY,
                            point.getTimestamp(),
                            point.getValue(),
                            point.getUnit()))
                .toList();
        case RESPIRATORY_RATE ->
            junction
                .vitals()
                .respiratoryRate(
                    userId,
                    RespiratoryRateVitalsRequest.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .provider(providerSlug)
                        .build())
                .stream()
                .map(
                    point ->
                        new VitalReading(
                            VitalMetric.RESPIRATORY_RATE,
                            point.getTimestamp(),
                            point.getValue(),
                            point.getUnit()))
                .toList();
        case BLOOD_OXYGEN ->
            junction
                .vitals()
                .bloodOxygen(
                    userId,
                    BloodOxygenVitalsRequest.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .provider(providerSlug)
                        .build())
                .stream()
                .map(
                    point ->
                        new VitalReading(
                            VitalMetric.BLOOD_OXYGEN,
                            point.getTimestamp(),
                            point.getValue(),
                            point.getUnit()))
                .toList();
        case BLOOD_PRESSURE -> bloodPressure(userId, startDate, endDate, providerSlug);
      };
    } catch (ApiError e) {
      throw new JunctionApiException(
          "Junction vitals fetch failed for %s (status %d)".formatted(resource.slug(), e.statusCode()),
          e);
    } catch (JunctionException e) {
      throw new JunctionApiException("Junction vitals fetch failed for " + resource.slug(), e);
    }
  }

  private List<VitalReading> bloodPressure(
      String userId, String startDate, String endDate, String providerSlug) {

    List<VitalReading> readings = new ArrayList<>();
    var points =
        junction
            .vitals()
            .bloodPressure(
                userId,
                BloodPressureVitalsRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .provider(providerSlug)
                    .build());

    for (var point : points) {
      readings.add(
          new VitalReading(
              VitalMetric.BLOOD_PRESSURE_SYSTOLIC,
              point.getTimestamp(),
              point.getSystolic(),
              point.getUnit()));
      readings.add(
          new VitalReading(
              VitalMetric.BLOOD_PRESSURE_DIASTOLIC,
              point.getTimestamp(),
              point.getDiastolic(),
              point.getUnit()));
    }
    return readings;
  }
}
