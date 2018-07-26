package com.udacity.sandwichclub.utils;

import android.util.JsonReader;

import com.udacity.sandwichclub.exception.AppException;
import com.udacity.sandwichclub.exception.code.AppErrorCode;
import com.udacity.sandwichclub.model.Sandwich;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static final String JSON_KEY_NAME = "name";
    public static final String JSON_KEY_MAIN_NAME = "mainName";
    public static final String JSON_KEY_ALSO_KNOWN_AS = "alsoKnownAs";
    public static final String JSON_KEY_PLACE_OF_ORIGIN = "placeOfOrigin";
    public static final String JSON_KEY_DESCRIPTION = "description";
    public static final String JSON_KEY_IMAGE = "image";
    public static final String JSON_KEY_INGREDIENTS = "ingredients";


    public static Sandwich parseSandwichJson(String json) {
        Sandwich sandwich = new Sandwich();
        JsonReader jsonReader = new JsonReader(new StringReader(json));

        try {
            Field field;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String jsonKey = jsonReader.nextName();
                switch (jsonKey) {
                    case JSON_KEY_NAME:
                        jsonReader.beginObject();
                        while (jsonReader.hasNext()) {
                            String nextName = jsonReader.nextName();
                            field = getSandwichClassField(nextName);
                            switch (nextName) {
                                case JSON_KEY_MAIN_NAME:
                                    field.set(sandwich, jsonReader.nextString());
                                    break;
                                case JSON_KEY_ALSO_KNOWN_AS:
                                    field.set(sandwich, getStringListFromJsonReader(jsonReader));
                                    break;
                                default:
                                    throw new AppException(AppErrorCode.ERROR_PARSING_JSON_DATA);
                            }
                        }
                        jsonReader.endObject();
                        break;
                    case JSON_KEY_PLACE_OF_ORIGIN:
                    case JSON_KEY_DESCRIPTION:
                    case JSON_KEY_IMAGE:
                        field = getSandwichClassField(jsonKey);
                        field.set(sandwich, jsonReader.nextString());
                        break;
                    case JSON_KEY_INGREDIENTS:
                        field = getSandwichClassField(jsonKey);
                        field.set(sandwich, getStringListFromJsonReader(jsonReader));
                        break;
                    default:
                        throw new AppException(AppErrorCode.ERROR_PARSING_JSON_DATA);
                }
            }
            jsonReader.endObject();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new AppException(AppErrorCode.ERROR_PARSING_JSON_DATA);
        } catch (IOException e) {
            e.printStackTrace();
            throw new AppException(AppErrorCode.ERROR_PARSING_JSON_DATA);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new AppException(AppErrorCode.ERROR_PARSING_JSON_DATA);
        }

        return sandwich;
    }

    private static Field getSandwichClassField(String fieldName) throws NoSuchFieldException {
        Class<Sandwich> sandwichClass = Sandwich.class;
        Field field = sandwichClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    private static List<String> getStringListFromJsonReader(JsonReader reader) throws IOException {
        List<String> result = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            result.add(reader.nextString());
        }
        reader.endArray();
        return result;
    }
}
