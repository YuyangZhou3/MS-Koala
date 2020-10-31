package com.medsec.util;

import com.google.gson.JsonObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class FCMHelper {

    /**
     * Instance
     **/
    private static FCMHelper instance = null;

    /**
     * Google URL to use firebase cloud messenging
     */
    private static final String URL_SEND = "https://fcm.googleapis.com/fcm/send";

    /**
     * STATIC TYPES
     */

    public static final String TYPE_TO = "to";  // Use for single devices, device groups and topics
    public static final String TYPE_CONDITION = "condition"; // Use for Conditions

    /**
     * Your SECRET server key
     */
    private static final String FCM_SERVER_KEY = "AAAAxffcDQ0:APA91bEcxTizVU3LuIHb-zaQBooL5IDUsCLJSg--Cgg2Ipfhj74Qxw4M2eF5PIJn29Kdw7-Vu46GLijF2XwxNn1fQ8e8BRBcvQX30sH0a3WeegYOYXFz3HGbZelvQ7UtwGXZ2cYYCj2e";

    public static FCMHelper getInstance() {
        if (instance == null) instance = new FCMHelper();
        return instance;
    }

    private FCMHelper() {}

    /**
     * Send notification
     * @param type
     * @param typeParameter
     * @param notificationObject
     * @return
     * @throws IOException
     */
    public String sendNotification(String type, String typeParameter, JsonObject notificationObject) throws IOException {
        return sendNotifictaionAndData(type, typeParameter, notificationObject, null);
    }

    /**
     * Send data
     * @param type
     * @param typeParameter
     * @param dataObject
     * @return
     * @throws IOException
     */
    public String sendData(String type, String typeParameter, JsonObject dataObject) throws IOException {
        return sendNotifictaionAndData(type, typeParameter, null, dataObject);
    }

    /**
     * Send notification and data
     * @param type
     * @param typeParameter
     * @param notificationObject
     * @param dataObject
     * @return
     * @throws IOException
     */
    public String sendNotifictaionAndData(String type, String typeParameter, JsonObject notificationObject, JsonObject dataObject) throws IOException {
        String result = null;
        if (type.equals(TYPE_TO) || type.equals(TYPE_CONDITION)) {
            JsonObject sendObject = new JsonObject();
            sendObject.addProperty(type, typeParameter);
            result = sendFcmMessage(sendObject, notificationObject, dataObject);
        }
        return result;
    }

    /**
     * Send data to a topic
     * @param topic
     * @param dataObject
     * @return
     * @throws IOException
     */
    public String sendTopicData(String topic, JsonObject dataObject) throws IOException{
        return sendData(TYPE_TO, "/topics/" + topic, dataObject);
    }

    /**
     * Send notification to a topic
     * @param topic
     * @param notificationObject
     * @return
     * @throws IOException
     */
    public String sendTopicNotification(String topic, JsonObject notificationObject) throws IOException{
        return sendNotification(TYPE_TO, "/topics/" + topic, notificationObject);
    }

    /**
     * Send notification and data to a topic
     * @param topic
     * @param notificationObject
     * @param dataObject
     * @return
     * @throws IOException
     */
    public String sendTopicNotificationAndData(String topic, JsonObject notificationObject, JsonObject dataObject) throws IOException{
        return sendNotifictaionAndData(TYPE_TO, "/topics/" + topic, notificationObject, dataObject);
    }

    /**
     * Send a Firebase Cloud Message
     * @param sendObject - Contains to or condition
     * @param notificationObject - Notification Data
     * @param dataObject - Data
     * @return
     * @throws IOException
     */
    private String sendFcmMessage(JsonObject sendObject, JsonObject notificationObject, JsonObject dataObject) throws IOException {
        HttpPost httpPost = new HttpPost(URL_SEND);

        // Header 
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "key=" + FCM_SERVER_KEY);

        if (notificationObject != null) sendObject.add("notification", notificationObject);
        if (dataObject != null) sendObject.add("data", dataObject);

        String data = sendObject.toString();

        StringEntity entity = new StringEntity(data);

        // JSON-Object
        httpPost.setEntity(entity);

        HttpClient httpClient = HttpClientBuilder.create().build();

        BasicResponseHandler responseHandler = new BasicResponseHandler();
        String response = (String) httpClient.execute(httpPost, responseHandler);

        System.out.println("request: " + data);
        return response;
    }

}
