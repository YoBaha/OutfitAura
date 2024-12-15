import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  static const String baseUrl = "http://10.0.2.2:3000/user/";

  static Future<Map<String, dynamic>> login(
      String email, String password) async {
    try {
      final response = await http.post(
        Uri.parse("${baseUrl}login"),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'password': password}),
      );
      print('Login response: ${response.body}');
      return _handleResponse(response);
    } catch (e) {
      print('Login error: $e');
      throw Exception("Failed to login: $e");
    }
  }

  static Future<Map<String, dynamic>> register(
      String email, String password) async {
    try {
      final response = await http.post(
        Uri.parse("${baseUrl}register"),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({'email': email, 'password': password}),
      );
      print('Register response: ${response.body}');
      return _handleResponse(response);
    } catch (e) {
      print('Register error: $e');
      throw Exception("Failed to register: $e");
    }
  }

  static Map<String, dynamic> _handleResponse(http.Response response) {
    if (response.statusCode == 200 || response.statusCode == 201) {
      return jsonDecode(response.body) as Map<String, dynamic>;
    } else {
      return {
        'success': false,
        'message': 'Server error. Status code: ${response.statusCode}',
      };
    }
  }
}
