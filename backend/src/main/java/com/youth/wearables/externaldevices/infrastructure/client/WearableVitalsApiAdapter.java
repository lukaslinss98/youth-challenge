package com.youth.wearables.externaldevices.infrastructure.client;

import com.junction.api.Junction;
import com.junction.api.resources.sleep.requests.GetSleepRequest;
import com.junction.api.resources.vitals.requests.BloodOxygenVitalsRequest;
import com.junction.api.resources.vitals.requests.BloodPressureVitalsRequest;
import com.junction.api.resources.vitals.requests.HeartrateVitalsRequest;
import com.junction.api.resources.vitals.requests.HrvVitalsRequest;
import com.junction.api.resources.vitals.requests.RespiratoryRateVitalsRequest;
import com.junction.api.types.ClientFacingSleep;
import com.youth.wearables.externaldevices.application.ports.WearableVitalsApi;
import com.youth.wearables.externaldevices.domain.VitalMetric;
import com.youth.wearables.externaldevices.domain.VitalReading;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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

    return JunctionCalls.execute(
        () ->
            SLEEP_SUMMARY_PROVIDERS.contains(providerSlug)
                ? fromSleepSummary(userId, providerSlug, startDate, endDate)
                : fromTimeseries(userId, providerSlug, startDate, endDate),
        "Junction vitals fetch failed for " + providerSlug);
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

    addReadings(
        readings,
        junction
            .vitals()
            .heartrate(
                userId,
                HeartrateVitalsRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .provider(providerSlug)
                    .build()),
        p -> List.of(new VitalReading(VitalMetric.HEART_RATE, p.getTimestamp(), p.getValue(), p.getUnit())));

    addReadings(
        readings,
        junction
            .vitals()
            .hrv(
                userId,
                HrvVitalsRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .provider(providerSlug)
                    .build()),
        p ->
            List.of(
                new VitalReading(
                    VitalMetric.HEART_RATE_VARIABILITY, p.getTimestamp(), p.getValue(), p.getUnit())));

    addReadings(
        readings,
        junction
            .vitals()
            .respiratoryRate(
                userId,
                RespiratoryRateVitalsRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .provider(providerSlug)
                    .build()),
        p ->
            List.of(
                new VitalReading(
                    VitalMetric.RESPIRATORY_RATE, p.getTimestamp(), p.getValue(), p.getUnit())));

    addReadings(
        readings,
        junction
            .vitals()
            .bloodOxygen(
                userId,
                BloodOxygenVitalsRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .provider(providerSlug)
                    .build()),
        p ->
            List.of(
                new VitalReading(VitalMetric.BLOOD_OXYGEN, p.getTimestamp(), p.getValue(), p.getUnit())));

    addReadings(
        readings,
        junction
            .vitals()
            .bloodPressure(
                userId,
                BloodPressureVitalsRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .provider(providerSlug)
                    .build()),
        p ->
            List.of(
                new VitalReading(
                    VitalMetric.BLOOD_PRESSURE_SYSTOLIC, p.getTimestamp(), p.getSystolic(), p.getUnit()),
                new VitalReading(
                    VitalMetric.BLOOD_PRESSURE_DIASTOLIC, p.getTimestamp(), p.getDiastolic(), p.getUnit())));

    return readings;
  }

  private static <T> void addReadings(
      List<VitalReading> target, Iterable<T> points, Function<T, List<VitalReading>> toReadings) {
    for (T point : points) {
      target.addAll(toReadings.apply(point));
    }
  }
}
