package connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.WeatherRecordDao;
import model.WeatherRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;

/*
@author Sambhav D Sethia
 */
public class SQLConnector {
    private static final Logger logger =  LoggerFactory.getLogger(SQLConnector.class);

    private final String apiBaseUrl;
    private final String apiKey;
    private final WeatherRecordDao weatherRecordDao;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SQLConnector(String apiBaseUrl, String apiKey, Connection dbConnection) {
        this.apiBaseUrl = apiBaseUrl;
        this.apiKey = apiKey;
        this.weatherRecordDao = new WeatherRecordDao(dbConnection);
    }

    public void syncData(String city, String units) {
        try{
            StringBuilder urlBuilder = new StringBuilder(apiBaseUrl)
                    .append("?q=").append(city)
                    .append("&units=").append(units)
                    .append("&appid=").append(apiKey);
            String url = urlBuilder.toString();
            WeatherRecord record = fetchWeatherRecord(url);
            weatherRecordDao.upsertWeatherRecord(record);
            logger.info("Successfully synced weather data for city: {}",city);
        }catch (Exception e) {
            logger.error("Error Syncing weather data", e);
        }
    }

    private WeatherRecord fetchWeatherRecord(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        conn.setRequestMethod("GET");
        int responseCode = conn.getResponseCode();
        if(responseCode != 200) {
            throw new RuntimeException(" Failed to fetch HTTP " + responseCode);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return objectMapper.readValue(response.toString(), WeatherRecord.class);
    }
}
