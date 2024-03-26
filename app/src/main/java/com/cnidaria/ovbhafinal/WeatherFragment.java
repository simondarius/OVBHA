package com.cnidaria.ovbhafinal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class WeatherFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private TableLayout weatherInfoTable;

    public WeatherFragment() {

    }



    public static WeatherFragment newInstance(String param1, String param2) {
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private double calculateMedian(List<Double> values) {
        Collections.sort(values);
        int size = values.size();
        if (size % 2 == 0) {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        } else {
            return values.get(size / 2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        weatherInfoTable = view.findViewById(R.id.weatherInfoTable);

        double latitude = 52.52;
        double longitude = 13.41;


        String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current=temperature_2m,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,wind_speed_10m";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        class DailyWeatherData {
                            String day;
                            double medianTemperature;
                            double medianWindSpeed;
                            double medianHumidity;

                            public DailyWeatherData(String day, double medianTemperature, double medianWindSpeed, double medianHumidity) {
                                this.day = day;
                                this.medianTemperature = medianTemperature;
                                this.medianWindSpeed = medianWindSpeed;
                                this.medianHumidity = medianHumidity;
                            }
                        }

                        List<DailyWeatherData> dailyWeatherList = new ArrayList<>();
                        JSONObject currentObj = response.getJSONObject("current");
                        JSONObject hourlyObj = response.getJSONObject("hourly");

                        JSONArray timeArray = hourlyObj.getJSONArray("time");
                        JSONArray temperatureArray = hourlyObj.getJSONArray("temperature_2m");
                        JSONArray windSpeedArray = hourlyObj.getJSONArray("wind_speed_10m");
                        JSONArray relativeHumidityArray = hourlyObj.getJSONArray("relative_humidity_2m");

                        // Process data to get daily median values
                        Map<String, List<Double>> dailyData = new HashMap<>();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM", Locale.US);

                        for (int i = 0; i < timeArray.length(); i++) {
                            String timestamp = timeArray.getString(i);
                            Date date = inputFormat.parse(timestamp);
                            String day = outputFormat.format(date);

                            double temperature = temperatureArray.getDouble(i);
                            double windSpeed = windSpeedArray.getDouble(i);
                            double humidity = relativeHumidityArray.getDouble(i);

                            if (!dailyData.containsKey(day)) {
                                dailyData.put(day, new ArrayList<>());
                            }
                            dailyData.get(day).add(temperature);
                            dailyData.get(day).add(windSpeed);
                            dailyData.get(day).add(humidity);
                        }

                        for (Map.Entry<String, List<Double>> entry : dailyData.entrySet()) {
                            String day = entry.getKey();
                            List<Double> values = entry.getValue();

                            double medianTemperature = calculateMedian(values.subList(0, values.size() / 3));
                            double medianWindSpeed = calculateMedian(values.subList(values.size() / 3, 2 * values.size() / 3));
                            double medianHumidity = calculateMedian(values.subList(2 * values.size() / 3, values.size()));

                            dailyWeatherList.add(new DailyWeatherData(day, medianTemperature, medianWindSpeed, medianHumidity));
                        }
                        Collections.sort(dailyWeatherList, new Comparator<DailyWeatherData>() {
                            @Override
                            public int compare(DailyWeatherData data1, DailyWeatherData data2) {
                                // Use string comparison for dates in the format "d MMM"
                                return data1.day.compareTo(data2.day);
                            }
                        });
                        for (DailyWeatherData data : dailyWeatherList) {
                            addWeatherDataRow(data.day, data.medianTemperature, data.medianWindSpeed, data.medianHumidity);
                        }
                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Handle error
                });


        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);

        return view;
    }
    private double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
    private void addWeatherDataRow(String time, double temperature, double windSpeed, double humidity) {
        TableRow row = new TableRow(requireContext());

        TextView timeTextView = new TextView(requireContext());
        timeTextView.setText(time);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        timeTextView.setLayoutParams(layoutParams);
        row.addView(timeTextView);

        TextView temperatureTextView = new TextView(requireContext());
        temperatureTextView.setText(String.format("%.2f", temperature));
        temperatureTextView.setLayoutParams(layoutParams);
        row.addView(temperatureTextView);

        TextView windSpeedTextView = new TextView(requireContext());
        windSpeedTextView.setText(String.format("%.2f", windSpeed));
        windSpeedTextView.setLayoutParams(layoutParams);
        row.addView(windSpeedTextView);

        TextView humidityTextView = new TextView(requireContext());
        humidityTextView.setText(String.format("%.2f", humidity));
        humidityTextView.setLayoutParams(layoutParams);
        row.addView(humidityTextView);

        weatherInfoTable.addView(row);
    }
}
