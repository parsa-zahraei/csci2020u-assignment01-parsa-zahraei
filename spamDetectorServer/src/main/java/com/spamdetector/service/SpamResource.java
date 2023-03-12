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

    private int truePositives = 0;
    private int trueNegatives = 0;
    private int falsePositives = 0;
    private int falseNegatives = 0;

    SpamResource() throws FileNotFoundException {
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

        testList = trainAndTest();

        getSpamResults();

        APCalc();

        System.out.print("Breakpoint here");


//      TODO: call  this.trainAndTest();

    }

    @GET
    @Path("/json")
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

    private void APCalc(){

        for (TestFile file:  testList){

            if ( file.getSpamProbability() > 0.50){
                if (file.getActualClass() == "spam"){
                    truePositives++;
                }
                else{
                    falsePositives++;
                }
            }
            else{
                if (file.getActualClass() == "ham"){
                    trueNegatives++;
                }
                else{
                    falseNegatives++;
                }
            }
        }


    }

    class Accuracy{
        int TruePositives;
        int TrueNegatives;

        double AccuracyPercent;

        Accuracy(int TruePositives, int TrueNegatives, double AccuracyPercent){
            this.TruePositives = TruePositives;
            this.TrueNegatives = TrueNegatives;
            this.AccuracyPercent = AccuracyPercent;
        }

        public int getTruePositives(){
            return TruePositives;
        }

        public int getTrueNegatives(){
            return TrueNegatives;
        }

        public double getAccuracyPercent(){
            return AccuracyPercent;
        }

    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object

        double accuracyPercent = (double) (truePositives + trueNegatives)/(testList.size());

        Accuracy accuracy = new Accuracy(truePositives, trueNegatives, accuracyPercent);

        String accuracyJson = null;

        try {
            accuracyJson = jsonMapper.writeValueAsString(accuracy);
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }

        Response accuracyResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type", "application/json")
                .entity(accuracyJson)
                .build();

        return accuracyResp;
    }

    class Precision{

        int TruePositives;
        int FalsePositives;

        double PrecisionPercent;

        Precision(int TruePositives, int FalsePositives, double PrecisionPercent){
            this.TruePositives = TruePositives;
            this.FalsePositives = FalsePositives;
            this.PrecisionPercent = PrecisionPercent;
        }

        public int getTruePositives(){
            return  this.TruePositives;
        }

        public int getFalsePositives(){
            return this.FalsePositives;
        }

        public double getPrecisionPercent(){
            return PrecisionPercent;
        }


    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
       //      TODO: return the precision of the detector, return in a Response object

        double precisionPercent = (double) (truePositives)/(falsePositives + truePositives);

        Precision precision = new Precision(truePositives, falsePositives, precisionPercent);

        String precisionJson = null;

        try {
            precisionJson = jsonMapper.writeValueAsString(precision);
        }
        catch (JsonProcessingException e){
            e.printStackTrace();
        }

        Response precisionResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type", "application/json")
                .entity(precisionJson)
                .build();

        return precisionResp;
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