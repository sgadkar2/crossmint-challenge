package com.crossmint.megaverse.Service;

import java.util.Map;


public interface MegaverseApiClient {

    String[][] getGoalMap();

    void generateAstralObject(String astralObject, Map<String, Object> requestBody);
}
