package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner scanner = new Scanner(System.in);

        int choice;

        do {
            System.out.println("1. Pobierz położenie ISS");
            System.out.println("2. Zakończ aplikacje");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    //Sprawdź położenie ISS

                    //Stworzenie HTTP klienta, request i wysłanie requestu z rządaniem odpowiedzi
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request =HttpRequest.newBuilder()
                            .uri(URI.create(ISS_API_LOCATION))
                            .build();
                    final HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());

                    //Tworzymy sobie mppera, żeby wciągnąć wartość z JSONa, czyli odpowiedzi z zewnętrznego serwisu
                    ObjectMapper objectMapper = new ObjectMapper();

                    final JsonNode jsonNode = objectMapper.readTree(send.body());

                    //Wyciągamy timestamp jako long
                    long timestamp = jsonNode.at("/timestamp").asLong();

                    //Tworzymy obiekt instant, który będzie nam potrzebny do stworzenia dalej obiektu LocalDataTiem
                    Instant instant = Instant.ofEpochSecond(timestamp);
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

                    //Wyciągamy szerokość i długość ISS
                    final double lat = jsonNode.at("/iss_position/latitude").asDouble();
                    final double lon = jsonNode.at("/iss_position/longitude").asDouble();

                    System.out.println("Dnia " + localDateTime + " ISS " + " jest w miejscu szerokość: " + lat + " długość " + lon);
                    break;
                case 2:
                    System.out.println("Zamkykamy appkę");
            }

        } while (choice != 2);

        scanner.close();
    }
}