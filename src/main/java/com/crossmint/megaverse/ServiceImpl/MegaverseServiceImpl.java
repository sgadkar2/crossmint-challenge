package com.crossmint.megaverse.ServiceImpl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.crossmint.megaverse.Service.MegaverseApiClient;
import com.crossmint.megaverse.Service.MegaverseService;

@Service
public class MegaverseServiceImpl implements MegaverseService {

    @Value("${megaverse.candidateId}")
    private String candidateId;

    @Autowired
    private MegaverseApiClient megaverseApiClient;

    private static final Logger logger = LoggerFactory.getLogger(MegaverseServiceImpl.class);

    @Override
    public void createMegaverse() {

        try{

            String[][] goalMap = megaverseApiClient.getGoalMap();

            //Creating the megaverse
            for(int i = 0; i < goalMap.length; i++){
                for(int j = 0; j < goalMap[i].length; j++){
                    if(goalMap[i][j].equals("POLYANET")){
                        createPolyanet(i, j);
                    }else if(goalMap[i][j].contains("COMETH")){
                        createCometh(i, j, goalMap[i][j].split("_")[0].toLowerCase());
                    }else if(goalMap[i][j].contains("SOLOON")){
                        createSolon(i, j, goalMap[i][j].split("_")[0].toLowerCase());
                    }else{
                        continue;
                    }

                }
            }
        }catch(Exception e){
            logger.error("Error occurred while creating megaverse: {}", e.getMessage());
        }
       
    }

    private Map<String, Object> createRequestBody(int row, int column, String extraKey, String extraValue) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("row", row);
        requestBody.put("column", column);
        requestBody.put("candidateId", candidateId);
        if (extraKey != null && extraValue != null) {
            requestBody.put(extraKey, extraValue);
        }
        return requestBody;
    }
    
    private void createPolyanet(int row, int column) {
        megaverseApiClient.generateAstralObject("polyanets", createRequestBody(row, column, null, null));
    }
    
    private void createCometh(int row, int column, String direction) {
        megaverseApiClient.generateAstralObject("comeths", createRequestBody(row, column, "direction", direction));
    }
    
    private void createSolon(int row, int column, String color) {
        megaverseApiClient.generateAstralObject("soloons", createRequestBody(row, column, "color", color));
    }
    
}
