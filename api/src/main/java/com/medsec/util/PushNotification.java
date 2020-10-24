package com.medsec.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.medsec.entity.Appointment;
import com.medsec.entity.Resource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PushNotification {
    private JsonObject notificationObject;
    private JsonObject dataObject;

    /*
    To send a notification of new appointment with recipient tokens to the FCM API
     */
    public HashMap<String, String> sendNotification(Object obj) throws IOException {

        System.out.println("obj: " + obj.toString());

        HashMap<String, String> hashMap = new Gson().fromJson(obj.toString(), HashMap.class);
        String uid = hashMap.containsKey("Uid") ? hashMap.get("Uid") : hashMap.get("PT_Id_Fk");

        System.out.println("uid: " + uid);

        Database db = new Database();
        ArrayList<String> recipientTokens = db.getFcmTokenByUid(uid);
        generateNotificationRequest(hashMap);

        FCMHelper fcm = FCMHelper.getInstance();
        HashMap<String, String> responseList = new HashMap<>();

        System.out.println("========================== Response List ==========================");
        for (String fcmToken : recipientTokens) {
            String response = fcm.sendNotifictaionAndData(FCMHelper.TYPE_TO, fcmToken, notificationObject, dataObject);
            System.out.println("fcmToken: " + fcmToken);
            System.out.println("response: " + response + "\n");
            responseList.put(fcmToken, response);
        }

        System.out.println("========================== Response List ==========================");

        return responseList;
    }

    /*
    Convert Java object to JsonObject
     */
    public JsonObject objectToJsonObject(Object object) {
        Gson gson = new Gson();
        JsonObject jsonObject = (JsonObject) gson.toJsonTree(object);           //
        jsonObject.remove("property");
        return jsonObject;

    }

    public void generateNotificationRequest(Map<String, String> objectMap) {

        String className = objectMap.containsKey("Uid") ? "Resource" : "Appointment";
        String body = "You have a new " + className + " !";

        JsonObject notificationObject = new JsonObject();
        notificationObject.addProperty("title", "Medical Secretary");
        notificationObject.addProperty("body", body);

        this.notificationObject = notificationObject;
        System.out.println("notificationObject: "+notificationObject.toString());

        // Notification structure
        JsonObject dataObject = new JsonObject();

        JsonObject objectJson = objectToJsonObject(objectMap);      //
        dataObject.add(className, objectJson);

        System.out.println("dataObject: "+dataObject.toString());

        this.dataObject = dataObject;
    }

}
