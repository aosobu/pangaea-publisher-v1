package com.pangaea.asynckafkalib.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Adewale S Osobu
 * Class is designed to extract specified value using given key
 * from json payload received from producer
 */
public class DynamicJsonParser {

    private static volatile String result = "";

    public static String parseJsonObject(JSONObject json, String key){
        String keyValue = null;
        try {
            keyValue = json.get(key).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return keyValue;
    }

    public static String getKey(JSONObject payload, String key){

        result = "";

        try {
            Iterator<?> keys;
            String nextKeys;

            boolean exists = payload.has(key);

            if(!exists){
                keys = payload.keys();

                while(keys.hasNext()){
                    nextKeys = (String) keys.next();

                    try{
                        if(payload.get(nextKeys) instanceof  JSONObject){
                            if(exists == false){
                                getKey(payload, key);
                            }
                        }else if(payload.get(nextKeys) instanceof JSONArray){
                            JSONArray jsonArray = payload.getJSONArray(nextKeys);

                            for (int i=0; i<jsonArray.length(); i++){
                                String jsonArrayToString = jsonArray.get(i).toString();
                                JSONObject innerJSON = new JSONObject(jsonArrayToString);
                              if(exists == false){
                                  if (!result.isEmpty()) {
                                      break;
                                  }
                                  getKey(innerJSON, key);
                              }
                            }
                        }

                    }catch(Exception e){

                    }
                }

            }else{
                if(!parseJsonObject(payload, key).isEmpty()) {
                    result = result.concat(parseJsonObject(payload, key));
                    if(!result.isEmpty())
                        return result;
                }
            }

        } catch (Exception e) {
            return "Unavailable Key {} " + e.getMessage();
        }

        return result;
    }

    public static String asJsonString(HashMap<String, Object> map) {
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
