// Добавлены необходимые импорты
package ru.stileyn;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static ru.stileyn.App.logger;

public class App {

    // Добавлен логгер
    static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            // Чтение JSON из файла
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("/home/stileyn/devs/java/Tickets/src/main/resources/tickets.json"));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray tickets = (JSONArray) jsonObject.get("tickets");

            // Инициализация структур данных для хранения информации
            List<FlightInfo> flightInfoList = new ArrayList<>();

            // Обработка каждого билета
            for (Object ticket : tickets) {
                JSONObject ticketObj = (JSONObject) ticket;

                String origin = (String) ticketObj.get("origin");
                String originName = (String) ticketObj.get("origin_name");
                String destination = (String) ticketObj.get("destination");
                String destinationName = (String) ticketObj.get("destination_name");
                String departureDate = (String) ticketObj.get("departure_date");
                String departureTime = (String) ticketObj.get("departure_time");
                String arrivalDate = (String) ticketObj.get("arrival_date");
                String arrivalTime = (String) ticketObj.get("arrival_time");
                String carrier = (String) ticketObj.get("carrier");
                long stops = (Long) ticketObj.get("stops");
                long price = (Long) ticketObj.get("price");

                if (origin.equals("VVO") && destination.equals("TLV")) {
                    // Расчет минимального времени полета для каждого авиаперевозчика
                    FlightInfo flightInfo = new FlightInfo(
                            origin, originName, destination, destinationName,
                            departureDate, departureTime, arrivalDate, arrivalTime,
                            carrier, (int) stops, (int) price
                    );
                    flightInfoList.add(flightInfo);
                }
            }

            // Расчет минимального времени полета для каждого авиаперевозчика
            calculateMinFlightTime(flightInfoList);

            // Расчет разницы между средней ценой и медианой
            calculatePriceDifference(flightInfoList);

        } catch (Exception e) {
            // Заменен вывод стека исключения на логирование ошибки
            logger.error("Произошла ошибка: {}", e.getMessage());
        }
    }

    private static void calculateMinFlightTime(List<FlightInfo> flightInfoList) {
        // Сортировка списка по времени полета
        flightInfoList.sort(Comparator.comparing(FlightInfo::getFlightTime));

        // Нахождение минимального времени полета для каждого авиаперевозчика
        System.out.println("Минимальное время полета между Владивостоком и Тель-Авивом для каждого авиаперевозчика:");
        for (String carrier : getUniqueCarriers(flightInfoList)) {
            FlightInfo minFlight = getMinFlightInfoForCarrier(flightInfoList, carrier);
            if (minFlight != null && minFlight.getFlightTime() != null) {
                System.out.println(carrier + ": " + minFlight.getFlightTime() + " (" +
                        minFlight.getDepartureDate() + " " + minFlight.getDepartureTime() + " - " +
                        minFlight.getArrivalDate() + " " + minFlight.getArrivalTime() + ")");
            } else {
                System.out.println(carrier + ": Нет данных о времени полета");
            }
        }
    }

    private static void calculatePriceDifference(List<FlightInfo> flightInfoList) {
        // Расчет средней цены и медианы
        int totalPrices = 0;
        List<Integer> prices = new ArrayList<>();
        for (FlightInfo flightInfo : flightInfoList) {
            totalPrices += flightInfo.getPrice();
            prices.add(flightInfo.getPrice());
        }
        double averagePrice = totalPrices / (double) flightInfoList.size();
        Collections.sort(prices);

        // Нахождение медианы
        int median;
        if (prices.size() % 2 == 0) {
            median = (prices.get(prices.size() / 2 - 1) + prices.get(prices.size() / 2)) / 2;
        } else {
            median = prices.get(prices.size() / 2);
        }

        // Вывод разницы между средней ценой и медианой
        System.out.println("\nРазница между средней ценой и медианой: " + (averagePrice - median));
    }

    private static List<String> getUniqueCarriers(List<FlightInfo> flightInfoList) {
        List<String> uniqueCarriers = new ArrayList<>();
        for (FlightInfo flightInfo : flightInfoList) {
            if (!uniqueCarriers.contains(flightInfo.getCarrier())) {
                uniqueCarriers.add(flightInfo.getCarrier());
            }
        }
        return uniqueCarriers;
    }

    private static FlightInfo getMinFlightInfoForCarrier(List<FlightInfo> flightInfoList, String carrier) {
        for (FlightInfo flightInfo : flightInfoList) {
            if (flightInfo.getCarrier().equals(carrier)) {
                return flightInfo;
            }
        }
        return null;
    }
}

// Добавлены геттеры для неиспользуемых полей класса
class FlightInfo {
    private final String origin;
    private final String originName;
    private final String destination;
    private final String destinationName;
    private final String departureDate;
    private final String departureTime;
    private final String arrivalDate;
    private final String arrivalTime;
    private final String carrier;
    private final int stops;
    private final int price;

    public FlightInfo(String origin, String originName, String destination, String destinationName,
                      String departureDate, String departureTime, String arrivalDate, String arrivalTime,
                      String carrier, int stops, int price) {
        this.origin = origin;
        this.originName = originName;
        this.destination = destination;
        this.destinationName = destinationName;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.carrier = carrier;
        this.stops = stops;
        this.price = price;
    }

    public String getOrigin() {
        return origin;
    }

    public String getOriginName() {
        return originName;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getCarrier() {
        return carrier;
    }

    public int getStops() {
        return stops;
    }

    public int getPrice() {
        return price;
    }

    public String getFlightTime() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yy HH:mm");
        try {
            Date departureDateTime = format.parse(departureDate + " " + departureTime);
            Date arrivalDateTime = format.parse(arrivalDate + " " + arrivalTime);

            long diff = arrivalDateTime.getTime() - departureDateTime.getTime();
            long minutes = diff / (60 * 1000);

            long hours = minutes / 60;
            minutes = minutes % 60;

            return String.format("%02d:%02d", hours, minutes);
        } catch (ParseException e) {
            // Заменен вывод стека исключения на логирование ошибки
            logger.error("Ошибка при расчете времени полета: {}", e.getMessage());
            return "00:00"; // Обработка ошибки в расчете времени полета
        }
    }
}
