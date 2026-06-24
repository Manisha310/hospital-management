package com.server;

import java.util.*;

public class JsonUtil {

  public static String toJson(Map<String, Object> map) {
    StringBuilder sb = new StringBuilder("{");
    boolean first = true;
    for (Map.Entry<String, Object> e : map.entrySet()) {
      if (!first) sb.append(",");
      first = false;
      sb.append("\"").append(escape(e.getKey())).append("\":");
      appendValue(sb, e.getValue());
    }
    sb.append("}");
    return sb.toString();
  }

  public static String toJson(List<Map<String, Object>> list) {
    StringBuilder sb = new StringBuilder("[");
    boolean first = true;
    for (Map<String, Object> m : list) {
      if (!first) sb.append(",");
      first = false;
      sb.append(toJson(m));
    }
    sb.append("]");
    return sb.toString();
  }

  private static void appendValue(StringBuilder sb, Object v) {
    if (v == null) sb.append("null");
    else if (v instanceof String) sb.append("\"").append(escape((String) v)).append("\"");
    else if (v instanceof Number || v instanceof Boolean) sb.append(v);
    else sb.append("\"").append(escape(v.toString())).append("\"");
  }

  public static String escape(String s) {
    return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
  }

  public static Map<String, String> parseJson(String json) {
    Map<String, String> map = new LinkedHashMap<>();
    json = json.trim();
    if (json.startsWith("{") && json.endsWith("}")) {
      json = json.substring(1, json.length() - 1).trim();
      int i = 0;
      while (i < json.length()) {
        // skip whitespace
        while (i < json.length() && json.charAt(i) <= ' ') i++;
        if (i >= json.length()) break;
        // read key
        if (json.charAt(i) != '"') break;
        int keyStart = i + 1;
        int keyEnd = findStringEnd(json, keyStart);
        if (keyEnd < 0) break;
        String key = json.substring(keyStart, keyEnd);
        i = keyEnd + 1;
        while (i < json.length() && json.charAt(i) <= ' ') i++;
        if (i >= json.length() || json.charAt(i) != ':') break;
        i++;
        while (i < json.length() && json.charAt(i) <= ' ') i++;
        // read value
        if (i < json.length() && json.charAt(i) == '"') {
          int valStart = i + 1;
          int valEnd = findStringEnd(json, valStart);
          if (valEnd < 0) break;
          map.put(key, json.substring(valStart, valEnd));
          i = valEnd + 1;
        } else {
          int valEnd = i;
          while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}') valEnd++;
          map.put(key, json.substring(i, valEnd).trim());
          i = valEnd;
        }
        while (i < json.length() && (json.charAt(i) == ',' || json.charAt(i) <= ' ')) i++;
      }
    }
    return map;
  }

  private static int findStringEnd(String s, int start) {
    for (int i = start; i < s.length(); i++) {
      if (s.charAt(i) == '\\') i++;
      else if (s.charAt(i) == '"') return i;
    }
    return -1;
  }
}
