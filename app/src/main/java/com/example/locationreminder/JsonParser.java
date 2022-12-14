package com.example.locationreminder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    /* JSON. parse() is a crucial method for converting JSON data in string
     form into Javascript objects.
    * */
    private HashMap<String,String> parseJsonObject(JSONObject object){
        HashMap<String,String> dataList=new HashMap<>();
        try{
            String name =object.getString("name");
            String laitude= object.getJSONObject("geometry").getJSONObject("location").getString("lat");
            String longitude= object.getJSONObject("geometry").getJSONObject("location").getString("lng");
            dataList.put("name",name);
            dataList.put("lat",laitude);
            dataList.put("lng",longitude);
        }
        catch (JSONException e){

        }
        return dataList;
    }
    private List<HashMap<String,String>> parseJsonArray(JSONArray jsonArray){
        List<HashMap<String,String>> datalist= new ArrayList<>();
        for(int i=0;i<jsonArray.length();i++){
            try {
                HashMap<String,String> data=parseJsonObject((JSONObject) jsonArray.get(i));
           datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object){
        JSONArray jsonArray=null;
        try {
            jsonArray=object.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return parseJsonArray(jsonArray);
    }
}
