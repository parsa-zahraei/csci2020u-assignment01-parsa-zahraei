package com.spamdetector.service;

import com.spamdetector.domain.TestFile;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();
    ObjectMapper jsonMapper = new ObjectMapper();

    List<TestFile> testList = null;

    SpamResource() throws FileNotFoundException {
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

        testList = trainAndTest();

        getSpamResults();

//      TODO: call  this.trainAndTest();

    }

    @GET
    @Produces("application/json")
    public Response getSpamResults() throws FileNotFoundException {
//       TODO: return the test results list of TestFile, return in a Response object

        String json = null;

        try{
            json = jsonMapper.writeValueAsString(testList);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        Response jsonResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type", "application/json")
                .entity(json)
                .build();

        return jsonResp;

    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object

        return null;
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
       //      TODO: return the precision of the detector, return in a Response object

        return null;
    }

    private List<TestFile> trainAndTest() throws FileNotFoundException {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

//        TODO: load the main directory "data" here from the Resources folder
        URL mainDirectory_url = this.getClass().getClassLoader().getResource("data");
        File mainDirectory = null;
        try{
            mainDirectory = new File(mainDirectory_url.toURI());
        }catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
        return this.detector.trainAndTest(mainDirectory);
    }
}