package org.himnabil.pattern.flattener.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
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

    int nbHomes = 250;
    int nbRooms = 4;
    int nbDevices = 4;
    List<DataNature> dataNatures = List.of(
            new DataNature("TEMPERATURE"),
            new DataNature("HUMIDITY"),
            new DataNature("LIGHT"),
            new DataNature("CO2")
    );
    int nbPoints = 4000;
    Duration step = Duration.ofMinutes(1);

    int totalNbPoints = nbHomes * nbRooms * nbDevices * dataNatures.size() * nbPoints;

    User user;



    static double randomValue() {
        return (Math.random() * 100) - 50;
    }

    @BeforeEach
    void setUp() {
        user = generateRandomUser(
                nbHomes,
                nbRooms,
                nbDevices,
                dataNatures,
                nbPoints,
                Instant.now().minus(step.multipliedBy(nbDevices)),
                step);
    }


    static Stream<Arguments> flatteners(){
        return IntStream.range(0, 10).boxed()
                .flatMap(i -> Stream.of(
                        Arguments.of(i, "naive ",naiveFlattener),
                        Arguments.of(i, "structural",structuralFlattener),
                        Arguments.of(i, "structuralParallel",structuralParallelFlattener),
                        Arguments.of(i, "structuralAllParallel",structuralAllParallelFlattener)
                ));
    }

    @ParameterizedTest(name = "flatterer : {0}, {1}")
    @MethodSource("flatteners")
    void flattenNominalCase(int i, String name,Flattener flattener) {
        assertThat(flattener
                .flatten(user)
                .toList()
        ).hasSize(totalNbPoints);
    }
}