import 'dart:io';
import 'package:flutter/material.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
//https://medium.com/@viveky259259/flutter-firebase-notifications-7954a3ad8111

class FirebaseNotifications {

  FirebaseMessaging _firebaseMessaging;
  static var fcmtoken;

  void setUpFirebase(BuildContext context) {

    _firebaseMessaging = FirebaseMessaging();
    firebaseCloudMessaging_Listeners(context);

    _firebaseMessaging.getToken().then((token) {
       fcmtoken=token;
        print("fcm token: " + fcmtoken);
    });
  }

  void firebaseCloudMessaging_Listeners(BuildContext context) {

    if (Platform.isIOS) iOS_Permission(context);

    _firebaseMessaging.configure(
      onMessage: (Map<String, dynamic> message) async {
        showDialog(
          context: context,
          builder: (context) => AlertDialog(
            content: ListTile(
              title: Text(message['notification']['title']),
              subtitle: Text(message['notification']['body']),
            ),
            actions: <Widget>[
              FlatButton(
                child: Text('Ok'),
                onPressed: () => Navigator.of(context).pop(),
              ),
            ],
          ),
        );
      },
      onResume: (Map<String, dynamic> message) async {
        print('on resume $message');
      },
      onLaunch: (Map<String, dynamic> message) async {
        print('on launch $message');
      },
    );
  }

  void iOS_Permission(BuildContext context) {
    _firebaseMessaging.requestNotificationPermissions(
        IosNotificationSettings(sound: true, badge: true, alert: true));
    _firebaseMessaging.onIosSettingsRegistered
        .listen((IosNotificationSettings settings) {
      print("Settings registered: $settings");
    });
  }
}