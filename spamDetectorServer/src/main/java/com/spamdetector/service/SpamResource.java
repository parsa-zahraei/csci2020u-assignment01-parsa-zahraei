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

    //Json Mapper declaration
    ObjectMapper jsonMapper = new ObjectMapper();

    List<TestFile> testList = null;

    private int truePositives = 0;
    private int trueNegatives = 0;
    private int falsePositives = 0;
    private int falseNegatives = 0;


    //Function Runs in background upon startup
    SpamResource() throws FileNotFoundException {
//        TODO: load resources, train and test to improve performance on the endpoint calls
        System.out.print("Training and testing the model, please wait");

        //Populate the testList
        testList = trainAndTest();

        //Evaluate the model based on the testList
        APCalc();

        System.out.print("Breakpoint here");


//      TODO: call  this.trainAndTest();

    }

    @GET
    @Produces("text/html")
    public String rootEndpoint() {
        String res = "Available endpoints are: <br>" +
                "api/spam/json -- return the json file of the TestFiles <br>" +
                "api/spam/accuracy -- returns the accuracy of the model <br>" +
                "api/spam/precision -- returns the precision of the model";
        return res;
    }

    //Return testList as json response object
    @GET
    @Path("/json")
    @Produces("application/json")
    public Response getSpamResults() throws FileNotFoundException {
//       TODO: return the test results list of TestFile, return in a Response object

        String json = null;

        //Serialize testList using the JsonMapper
        try{
            json = jsonMapper.writeValueAsString(testList); //Return a string representation of the json objects
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        Response jsonResp = Response.status(200).header("Access-Control-Allow-Origin", "http://localhost:63342")
                .header("Content-Type", "application/json")
                .entity(json)
                .build();

        return jsonResp;

    }

    //Caculate the number of true positives, true negatives, false positives and false negatives contained within
    //testList
    private void APCalc(){

        for (TestFile file:  testList){

            //If a TestFile object has a spamProbability value greater than 50%, and it's actual class is spam,
            //then it is a true positive. If its actual class is not spam, then it's a false positive
            if ( file.getSpamProbability() > 0.75){
                if (file.getActualClass() == "spam"){
                    truePositives++;
                }
                else{
                    falsePositives++;
                }
            }
            //If a TestFile object has a spamProbability value less than 50%, and it's actual class is ham,
            //then it is a true negative. It its actual class is not ham, then it's a false negative
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

    //Internal class that represents the accuracy of the model
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

    //Returns an accuracy object as a json response object, containing information about the model's accuracy, the number of true positives,
    //and the number of true negatives
    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() {
//      TODO: return the accuracy of the detector, return in a Response object

        //Calculate the percentage accuracy of the model
        double accuracyPercent = (double) (truePositives + trueNegatives)/(testList.size());

        //Create the accuracy object
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

    //Internal class that represents the precision of the model
    class Precision{

        int TruePositives;
        int FalsePositives;

        int FalseNegatives;

        double PrecisionPercent;

        Precision(int TruePositives, int FalsePositives, int FalseNegatives, double PrecisionPercent){
            this.TruePositives = TruePositives;
            this.FalsePositives = FalsePositives;
            this.PrecisionPercent = PrecisionPercent;
            this.FalseNegatives = FalseNegatives;
        }

        //Set getter functions to allow for serialization of the fields

        public int getTruePositives(){
            return  this.TruePositives;
        }

        public int getFalsePositives(){
            return this.FalsePositives;
        }

        public int getFalseNegatives(){return this.FalseNegatives;}

        public double getPrecisionPercent(){
            return PrecisionPercent;
        }

    }

    //Returns a precision object as a json response object, containing information about the model's precision, the number of true positives,
    //and the number of false positives
    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() {
       //      TODO: return the precision of the detector, return in a Response object

        double precisionPercent = (double) (truePositives)/(falsePositives + truePositives);

        Precision precision = new Precision(truePositives, falsePositives, falseNegatives, precisionPercent);

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