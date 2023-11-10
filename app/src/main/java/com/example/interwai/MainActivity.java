package com.example.interwai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ChatGPTClient chatGPTClient;
    private EditText message;
    private TextView res;
    private ProgressDialog loadingDialog;
    boolean double_check;
    private Handler handler;
    String content = "";
    String check_1 = "";
    String check_2 = "";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showLoadingDialog();
        // Simulate some background task

        Button check = findViewById(R.id.check);
        Button send = findViewById(R.id.send);

        Intent intent = getIntent();
        String questionFromBoss ="";
        String critFromBoss="";
        if (intent != null) {
            if (intent.hasExtra("QUESTION")) {
                questionFromBoss = intent.getStringExtra("QUESTION");
                // Use message1 as needed
            }


            if (intent.hasExtra("CRITERIA")) {
                critFromBoss = intent.getStringExtra("CRITERIA");
                // Use message2 as needed
            }

        }

        chatGPTClient = new ChatGPTClient();
        message = findViewById(R.id.message);
        res = findViewById(R.id.response);
        TextView marks = findViewById(R.id.marks);

        String final_ques = " Question: "+questionFromBoss +
                ". Ask this question only without any other unecessary word";


        // Create a new thread to perform the API call
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Make the API call in the background thread
                chatGPTClient.startMessage(final_ques);


                boolean assistantExists = false;
                while(!assistantExists){
                    check_1 = chatGPTClient.getMessage();
                    try {
                        JSONObject jsonData = new JSONObject(check_1);
                        JSONArray dataArray = jsonData.getJSONArray("data");

                        // Check if "assistant" role exists in the data array

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject messageObject = dataArray.getJSONObject(i);
                            if (messageObject.optString("role", "").equals("assistant")) {
                                assistantExists = true;
                                String questionContent = null;

                                for (int j = 0; j < dataArray.length(); j++) {
                                    JSONObject messageObject1 = dataArray.getJSONObject(i);

                                    if ("assistant".equals(messageObject1.optString("role", ""))) {
                                        // Found the message with role "assistant"
                                        JSONArray contentArray = messageObject1.getJSONArray("content");

                                        if (contentArray.length() > 0) {
                                            JSONObject textObject = contentArray.getJSONObject(0).getJSONObject("text");
                                            content = textObject.optString("value", "");
                                            break;  // Stop looping once the content is found
                                        }
                                    }
                                }

                                if (questionContent != null) {
                                    System.out.println("Extracted content: " + questionContent);
                                }

                                break;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }







                // Update the UI with the response on the main thread
                String finalResponse = content;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        res.setText(finalResponse);
                        dismissLoadingDialog();

                    }
                });
            }
        }).start();

        // Set an onClickListener for the button
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This code will be executed when the button is clicked


                // Create a new thread to perform the API call
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Make the API call in the background thread

                        chatGPTClient.getMessage();

                        // Update the UI with the response on the main thread

                    }
                }).start();
            }
        });

        String finalCritFromBoss = critFromBoss;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // This code will be executed when the button is clicked
                showLoadingDialog();
                String userMessage = String.valueOf(message.getText());

                // Create a new thread to perform the API call
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Make the API call in the background thread


                        chatGPTClient.continueSendMessage(userMessage, finalCritFromBoss);


                        boolean assistantExists = false;
                        while(!assistantExists){
                            check_2 = chatGPTClient.getMessage();
                            if(!check_2.equalsIgnoreCase(check_1)) {
                                try {
                                    JSONObject jsonData = new JSONObject(check_2);
                                    JSONArray dataArray = jsonData.getJSONArray("data");

                                    // Check if "assistant" role exists in the data array

                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject messageObject = dataArray.getJSONObject(i);
                                        if (messageObject.optString("role", "").equals("assistant")) {
                                            assistantExists = true;


                                            for (int j = 0; j < dataArray.length(); j++) {
                                                JSONObject messageObject1 = dataArray.getJSONObject(i);

                                                if ("assistant".equals(messageObject1.optString("role", ""))) {
                                                    // Found the message with role "assistant"
                                                    JSONArray contentArray = messageObject1.getJSONArray("content");

                                                    if (contentArray.length() > 0) {
                                                        JSONObject textObject = contentArray.getJSONObject(0).getJSONObject("text");
                                                        if(!textObject.optString("value", "").equals(content)) {
                                                            content = textObject.optString("value", "");
                                                            break;

                                                        }
                                                        else
                                                            assistantExists = false;  // Stop looping once the content is found
                                                    }
                                                }
                                            }


                                            break;
                                        }
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        // Update the UI with the response on the main thread
                        String finalResponse = content;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                marks.setText("Mark: " + finalResponse);
                                dismissLoadingDialog();

                            }
                        });
                    }
                }).start();
            }
        });
    }

    private void showLoadingDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading...");
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }

    private void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }






}
