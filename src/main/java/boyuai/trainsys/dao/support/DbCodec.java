package boyuai.trainsys.dao.support;

import boyuai.trainsys.model.TripInfo;
import boyuai.trainsys.model.UserInfo;
import boyuai.trainsys.util.Time;
import boyuai.trainsys.util.TrainScheduler;
import boyuai.trainsys.util.Types;

public final class DbCodec {
    private DbCodec() {
    }

    public static String joinIntArray(int[] values, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(values[i]);
        }
        return builder.toString();
    }

    public static int[] parseIntArray(String value) {
        if (value == null || value.isEmpty()) {
            return new int[0];
        }
        String[] parts = value.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i]);
        }
        return result;
    }

    public static TrainScheduler toTrainScheduler(String trainId, Integer seatNum, String startTime, String stations, String duration, String price) {
        TrainScheduler scheduler = new TrainScheduler();
        scheduler.setTrainID(new Types.TrainID(trainId));
        scheduler.setSeatNum(seatNum == null ? 0 : seatNum);
        if (startTime != null && !startTime.isEmpty()) {
            scheduler.setStartTime(new Time(startTime + " 01-01"));
        }
        scheduler.setDuration(parseIntArray(duration));
        scheduler.setPrice(parseIntArray(price));
        int[] stationIds = parseIntArray(stations);
        for (int stationId : stationIds) {
            scheduler.addStation(new Types.StationID(stationId));
        }
        return scheduler;
    }

    public static UserInfo toUserInfo(Long userId, String username, String password, Integer privilege) {
        if (userId == null) {
            return null;
        }
        return new UserInfo(new Types.UserID(userId), username, password, privilege == null ? 0 : privilege);
    }

    public static TripInfo toTripInfo(String trainId, Integer departureStation, Integer arrivalStation, Integer type,
                                      Integer duration, Integer price, String departureTime, String arrivalTime) {
        TripInfo tripInfo = new TripInfo();
        tripInfo.setTrainID(new Types.TrainID(trainId));
        tripInfo.setDepartureStation(new Types.StationID(departureStation == null ? -1 : departureStation));
        tripInfo.setArrivalStation(new Types.StationID(arrivalStation == null ? -1 : arrivalStation));
        tripInfo.setType(type == null ? 0 : type);
        tripInfo.setDuration(duration == null ? 0 : duration);
        tripInfo.setPrice(price == null ? 0 : price);
        if (departureTime != null) {
            tripInfo.setDepartureTime(new Time(departureTime));
        }
        if (arrivalTime != null) {
            tripInfo.setArrivalTime(new Time(arrivalTime));
        }
        return tripInfo;
    }
}
