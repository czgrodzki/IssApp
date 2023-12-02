package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            System.out.println("3. Zakończ aplikacje");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    //Sprawdź położenie ISS

                    //Stworzenie HTTP klienta, request i wysłanie requestu z rządaniem odpowiedzi
                    final HttpResponse<String> stringHttpResponseLocation = getStringHttpResponse(ISS_API_LOCATION);

                    //Tworzymy sobie mppera, żeby wciągnąć wartość z JSONa, czyli odpowiedzi z zewnętrznego serwisu
                    final JsonNode jsonNode = getJsonNode(stringHttpResponseLocation);

                    //Wyciągamy timestamp jako long
                    long timestamp = jsonNode.at("/timestamp").asLong();

                    //Tworzymy obiekt instant, który będzie nam potrzebny do stworzenia dalej obiektu LocalDataTiem
                    Instant instant = Instant.ofEpochSecond(timestamp);
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                    //Wyciągamy szerokość i długość ISS
                    final double lat = jsonNode.at("/iss_position/latitude").asDouble();
                    final double lon = jsonNode.at("/iss_position/longitude").asDouble();

                    System.out.println("Dnia " + localDateTime + " ISS " + " jest w miejscu szerokość: " + lat + " długość " + lon);

                    writeToCsv("iss_location.csv", true, "date", localDateTime.toString(), "lat", String.valueOf(lat), "lon", String.valueOf(lon));
                    break;

                case 2:
                    final HttpResponse<String> stringHttpResponsePeople = getStringHttpResponse(ISS_API_PEOPLE);

                    final JsonNode jsonNode1 = getJsonNode(stringHttpResponsePeople);
                    final int totalNumber = jsonNode1.at("/number").asInt();

                    //TODO Jak obejść to, żeby zapisaywało do pliku wszystkich ludzi raz i później już nie ndapisywało
                    StringBuilder people = new StringBuilder();
                    for (JsonNode jsonArrayNode : jsonNode1.at("/people")) {
                        String name = jsonArrayNode.at("/name").asText();
                        System.out.println(name);
                        writeToCsv("iss_people.csv", true, name);
                        people.append(name).append(",");
                    }
                    people.append(totalNumber).append("\n");
                    writeToCsv("iss_people.csv", true, String.valueOf(totalNumber));


                    System.out.println("Wszystkich osób jest " + totalNumber);
                    break;

                case 3:
                    System.out.println("Zamkykamy appkę");
                    break;

                default:
                    System.out.println("Nie ma takiej komendy");
                    break;
            }

        } while (choice != 3);

        scanner.close();
    }

    private static JsonNode getJsonNode(final HttpResponse<String> stringHttpResponseLocation) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(stringHttpResponseLocation.body());
    }

    private static HttpResponse<String> getStringHttpResponse(final String uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());

    }

    private static void writeToCsv(String file, boolean shouldAppend, String... arguments) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, shouldAppend))) {
            StringBuilder line = new StringBuilder();
            for (String argument : arguments) {
                line.append(argument).append(",");
            }
            line.append("\n");
            writer.write(line.toString());
        }

    }
}