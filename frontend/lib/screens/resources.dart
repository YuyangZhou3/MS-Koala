import 'package:flutter/material.dart';
import 'dart:convert';
import 'package:frontend/util/serverDetails.dart';
import 'package:http/http.dart' as http;
import 'package:frontend/util/authentication.dart';
import 'package:frontend/components/resource.dart';
import 'package:frontend/screens/resourcedetail.dart';

class Resources extends StatefulWidget {
  @override
  _ResourcesState createState() => _ResourcesState();

}

class _ResourcesState extends State<Resources> {

  List<Resource> _resources = List<Resource>();


  @override
  void initState() {
    super.initState();
    getResources();
  }

  /*@override
  void deactivate() {
    super.deactivate();
    print("deactivate");
    var bool = ModalRoute.of(context).isCurrent;
    if (bool) {
      print("back");
      initState();
    }
  }*/

  getResources() async {
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
          'me/resources';
      print(url);
      Map<String, String> headers = {"Authorization": auth};
      print(headers);
      var jsonResponse = null;
      var response = await http.get(url, headers: headers);
      print(response.body);
      if (response.statusCode == 200) {
        jsonResponse = json.decode(response.body);
        if (jsonResponse != null) {
          setState(() {
            for (var doc in jsonResponse) {
              _resources.add(Resource.fromJson(doc));
            }
          });
        }
      } else {}
    }
  }

  @override
  build(context) {
    return Scaffold(
        appBar: PreferredSize(
          preferredSize: Size.fromHeight(60.0),
          child: AppBar(
            leading: BackButton(color: Colors.black),
            centerTitle: true,
            title: Text("Resource",
                style:TextStyle(color: Colors.black
                )
            ),
            backgroundColor: Colors.white,
            brightness: Brightness.light,
//          backgroundColor: Color(0x44000000),
            elevation: 0.5,
          ),
        ),
        body: ListView(
            children: _resources.map((element) => Container(
                decoration: BoxDecoration(
                  border: Border.all(width: 0.8),
                  borderRadius: BorderRadius.circular(12.0),
                ),
                margin:
                const EdgeInsets.symmetric(horizontal: 8.0, vertical: 4.0),
                child: ListTile(
                    leading: Icon(Icons.book,size: 30.0, color: Colors.teal),
                    title: Text(element.name,style: TextStyle(color: Colors.blueGrey.withOpacity(1.0),
                      fontWeight: FontWeight.bold,)),
                    trailing: Icon(Icons.arrow_right),
                    onTap: () {
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) =>
                                  resourcedetail(element))).then((value) { if(value!=null && value){_resources = List<Resource>(); getResources();}});
                    }
                )
            )).toList()
        )

    );

  }
}