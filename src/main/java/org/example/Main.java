package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Crew;
import org.example.model.IssLocation;
import org.example.service.CalculationService;
import org.example.service.MapperService;
import org.example.storage.CsvFileStorage;
import org.example.webclient.WebClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final String ISS_API_LOCATION = "http://api.open-notify.org/iss-now.json";
    private static final String ISS_API_PEOPLE = "http://api.open-notify.org/astros.json";

    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        int choice;

        do {
            System.out.println("1. Pobierz położenie ISS");
            System.out.println("2. Pobierz ludzi na ISS");
            System.out.println("3. Pokaż prędkość ISS");
            System.out.println("4. Policz średnią prędkość z pliku");
            System.out.println("5. Zakończ aplikacje");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    //Sprawdź położenie ISS

                    //Stworzenie HTTP klienta, request i wysłanie requestu z rządaniem odpowiedzi
                    final HttpResponse<String> stringHttpResponseLocation = WebClient.getStringHttpResponse(ISS_API_LOCATION);

                    //Tworzymy sobie mppera, żeby wciągnąć wartość z JSONa, czyli odpowiedzi z zewnętrznego serwisu
                    final JsonNode jsonNode = MapperService.getJsonNode(stringHttpResponseLocation);

                    //Wyciągamy timestamp jako long
                    long timestamp = jsonNode.at("/timestamp").asLong();

                    //Tworzymy obiekt instant, który będzie nam potrzebny do stworzenia dalej obiektu LocalDataTiem
                    Instant instant = Instant.ofEpochSecond(timestamp);

                    IssLocation issLocation = new IssLocation(
                            LocalDateTime.ofInstant(instant, ZoneId.systemDefault()),
                            jsonNode.at("/iss_position/latitude").asDouble(),
                            jsonNode.at("/iss_position/longitude").asDouble());

                    System.out.println("Dnia " + issLocation.getLocalDateTime() + " ISS " + " jest w miejscu szerokość: "
                            + issLocation.getLat() + " długość " + issLocation.getLat());

                    CsvFileStorage.writeToCsv("iss_location.csv", true, "date", issLocation.getLocalDateTime().toString(),
                            "lat", String.valueOf(issLocation.getLat()), "lon", String.valueOf(issLocation.getLon()));
                    break;

                case 2:
                    final HttpResponse<String> stringHttpResponsePeople = WebClient.getStringHttpResponse(ISS_API_PEOPLE);

                    final JsonNode jsonNode1 = MapperService.getJsonNode(stringHttpResponsePeople);
                    final int totalNumber = jsonNode1.at("/number").asInt();

                    //TODO Jak obejść to, żeby zapisaywało do pliku wszystkich ludzi raz i później już nie ndapisywało
                    StringBuilder workers = new StringBuilder();
                    List<String> names = new ArrayList<>();
                    for (JsonNode jsonArrayNode : jsonNode1.at("/people")) {
                        String name = jsonArrayNode.at("/name").asText();
                        names.add(name);
                        CsvFileStorage.writeToCsv("iss_people.csv", true, name);
                        workers.append(name).append(",");
                    }
                    Crew crew = new Crew(names);
                    System.out.println(crew.toString());
                    workers.append(totalNumber).append("\n");
                    CsvFileStorage.writeToCsv("iss_people.csv", true, String.valueOf(totalNumber));


                    System.out.println("Wszystkich osób jest " + totalNumber);
                    break;

                case 3:
                    final HttpResponse<String> stringHttpResponseFirst = WebClient.getStringHttpResponse(ISS_API_LOCATION);
                    final JsonNode jsonNodeFirst = MapperService.getJsonNode(stringHttpResponseFirst);
                    final double latFirst = jsonNodeFirst.at("/iss_position/latitude").asDouble();
                    final double lonFirst = jsonNodeFirst.at("/iss_position/longitude").asDouble();

                    //TODO weź czas z timestampa - wcześniej utwórz obiekt IssLocation i operuj na geterach
                    final long timeDifferenceInSeconds = 2;
                    Thread.sleep(Duration.ofSeconds(timeDifferenceInSeconds));

                    final HttpResponse<String> stringHttpResponseSecond = WebClient.getStringHttpResponse(ISS_API_LOCATION);
                    final JsonNode jsonNodeSecond = MapperService.getJsonNode(stringHttpResponseSecond);
                    final double latSecond = jsonNodeSecond.at("/iss_position/latitude").asDouble();
                    final double lonSecond = jsonNodeSecond.at("/iss_position/longitude").asDouble();

                    final double distance = CalculationService.calculateDistance(latFirst, lonFirst, latSecond, lonSecond);

                    //dorga/przez czas
                    double speed = distance / timeDifferenceInSeconds;
                    System.out.println("Iss is going " + speed + "km/s");
                    CsvFileStorage.writeToCsv("iss_speed.csv", true, "Speed", String.valueOf(speed));
                    break;

                case 4:
                    final List<String> speedValues = CsvFileStorage.readSpeedFromCsvFile("iss_speed.csv");
                    final double average = CalculationService.getAverage(speedValues);

                    System.out.println("Średnia prędkość z pliku to " + average);
                    break;

                case 5:
                    System.out.println("Zamkykamy appkę");
                    break;

                default:
                    System.out.println("Nie ma takiej komendy");
                    break;
            }

        } while (choice != 5);

        scanner.close();
    }

}