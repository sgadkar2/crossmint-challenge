package com.crossmint.megaverse.ServiceImpl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.crossmint.megaverse.Service.CreateMegaverse;

@Service
public class CreateMegaverseImpl implements CreateMegaverse {

    @Value("${megaverse.url}")
    private String megaverseUrl;

    @Value("${megaverse.candidateId}")
    private String candidateId;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RetryTemplate retryTemplate;

    private static final Logger logger = LoggerFactory.getLogger(CreateMegaverseImpl.class);

    @Override
    public void createMegaverse() {

        try{

             //Get the goal from the API
            ResponseEntity<Map<String, String[][]>> responseEntity = restTemplate.exchange(
                megaverseUrl + "map/" + candidateId + "/goal",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String[][]>>() {}
            );

            String[][] goal = responseEntity.getBody().get("goal");

            //Create the megaverse
            for(int i = 0; i < goal.length; i++){
                for(int j = 0; j < goal[i].length; j++){
                    if(goal[i][j].equals("POLYANET")){
                        createPolyanet(i, j);
                    }else if(goal[i][j].contains("COMETH")){
                        createCometh(i, j, goal[i][j].split("_")[0].toLowerCase());
                    }else if(goal[i][j].contains("SOLOON")){
                        createSolon(i, j, goal[i][j].split("_")[0].toLowerCase());
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
        callApi(megaverseUrl + "polyanets", createRequestBody(row, column, null, null));
    }
    
    private void createCometh(int row, int column, String direction) {
        callApi(megaverseUrl + "comeths", createRequestBody(row, column, "direction", direction));
    }
    
    private void createSolon(int row, int column, String color) {
        callApi(megaverseUrl + "soloons", createRequestBody(row, column, "color", color));
    }

    private void callApi(String url, Map<String, Object> requestBody){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try{
            // Wrapped the API call in the retryTemplate execution (which is a utility present in Spring) so that if the API call fails, it will retry.
            // Configuration for retryTemplate is in the configuration package.
            ResponseEntity<String> responseEntity = retryTemplate.execute(context -> {
                return restTemplate.postForEntity(
                    url,
                    request,
                    String.class
                );
            });

        }catch (HttpClientErrorException e) {
            logger.error("Client error while calling {}: {}", url, e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Server error while calling {}: {}", url, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while calling {}: {}", url, e.getMessage());
        }
    }   
}
