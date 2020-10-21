import 'dart:convert';

import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:frontend/components/pdffile.dart';
import 'package:frontend/components/resource.dart';
import 'package:frontend/util/authentication.dart';
import 'package:frontend/util/serverDetails.dart';
import 'package:frontend/util/url_launch_wrapper.dart';
import 'package:http/http.dart' as http;
import 'package:url_launcher/url_launcher.dart';
import 'package:path_provider/path_provider.dart';
import 'dart:async';
import 'dart:io';
import 'package:frontend/main.dart';

import 'appointmentfile.dart';

class resourcedetail extends StatefulWidget {

  final Resource _resource;
  const resourcedetail(this._resource);

  @override
  _resourcedetailState createState() => _resourcedetailState(_resource);
}

class _resourcedetailState extends State<resourcedetail> {
  Pdffile _file;
  var _resourceState;
  var pdfTitle = null;
  var pdfLink = null;
  String pathPDF = "";

  _resourcedetailState(this._resourceState);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      //extendBodyBehindAppBaorder: true,
      appBar: PreferredSize(
          preferredSize: Size.fromHeight(60.0),
          child: AppBar(
            leading: BackButton(color: Colors.black),
            centerTitle: true,
            title: Text("Resource Details", style: TextStyle(color: Colors.black)),
            backgroundColor: Colors.white,
            brightness: Brightness.light,
//            backgroundColor: Colors.transparent,
            elevation: 0.5,
          )
      ),
      body:   new Builder(
          builder:(BuildContext context){
            return new Container(
                margin: const EdgeInsets.all(8.0),
                decoration: new BoxDecoration(
                  color: Color.fromARGB(255, 196, 218, 234),
                  borderRadius: BorderRadius.all(Radius.circular(4.0)),),
                child: _buildCard(_resourceState.name, _resourceState.type, _resourceState.content)
            );
          }),
    );
  }

  Widget _buildCard(String name,String type, String url) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Table(
        children: [
          TableRow(
          children: [
            ListTile(
                title: Text(name,
                style: TextStyle(fontSize: 20.0, fontFamily: "Arial", fontWeight: FontWeight.bold)),
          ),]
          ),
          TableRow(
            children: [
            ListTile(
                title: Text("Type: " + type.toString(),
                    style: TextStyle(fontSize: 17.5, fontFamily: "Arial"))
            ),
            ]
          ), TableRow(
              children: [
                ListTile(
                    title: Text(url.toString(),
                        style: TextStyle(fontSize: 17.5, fontFamily: "Arial")),
                    trailing: Icon(Icons.public),
                    onTap: () {
                      if(type == "website") launchURL(url);
                      else if(type == "file") {

                          if(pdfTitle == null){
                            pdfTitle = _resourceState.name;
                            pdfLink = _resourceState.content;
                            print("get the file");
                            getPdfLink().then((f) {
                              setState(() {
                                if (f != null) {
                                  pathPDF = f.path;
                                  print("pathPDF: "+pathPDF);
                                  Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                      builder: (context) =>
                                      appointmentfile(pathPDF)));
                                }
                                else {
                                  pathPDF = "";
                                  print("No file found");
                                  Text("No avaliable pdf file at present", style: TextStyle(fontSize: 20.0, fontFamily: "Arial",color:Colors.grey, height:1.5 ),);
                                }
                              });
                            });
                          }else{
                            Navigator.push(
                                context,
                                MaterialPageRoute(
                                    builder: (context) =>
                                        appointmentfile(pathPDF)));
                          }
                      };
                    }
                ),
              ]
          ) ,TableRow(
              children: [
                ListTile(
                    trailing: Text("Delete the Resource",
                        style: TextStyle(fontSize: 15.5, fontFamily: "Arial",color:Colors.deepOrange)),
                    onTap: () {
                      deleteResource();
                    }
                ),
              ]
          ) ,
        ],
      )
    );
  }


  Future<File> getPdfLink() async{
    print('getPdf');
    String currentToken = await Authentication.getCurrentToken();
    print(currentToken);
    if (currentToken == null) {
      print('bouncing');
      Authentication.bounceUser(context);
    }else {
      var fileId = _resourceState.id.toString();;
      String auth = "Bearer " + currentToken;
      String url = ServerDetails.ip +
          ':' +
          ServerDetails.port +
          ServerDetails.api +
          'resources/link/' +
          fileId;
      print(url);
//      pdfLink = pdfLink.replaceAll("\\", "/");
      Map<String, String> headers = {"Authorization": auth};

      final filepath = pdfLink.substring(pdfLink.indexOf("/")+1, pdfLink.lastIndexOf("/"));
      print(filepath);
      final filename = pdfLink.substring(pdfLink.lastIndexOf("/") + 1);
      print(filename);
      var request = await HttpClient().getUrl(Uri.parse (url));
      request.headers.add("Authorization", auth);
      var response = await request.close();
      print(response.statusCode);
      if (response.statusCode == 200) {
        var bytes = await consolidateHttpClientResponseBytes(response);
        String dir = (await getApplicationDocumentsDirectory()).path;
        var fpath = Directory('$dir/$filepath');
        try{
          bool exists = await fpath.exists();
          if (!exists){
            await fpath.parent.create();
            await fpath.create();
          }
        }catch(e){
          print(e);
        }

        File file = new File('$dir/$filepath/$filename');

        if (file != null) {
          await file.writeAsBytes(bytes);
          return file;
        }
      } else {
        setState(() {
          _file = null;
          pdfTitle = null;
          pdfLink = null;
          print(response.statusCode);
          return null;
        });
      }
    }
  }

  deleteResource() async {
    String currentToken = await Authentication.getCurrentToken();
    print(currentToken);
    if (currentToken == null) {
      print('bouncing');
      Authentication.bounceUser(context);
    } else {
      String auth = "Bearer " + currentToken;
      String url = ServerDetails.ip +
          ':' +
          ServerDetails.port +
          ServerDetails.api +
          'resources/'+
          _resourceState.id.toString();
      print(url);
      Map<String, String> headers = {"Authorization": auth};
      print(headers);
      var jsonResponse = null;
      var response = await http.delete(url, headers: headers);
      print(response.body);
      if (response.statusCode == 200) {
        print("200" + response.body);
        jsonResponse = json.decode(response.body);
        if (jsonResponse != null) {
          setState(() {
            Navigator.of(context).pop(true);
          });
        }
      } else {
        print(response.body);
      }
    }
  }
}