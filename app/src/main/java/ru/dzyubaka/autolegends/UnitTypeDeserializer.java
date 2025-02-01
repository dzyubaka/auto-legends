package ru.dzyubaka.autolegends;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UnitTypeDeserializer implements JsonDeserializer<List<UnitType>> {

    @Override
    public List<UnitType> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<UnitType> unitTypes = new ArrayList<>();
        JsonArray jsonArray = json.getAsJsonArray();

        for (JsonElement element : jsonArray) {
            String unitName = element.getAsString();
            unitTypes.add(UnitType.valueOf(unitName));
        }

        return unitTypes;
    }
}