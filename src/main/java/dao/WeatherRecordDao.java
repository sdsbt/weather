package dao;

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
@author Sambhav D Sethia
 */
public class WeatherRecordDao {
    private final Connection connection;

    public WeatherRecordDao(Connection connection) {
        this.connection = connection;
    }

    public void upsertWeatherRecord(WeatherRecord record) throws SQLException {
         upsertWeatherRecordCore(record);
         upsertSys(record.getId(), record.getSys());
         for (Weather weather : record.getWeather()) {
            upsertWeather(record.getId(), weather);
         }
        // 4. Insert or update Main
        upsertMain(record.getId(), record.getMain());
        // 5. Insert or update Wind
        upsertWind(record.getId(), record.getWind());
        // 6. Insert or update Clouds
        upsertClouds(record.getId(), record.getClouds());

    }
    private void upsertWeatherRecordCore(WeatherRecord record) throws SQLException {
        String sql  =   "INSERT INTO weather_data.weather_records (id, name, base, visibility, dt, " +
                        "timezone, cod, lon, lat, temp, humidity)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)" +
                        "ON CONFLICT (id) DO UPDATE SET " +
                        "name = EXCLUDED.NAME, base = EXCLUDED.base, visibility = EXCLUDED.visibility, "+
                        "dt = EXCLUDED.DT, timezone = EXCLUDED.timezone, cod = EXCLUDED.cod, " +
                        "lon = EXCLUDED.lon, lat = EXCLUDED.lat, " +
                        "temp = EXCLUDED.temp, humidity = EXCLUDED.humidity";
        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, record.getId());
            ps.setString(2, record.getName());
            ps.setString(3, record.getBase());
            ps.setInt(4, record.getVisibility());
            ps.setLong(5, record.getDt());
            ps.setInt(6, record.getTimezone());
            ps.setInt(7, record.getCod());
            ps.setDouble(8, record.getCoord().getLon());
            ps.setDouble(9, record.getCoord().getLat());
            ps.setDouble(10, record.getMain().getTemp());
            ps.setInt(11, record.getMain().getHumidity());
            ps.executeUpdate();
        }
    }

    private void upsertSys(int weatherRecordId, Sys sys) throws SQLException {
        String sql = "INSERT INTO weather_data.systems (weather_record_id, type, system_id, country, sunrise, sunset) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (weather_record_id) DO UPDATE SET " +
                "type = EXCLUDED.type, system_id = EXCLUDED.system_id, country = EXCLUDED.country, " +
                "sunrise = EXCLUDED.sunrise, sunset = EXCLUDED.sunset";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, weatherRecordId);
            ps.setInt(2, sys.getType());
            ps.setInt(3, sys.getId());
            ps.setString(4, sys.getCountry());
            ps.setLong(5, sys.getSunrise());
            ps.setLong(6, sys.getSunset());
            ps.executeUpdate();
        }
    }

    private void upsertWeather(int weatherRecordId, Weather weather) throws SQLException {
        String sql = "INSERT INTO weather_data.weathers (weather_record_id, weather_id, main, description, icon) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (weather_record_id, weather_id) DO UPDATE SET " +
                "main = EXCLUDED.main, description = EXCLUDED.description, icon = EXCLUDED.icon";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, weatherRecordId);
            ps.setInt(2, weather.getId());
            ps.setString(3, weather.getMain());
            ps.setString(4, weather.getDescription());
            ps.setString(5, weather.getIcon());
            ps.executeUpdate();
        }
    }

    private void upsertMain(int weatherRecordId, Stats main) throws SQLException {
        String sql = "INSERT INTO weather_data.main_weathers (weather_record_id, temp, feels_like, temp_min, temp_max, pressure, humidity, sea_level, grnd_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (weather_record_id) DO UPDATE SET " +
                "temp = EXCLUDED.temp, feels_like = EXCLUDED.feels_like, temp_min = EXCLUDED.temp_min, " +
                "temp_max = EXCLUDED.temp_max, pressure = EXCLUDED.pressure, humidity = EXCLUDED.humidity, " +
                "sea_level = EXCLUDED.sea_level, grnd_level = EXCLUDED.grnd_level";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, weatherRecordId);
            ps.setDouble(2, main.getTemp());
            ps.setDouble(3, main.getFeelsLike());
            ps.setDouble(4, main.getTempMin());
            ps.setDouble(5, main.getTempMax());
            ps.setInt(6, main.getPressure());
            ps.setInt(7, main.getHumidity());
            ps.setInt(8, main.getSeaLevel());
            ps.setInt(9, main.getGrndLevel());
            ps.executeUpdate();
        }
    }
    private void upsertWind(int weatherRecordId, Wind wind) throws SQLException {
        String sql = "INSERT INTO weather_data.winds (weather_record_id, speed, deg) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (weather_record_id) DO UPDATE SET " +
                "speed = EXCLUDED.speed, deg = EXCLUDED.deg";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, weatherRecordId);
            ps.setDouble(2, wind.getSpeed());
            ps.setInt(3, wind.getDeg());
            ps.executeUpdate();
        }
    }

    private void upsertClouds(int weatherRecordId, Clouds clouds) throws SQLException {
        String sql = "INSERT INTO weather_data.clouds (weather_record_id, cloud_cover) " +
                "VALUES (?, ?) " +
                "ON CONFLICT (weather_record_id) DO UPDATE SET " +
                "cloud_cover = EXCLUDED.cloud_cover";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, weatherRecordId);
            ps.setInt(2, clouds.getCloudCover());
            ps.executeUpdate();
        }
    }
}
