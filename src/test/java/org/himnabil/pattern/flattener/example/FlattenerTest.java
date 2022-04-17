package org.himnabil.pattern.flattener.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.himnabil.pattern.flattener.example.Flattener.*;
import static org.himnabil.pattern.flattener.example.Model.DataNature;
import static org.himnabil.pattern.flattener.example.Model.User;
import static org.himnabil.pattern.flattener.example.Utils.generateRandomUser;
class FlattenerTest {

    final int nbHomes = 300;
    final int nbRooms = 6;
    final int nbDevices = 40;
    final List<DataNature> dataNatures = List.of(
            new DataNature("TEMPERATURE"),
            new DataNature("HUMIDITY"),
            new DataNature("LIGHT"),
            new DataNature("CO2")
    );
    final int nbPoints = 430;
    Duration step = Duration.ofMinutes(1);
    final private Instant start = Instant.now().minus(step.multipliedBy(nbPoints));


    final int totalNbPoints = nbHomes * nbRooms * nbDevices * dataNatures.size() * nbPoints;

    final User user = generateRandomUser(nbHomes, nbRooms, nbDevices, dataNatures, nbPoints, start, step);


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
    void flattenNominalCase(int i, String name, Flattener flattener) {
        assertThat(flattener
                .flatten(user)
                .toList()
        ).hasSize(totalNbPoints);
    }
}