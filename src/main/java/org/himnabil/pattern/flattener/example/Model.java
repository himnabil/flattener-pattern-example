package org.himnabil.pattern.flattener.example;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Model {

     public record User(UUID userId, List<Home> homes) {}
     public record Home(UUID homeId, List<Room> rooms) {}
     public record Room(UUID roomId, List<Device> devices) {}
    
     public record Device(UUID deviceId, List<Data> data) {}

    public record DataNature(String nature) {} // e.g. temperature, humidity, etc.

    public record Data(DataNature nature, List<DataPoint> points) {}

    public record DataPoint(Instant ts, double value) {}

}
