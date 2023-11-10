package com.example.interwai;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ChatGPTClient{
    private static final String API_URL = "https://api.openai.com/v1/threads/thread_MyVwkfMX4YQgB5nnmsv2ayyg/runs";
    private static final String API_KEY = "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";
    String assis = "asst_SBYV70RRKoeuiyg3bgmKktHM";
    String thread_id;
    String run_id;

    public String startMessage(String conversation) {
        try {
            URL url = new URL("https://api.openai.com/v1/threads");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("OpenAI-Beta", "assistants=v1");
            connection.setDoOutput(true);

            // Construct the request JSON
            String requestData = "{ \"messages\": [ { \"role\": \"user\", \"content\": \"Ask the question in the instrcution given without any other unnecessary word\"}]}";


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the API
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                thread_id = response.toString();
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());

                    // Extract the value associated with the "id" field
                    thread_id = jsonObject.getString("id");
                    System.out.println("Thread ID: " + thread_id);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            URL url = new URL("https://api.openai.com/v1/threads/"+thread_id+"/runs");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("OpenAI-Beta", "assistants=v1");
            connection.setDoOutput(true);

            // Construct the request JSON
            String requestData = "{\n" +
                    "  \"assistant_id\": \"" + assis + "\",\n" +
                    "  \"model\": \"gpt-3.5-turbo\",\n" +
                    "  \"instructions\": \""+conversation+"\"\n" +
                    "}";


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the API
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                try {
                    JSONObject jsonObject = new JSONObject(response.toString());

                    // Extract the value associated with the "id" field
                    run_id = jsonObject.getString("id");
                    System.out.println("RUN ID: " + run_id);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String continueSendMessage(String message,String criteria){
        try {
            URL url = new URL("https://api.openai.com/v1/threads/" + thread_id + "/messages");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("OpenAI-Beta", "assistants=v1");
            connection.setDoOutput(true);

            // Construct the request JSON

            String requestData = "{ \"role\": \"user\", \"content\": \""+message+"\" }";


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the API
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                System.out.println("Thread ID: " + thread_id);
                System.out.println(response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            URL url = new URL("https://api.openai.com/v1/threads/"+thread_id+"/runs");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("OpenAI-Beta", "assistants=v1");
            connection.setDoOutput(true);

            // Construct the request JSON
            String requestData = "{\n" +
                    "  \"assistant_id\": \"" + assis + "\",\n" +
                    "  \"model\": \"gpt-3.5-turbo\",\n" +
                    "  \"instructions\": \"Evaluate the answer based on :"+criteria+". If answer consist of criteria then give 100%. Reply only the mark without any other unnecessary word\"\n" +
                    "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response from the API
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                System.out.println("RUN ID: " + run_id);
                System.out.println(response.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMessage(){
        try {
            URL url = new URL("https://api.openai.com/v1/threads/"+thread_id+"/messages");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("OpenAI-Beta", "assistants=v1");
            // Get the response from the API
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
                // Process the response as needed
                return response.toString();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
