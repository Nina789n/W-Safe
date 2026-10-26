package com.womensafety.shajt3ch;

import android.os.AsyncTask;
import android.util.Log;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordHelper {
    private static final String TAG = "DiscordHelper";
    
    // Discord webhook URL - Replace with your actual webhook URL
    // To get webhook: Discord Server Settings > Integrations > Webhooks > New Webhook
    private static final String DISCORD_WEBHOOK_URL = "https://discord.com/api/webhooks/1448534341989957846/CnNxI3hNkp3c5XQ5lxdS6vykW6IRl1LR4aQEn6zVX14PCB6SbnHQLvjIkBHJltRYICWz";
    
    public interface DiscordCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public static void sendMessageToDiscord(String message, DiscordCallback callback) {
        new SendDiscordMessageTask(callback).execute(message);
    }
    
    private static class SendDiscordMessageTask extends AsyncTask<String, Void, String> {
        private DiscordCallback callback;
        
        public SendDiscordMessageTask(DiscordCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected String doInBackground(String... messages) {
            if (DISCORD_WEBHOOK_URL.equals("YOUR_DISCORD_WEBHOOK_URL_HERE")) {
                return "Webhook URL not configured";
            }
            
            if (messages == null || messages.length == 0) {
                return "No message provided";
            }
            
            String message = messages[0];
            
            try {
                URL url = new URL(DISCORD_WEBHOOK_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                
                // Discord webhook JSON format
                String jsonPayload = "{\"content\":\"" + escapeJson(message) + "\"}";
                
                OutputStream os = connection.getOutputStream();
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == 200 || responseCode == 204) {
                    return "SUCCESS";
                } else {
                    return "Error: " + responseCode;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending to Discord", e);
                return "Error: " + e.getMessage();
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (result.equals("SUCCESS")) {
                    callback.onSuccess();
                } else {
                    callback.onError(result);
                }
            }
        }
        
        private String escapeJson(String text) {
            return text.replace("\\", "\\\\")
                      .replace("\"", "\\\"")
                      .replace("\n", "\\n")
                      .replace("\r", "\\r")
                      .replace("\t", "\\t");
        }
    }
}

