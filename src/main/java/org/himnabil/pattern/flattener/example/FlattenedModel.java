package org.himnabil.pattern.flattener.example;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.himnabil.pattern.flattener.example.Model.*;

public class FlattenedModel {

    public static Stream<FlattenHome> streamFlattenHome(User user) {
        return user.homes().stream()
                .map(home -> new FlattenHome(user.userId(), home.homeId(), home.rooms()));
    }

    public record FlattenHome(UUID userId, UUID homeId, List<Room> rooms) {
        public Stream<FlattenRoom> stream() {
            return rooms.stream()
                    .map(room -> new FlattenRoom(userId, homeId, room.roomId(), room.devices()));
        }
    }

    public record FlattenRoom(UUID userId, UUID homeId, UUID roomId, List<Device> devices) {
        public Stream<FlattenDevice> stream() {
            return devices.stream()
                    .map(device -> new FlattenDevice(userId, homeId, roomId, device.deviceId(), device.data()));
        }
    }

    public record FlattenDevice(UUID userId, UUID homeId, UUID roomId, UUID deviceId, List<Data> data) {
        public Stream<FlattenData> stream() {
            return data.stream()
                    .map(data -> new FlattenData(userId, homeId, roomId, deviceId, data.nature(), data.points()));
        }
    }

    public record FlattenData(UUID userId, UUID homeId, UUID roomId, UUID deviceId, DataNature nature,
                              List<DataPoint> points) {
        public Stream<FlattenDataPoint> stream() {
            return points.stream()
                    .map(point -> new FlattenDataPoint(userId, homeId, roomId, deviceId, nature, point.ts(),
                            point.value()));
        }
    }

    public record FlattenDataPoint(
            UUID userId,
            UUID homeId,
            UUID roomId,
            UUID deviceId,
            Model.DataNature nature,
            Instant ts,
            double value
    ){}
}
