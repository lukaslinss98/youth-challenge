package com.youth.wearables.externaldevices.infrastructure.client;

import com.junction.api.Junction;
import com.junction.api.core.ApiError;
import com.junction.api.core.JunctionException;
import com.junction.api.resources.sleep.requests.GetSleepRequest;
import com.junction.api.resources.vitals.requests.BloodOxygenVitalsRequest;
import com.junction.api.resources.vitals.requests.BloodPressureVitalsRequest;
import com.junction.api.resources.vitals.requests.HeartrateVitalsRequest;
import com.junction.api.resources.vitals.requests.HrvVitalsRequest;
import com.junction.api.resources.vitals.requests.RespiratoryRateVitalsRequest;
import com.junction.api.types.ClientFacingSleep;
import com.youth.wearables.externaldevices.application.ports.WearableVitalsApi;
import com.youth.wearables.externaldevices.domain.JunctionApiException;
import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class WearableVitalsApiAdapter implements WearableVitalsApi {

  private static final Set<String> SLEEP_SUMMARY_PROVIDERS = Set.of("whoop", "whoop_v2");

  private final Junction junction;

  WearableVitalsApiAdapter(Junction junction) {
    this.junction = junction;
  }

  @Override
  public List<VitalReading> fetchAll(
      UUID junctionUserId, String providerSlug, LocalDate start, LocalDate end) {

    String userId = junctionUserId.toString();
    String startDate = start.toString();
    String endDate = end.toString();

    try {
      if (SLEEP_SUMMARY_PROVIDERS.contains(providerSlug)) {
        return fromSleepSummary(userId, providerSlug, startDate, endDate);
      }
      return fromTimeseries(userId, providerSlug, startDate, endDate);
    } catch (ApiError e) {
      throw new JunctionApiException(
          "Junction vitals fetch failed for %s (status %d)".formatted(providerSlug, e.statusCode()),
          e);
    } catch (JunctionException e) {
      throw new JunctionApiException("Junction vitals fetch failed for " + providerSlug, e);
    }
  }

  private List<VitalReading> fromSleepSummary(
      String userId, String providerSlug, String startDate, String endDate) {

    List<VitalReading> readings = new ArrayList<>();
    List<ClientFacingSleep> nights =
        junction
            .sleep()
            .get(
                userId,
                GetSleepRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .provider(providerSlug)
                    .build())
            .getSleep();

    for (ClientFacingSleep night : nights) {
      var measuredAt = night.getBedtimeStart();
      night
          .getHrResting()
          .ifPresent(
              v ->
                  readings.add(
                      new VitalReading(VitalMetric.HEART_RATE, measuredAt, v.doubleValue(), "bpm")));
      night
          .getAverageHrv()
          .ifPresent(
              v ->
                  readings.add(
                      new VitalReading(
                          VitalMetric.HEART_RATE_VARIABILITY, measuredAt, v, "ms")));
      night
          .getRespiratoryRate()
          .ifPresent(
              v ->
                  readings.add(
                      new VitalReading(
                          VitalMetric.RESPIRATORY_RATE, measuredAt, v, "breaths/min")));
    }
    return readings;
  }

  private List<VitalReading> fromTimeseries(
      String userId, String providerSlug, String startDate, String endDate) {

    List<VitalReading> readings = new ArrayList<>();

    junction
        .vitals()
        .heartrate(
            userId,
            HeartrateVitalsRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .provider(providerSlug)
                .build())
        .forEach(
            p ->
                readings.add(
                    new VitalReading(
                        VitalMetric.HEART_RATE, p.getTimestamp(), p.getValue(), p.getUnit())));

    junction
        .vitals()
        .hrv(
            userId,
            HrvVitalsRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .provider(providerSlug)
                .build())
        .forEach(
            p ->
                readings.add(
                    new VitalReading(
                        VitalMetric.HEART_RATE_VARIABILITY,
                        p.getTimestamp(),
                        p.getValue(),
                        p.getUnit())));

    junction
        .vitals()
        .respiratoryRate(
            userId,
            RespiratoryRateVitalsRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .provider(providerSlug)
                .build())
        .forEach(
            p ->
                readings.add(
                    new VitalReading(
                        VitalMetric.RESPIRATORY_RATE, p.getTimestamp(), p.getValue(), p.getUnit())));

    junction
        .vitals()
        .bloodOxygen(
            userId,
            BloodOxygenVitalsRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .provider(providerSlug)
                .build())
        .forEach(
            p ->
                readings.add(
                    new VitalReading(
                        VitalMetric.BLOOD_OXYGEN, p.getTimestamp(), p.getValue(), p.getUnit())));

    junction
        .vitals()
        .bloodPressure(
            userId,
            BloodPressureVitalsRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .provider(providerSlug)
                .build())
        .forEach(
            p -> {
              readings.add(
                  new VitalReading(
                      VitalMetric.BLOOD_PRESSURE_SYSTOLIC,
                      p.getTimestamp(),
                      p.getSystolic(),
                      p.getUnit()));
              readings.add(
                  new VitalReading(
                      VitalMetric.BLOOD_PRESSURE_DIASTOLIC,
                      p.getTimestamp(),
                      p.getDiastolic(),
                      p.getUnit()));
            });

    return readings;
  }
}
