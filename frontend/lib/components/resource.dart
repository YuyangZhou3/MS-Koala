class Resource {
  final String id;
  final String name;
  final String content;
  final String type;

  Resource({
    this.id,
    this.name,
    this.content,
    this.type});

  factory Resource.fromJson(Map<String, dynamic> json) {
    return Resource(
        id: json['id'],
        name: json['name'],
        content: json['content'],
        type: json['type']);
  }
}