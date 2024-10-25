package com.crossmint.megaverse.ServiceImpl;

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

import com.crossmint.megaverse.Service.MegaverseApiClient;


@Service
public class MegaverseApiClientImpl implements MegaverseApiClient{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RetryTemplate retryTemplate;

    @Value("${megaverse.url}")
    private String megaverseUrl;

    @Value("${megaverse.candidateId}")
    private String candidateId;

    private static final Logger logger = LoggerFactory.getLogger(MegaverseApiClientImpl.class);

    @Override
    public String[][] getGoalMap() {

        try{

            //Getting the goal map from the API
            ResponseEntity<Map<String, String[][]>> responseEntity = restTemplate.exchange(
                megaverseUrl + "map/" + candidateId + "/goal",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, String[][]>>() {}
            );

            if (responseEntity.getBody() != null && responseEntity.getBody().get("goal") != null) {
                return responseEntity.getBody().get("goal");
            } else {
                logger.error("The response body or Goal Map is null.");
            }
        }catch(Exception e){
            logger.error("Unexpected error while getting Goal Map: {}", e.getMessage());
        }

        return new String[0][];
    }   

    @Override
    public void generateAstralObject(String astralObject, Map<String, Object> requestBody){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try{
            // Wrapped the API call in the retryTemplate execution (which is a utility present in Spring) so that if the API call fails, it will retry.
            // Configuration for retryTemplate is in the configuration package.
            retryTemplate.execute(context -> {
                return restTemplate.postForEntity(
                    megaverseUrl + astralObject,
                    request,
                    String.class
                );
            });

        }catch (HttpClientErrorException e) {
            logger.error("Client error while generating {}: {}", astralObject, e.getMessage());
        } catch (HttpServerErrorException e) {
            logger.error("Server error while generating {}: {}", astralObject, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while generating {}: {}", astralObject, e.getMessage());
        }
    }
    
}
