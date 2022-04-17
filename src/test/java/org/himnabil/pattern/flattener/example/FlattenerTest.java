package org.himnabil.pattern.flattener.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import static org.himnabil.pattern.flattener.example.Model.*;
import static org.himnabil.pattern.flattener.example.FlattenedModel.*;
import static org.himnabil.pattern.flattener.example.Flattener.*;
class FlattenerTest {

    static User generateRandomUser(int nbHomes,
                                   int nbRooms,
                                   int nbDevices,
                                   List<DataNature> dataNatures,
                                   int nbPoints,
                                   Instant start,
                                   Duration step) {
        return new User(
                UUID.randomUUID(),
                Stream.generate(
                        () -> generateRandomHome(
                                nbRooms,
                                nbDevices,
                                dataNatures,
                                nbPoints,
                                start,
                                step))
                        .limit(nbHomes)
                        .toList()
                );
    }

    static Home generateRandomHome(int nbRooms,
                                   int nbDevices,
                                   List<DataNature> dataNatures,
                                   int nbPoints,
                                   Instant start,
                                   Duration step) {
        return new Home(
                UUID.randomUUID(),
                Stream.generate(
                        () -> generateRandomRoom(
                                nbDevices,
                                dataNatures,
                                nbPoints,
                                start,
                                step))
                        .limit(nbRooms)
                        .toList()
                );
    }

    static Room generateRandomRoom(int nbDevices,
                                   List<DataNature> dataNatures,
                                   int nbPoints,
                                   Instant start,
                                   Duration step) {
        return new Room(
                UUID.randomUUID(),
                Stream.generate(
                        () -> generateRandomDevice(
                                dataNatures,
                                nbPoints,
                                start,
                                step))
                        .limit(nbDevices)
                        .toList()
                );
    }

    static Device generateRandomDevice(List<DataNature> dataNatures,
                                       int nbPoints,
                                       Instant start,
                                       Duration step) {
        return new Device(
                UUID.randomUUID(),
                dataNatures.stream()
                        .map(dataNature -> generateRandomData(dataNature, nbPoints, start, step))
                        .toList()
                );
    }

    static Data generateRandomData(DataNature dataNature,
                                   int nbPoints,
                                   Instant start,
                                   Duration step) {
        return new Data(
                dataNature,
                Stream.iterate(start, startTs -> startTs.plus(step))
                        .map(ts -> new DataPoint(ts, randomValue()))
                        .limit(nbPoints)
                        .toList()
                );
    }

    static double randomValue() {
        return (Math.random() * 100) - 50;
    }

    @Test
    void flattenNominalCase() {
        User user = generateRandomUser(
                2,
                3,
                2,
                List.of(new DataNature("TEMPERATURE"), new DataNature("HUMIDITY")),
                10,
                Instant.now().minus(10, ChronoUnit.MINUTES),
                Duration.ofMinutes(1));

        List<FlattenDataPoint> result = flatten(user).toList();

        assertThat(result).hasSize(240); // 2 home * 3 room * 2 device * 2 dataNatures * 10 points
    }
}