package org.himnabil.pattern.flattener.example;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.himnabil.pattern.flattener.example.Model.*;

public class Utils {

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

}
