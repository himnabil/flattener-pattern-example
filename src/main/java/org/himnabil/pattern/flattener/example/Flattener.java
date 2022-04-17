package org.himnabil.pattern.flattener.example;

import static org.himnabil.pattern.flattener.example.FlattenedModel.*;
import static org.himnabil.pattern.flattener.example.Model.*;

import java.util.function.Function;
import java.util.stream.Stream;
@FunctionalInterface
public interface Flattener {
    Stream<FlattenDataPoint> flatten(User user);

    Flattener naiveFlattener = user -> {
        Stream.Builder<FlattenDataPoint> builder = Stream.builder();
        for (Home home : user.homes()) {
            for (Room room : home.rooms()) {
                for (Device device : room.devices()) {
                    for (Data data : device.data()) {
                        for (DataPoint dataPoint : data.points()) {
                            builder.add(
                                    new FlattenDataPoint(
                                            user.userId(),
                                            home.homeId(),
                                            room.roomId(),
                                            device.deviceId(),
                                            data.nature(),
                                            dataPoint.ts(),
                                            dataPoint.value()
                                    )
                            );
                        }
                    }
                }
            }
        }
        return builder.build();
    };

    Flattener structuralFlattener = user -> streamFlattenHome(user)
            .flatMap(FlattenHome::stream)
            .flatMap(FlattenRoom::stream)
            .flatMap(FlattenDevice::stream)
            .flatMap(FlattenData::stream);

    Flattener structuralParallelFlattener = user -> streamFlattenHome(user)
            .parallel()
            .flatMap(FlattenHome::stream)
            .flatMap(FlattenRoom::stream)
            .flatMap(FlattenDevice::stream)
            .flatMap(FlattenData::stream);

    Flattener structuralAllParallelFlattener = user -> parallelStreamFlattenHome(user)
            .flatMap(FlattenHome::parallelStream)
            .flatMap(FlattenRoom::parallelStream)
            .flatMap(FlattenDevice::parallelStream)
            .flatMap(FlattenData::parallelStream);

}
