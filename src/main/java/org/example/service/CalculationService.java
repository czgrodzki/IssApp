package org.example.service;

import java.util.List;
import java.util.Optional;

public class CalculationService {

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Promień ziemi w kilometrach
        final double r = 6371;

        // Różnice szerokości i długości geograficznych
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        // Obliczenia według wzoru haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Odległość w kilometrach
        return r * c;
    }

    public static double getAverage(final List<String> speedValues) {
        final Optional<Double> sum = speedValues.stream()
                .map(Double::valueOf)
                .reduce(Double::sum);

        double average = 0;
        if (sum.isPresent()) {
            average = sum.get() / speedValues.size();
        }
        return average;


//      To samo, mniej kodu
//      speedValues.stream()
//           .mapToDouble(Double::parseDouble)
//           .average()
//           .ifPresentOrElse(System.out::println, () -> System.out.println("Nie ma średniej"));


    }


}
