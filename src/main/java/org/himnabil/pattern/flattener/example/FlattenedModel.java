package org.himnabil.pattern.flattener.example;

import java.time.Instant;
import java.util.UUID;

public class FlattenedModel {

    public record FlattenDataPoint(
            UUID userId,
            UUID homeId,
            UUID deviceId,
            Model.DataNature nature,
            Instant ts,
            double value
    ){}
}
