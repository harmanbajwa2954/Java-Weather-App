import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherAppData {

    // Fetch weather data for the given location name
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        // Extract latitude and longitude from the location data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // API request URL with necessary parameters for weather data
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,precipitation_probability,weather_code,visibility,wind_speed_10m," +
                "wind_direction_10m,uv_index,is_day,sunshine_duration&timezone=Asia%2FBangkok";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // Parse the JSON response
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // Extract hourly weather data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // Fetch individual data points for the current hour
            double temperature = (double) ((JSONArray) hourly.get("temperature_2m")).get(index);
            String weatherCondition = convertWeatherCode((long) ((JSONArray) hourly.get("weather_code")).get(index));
            long humidity = (long) ((JSONArray) hourly.get("relative_humidity_2m")).get(index);
            double windspeed = (double) ((JSONArray) hourly.get("wind_speed_10m")).get(index);
            long windDirection = (long) ((JSONArray) hourly.get("wind_direction_10m")).get(index);
            long precipitation = (long) ((JSONArray) hourly.get("precipitation_probability")).get(index);
            double visibility = (double) ((JSONArray) hourly.get("visibility")).get(index);
            double uvIndex = (double) ((JSONArray) hourly.get("uv_index")).get(index);
            double sunshineDuration = (double) ((JSONArray) hourly.get("sunshine_duration")).get(index);
            boolean isDay = ((JSONArray) hourly.get("is_day")).get(index).equals(1L);

            // Build and return the weather data object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            weatherData.put("wind_direction", windDirection);
            weatherData.put("precipitation", precipitation);
            weatherData.put("visibility", visibility);
            weatherData.put("uv_index", uvIndex);
            weatherData.put("sunshine_duration", sunshineDuration);
            weatherData.put("is_day", isDay);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Retrieves location data (latitude, longitude) based on location name
    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect API");
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                return (JSONArray) resultJsonObj.get("results");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Fetch API response based on URL string
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get the current time in the format required by the API
    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currentDateTime.format(formatter);
    }

    // Find the index of the current hour in the time array
    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i).equals(currentTime)) {
                return i;
            }
        }
        return 0; // Default to the first element if current time is not found
    }

    // Convert weather code to descriptive weather condition
    private static String convertWeatherCode(long weathercode) {
        if (weathercode == 0L) return "Clear";
        if (weathercode <= 3L && weathercode > 0L) return "Cloudy";
        if ((weathercode >= 51L && weathercode <= 67L) || (weathercode >= 80L && weathercode <= 99L)) return "Rain";
        if (weathercode >= 71L && weathercode <= 77L) return "Snow";
        return "Unknown";
    }
}

