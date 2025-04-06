import org.json.simple.JSONObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    private JTextField searchTextField;
    private JLabel weatherConditionImage, temperatureText, weatherDescription, humidityText, windSpeedText, precipitationText, visibilityText, uvIndexText, sunshineDurationText, windDirectionText, isDayImage;
    private Map<String, ImageIcon> weatherIcons;

    public WeatherAppGUI() {
        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(470, 831);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(252, 243, 238));
        weatherIcons = loadWeatherIcons();
        initializeGUIComponents();
    }

    // Initialize all components and layout of the GUI
    private void initializeGUIComponents() {
        setLayout(null);

        searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 43);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        weatherConditionImage = new JLabel(weatherIcons.get("cloudy"));
        weatherConditionImage.setBounds(0, 95, 450, 217);
        add(weatherConditionImage);

        isDayImage = new JLabel(weatherIcons.get("day"));
        isDayImage.setBounds(10, 90, 50, 50);
        isDayImage.setHorizontalAlignment(SwingConstants.CENTER);
        add(isDayImage);

        temperatureText = new JLabel("10°C");
        temperatureText.setBounds(0, 320, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);


        weatherDescription = new JLabel("Cloudy");
        weatherDescription.setBounds(0, 400, 450, 36);
        weatherDescription.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherDescription.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherDescription);

        // Humidity
        JLabel humidityImage = new JLabel(weatherIcons.get("humidity"));
        humidityImage.setBounds(15, 450, 74, 66);
        add(humidityImage);

        humidityText = new JLabel("<html><b>Humidity</b>: 100%</html>");
        humidityText.setBounds(90, 450, 120, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // Wind Speed
        JLabel windSpeedImage = new JLabel(weatherIcons.get("windspeed"));
        windSpeedImage.setBounds(220, 450, 74, 66);
        add(windSpeedImage);

        windSpeedText = new JLabel("<html><b>Wind Speed</b>: 15 km/h</html>");
        windSpeedText.setBounds(310, 450, 120, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        // Precipitation
        JLabel precipitationImage = new JLabel(weatherIcons.get("precipitation"));
        precipitationImage.setBounds(15, 530, 74, 66);
        add(precipitationImage);

        precipitationText = new JLabel("<html><b>Precipitation</b>: 0%</html>");
        precipitationText.setBounds(90, 530, 120, 55);
        precipitationText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(precipitationText);

        // Visibility
        JLabel visibilityImage = new JLabel(weatherIcons.get("visibility"));
        visibilityImage.setBounds(220, 530, 74, 66);
        add(visibilityImage);

        visibilityText = new JLabel("<html><b>Visibility</b>: 10 m</html>");
        visibilityText.setBounds(310, 530, 120, 55);
        visibilityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(visibilityText);

        // UV Index
        JLabel uvIndexImage = new JLabel(weatherIcons.get("uv_index"));
        uvIndexImage.setBounds(15, 620, 74, 66);
        add(uvIndexImage);

        uvIndexText = new JLabel("<html><b>UV Index</b>: 5</html>");
        uvIndexText.setBounds(90, 620, 120, 55);
        uvIndexText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(uvIndexText);

        // Sunshine Duration
        JLabel sunshineDurationImage = new JLabel(weatherIcons.get("sunshine_duration"));
        sunshineDurationImage.setBounds(220, 620, 74, 66);
        add(sunshineDurationImage);

        sunshineDurationText = new JLabel("<html><b>Sunshine</b>: 3 mins</html>");
        sunshineDurationText.setBounds(310, 620, 120, 55);
        sunshineDurationText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(sunshineDurationText);

        // Wind Direction
        JLabel windDirectionImage = new JLabel(weatherIcons.get("wind_direction"));
        windDirectionImage.setBounds(105, 720, 74, 66);
        add(windDirectionImage);

        windDirectionText = new JLabel("<html><b>Wind Direction</b>: 180°</html>");
        windDirectionText.setBounds(180, 720, 120, 55);
        windDirectionText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windDirectionText);

        JButton searchButton = new JButton(weatherIcons.get("search"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 43, 43);

        searchButton.addActionListener(this::onSearchButtonClick);
        add(searchButton);
    }

    // Handle search button click to fetch and display weather data
    private void onSearchButtonClick(ActionEvent e) {
        String userInput = searchTextField.getText().trim();
        if (userInput.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a location.");
            return;
        }

        weatherData = WeatherAppData.getWeatherData(userInput);
        if (weatherData == null) {
            JOptionPane.showMessageDialog(this, "Weather data not found for this location.");
            return;
        }

        updateWeatherDisplay();
    }

    // Update GUI elements with the fetched weather data
    private void updateWeatherDisplay() {
        String weatherCondition = (String) weatherData.get("weather_condition");
        weatherConditionImage.setIcon(weatherIcons.getOrDefault(weatherCondition.toLowerCase(), weatherIcons.get("cloudy")));

        double temperature = (double) weatherData.get("temperature");
        temperatureText.setText(String.format("%.1f°C", temperature));

        weatherDescription.setText(weatherCondition);

        long humidity = (long) weatherData.get("humidity");
        humidityText.setText(String.format("<html><b>Humidity</b>: %d%%</html>", humidity));

        double windSpeed = (double) weatherData.get("windspeed");
        windSpeedText.setText(String.format("<html><b>Wind Speed</b>: %.1f km/h</html>", windSpeed));

        long precipitation = (long) weatherData.get("precipitation");
        precipitationText.setText(String.format("<html><b>Precipitation</b>: %d%%</html>", precipitation));

        double visibility = (double) weatherData.get("visibility");
        visibilityText.setText(String.format("<html><b>Visibility</b>: %.1f m</html>", visibility));

        double uvIndex = (double) weatherData.get("uv_index");
        uvIndexText.setText(String.format("<html><b>UV Index</b>: %.1f</html>", uvIndex));

        double sunshineDuration = (double) weatherData.get("sunshine_duration");
        sunshineDurationText.setText(String.format("<html><b>Sunshine</b>: %.1f mins</html>", sunshineDuration));

        long windDirection = (long) weatherData.get("wind_direction");
        windDirectionText.setText(String.format("<html><b>Wind Direction</b>: %d°</html>", windDirection));

        boolean isDay = (boolean) weatherData.get("is_day");
        isDayImage.setIcon(weatherIcons.get(isDay ? "day" : "night"));
    }

    // Load weather icons into a HashMap for quick access
    private Map<String, ImageIcon> loadWeatherIcons() {
        Map<String, ImageIcon> icons = new HashMap<>();
        icons.put("clear", loadImageIcon("src/assets/clear.png"));
        icons.put("cloudy", loadImageIcon("src/assets/cloudy.png"));
        icons.put("rain", loadImageIcon("src/assets/rain.png"));
        icons.put("snow", loadImageIcon("src/assets/snow.png"));
        icons.put("humidity", loadImageIcon("src/assets/humidity.png"));
        icons.put("windspeed", loadImageIcon("src/assets/windspeed.png"));
        icons.put("search", loadImageIcon("src/assets/search.png"));
        icons.put("precipitation", loadImageIcon("src/assets/precipitation.png"));
        icons.put("visibility", loadImageIcon("src/assets/visibility.png"));
        icons.put("uv_index", loadImageIcon("src/assets/uv_index.png"));
        icons.put("sunshine_duration", loadImageIcon("src/assets/sunshine.png"));
        icons.put("wind_direction", loadImageIcon("src/assets/wind_direction.png"));
        icons.put("day", loadImageIcon("src/assets/daytime.png"));
        icons.put("night", loadImageIcon("src/assets/nighttime.png"));
        return icons;
    }

    // Helper method to load an image icon from a file path
    private ImageIcon loadImageIcon(String path) {
        try {
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        } catch (IOException e) {
            System.err.println("Could not load image from path: " + path);
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherAppGUI().setVisible(true));
    }
}


