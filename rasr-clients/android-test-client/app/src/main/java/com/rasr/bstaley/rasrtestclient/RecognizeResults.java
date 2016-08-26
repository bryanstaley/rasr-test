package com.rasr.bstaley.rasrtestclient;

/**
 * Created by bstaley on 8/26/2016.
 */


public class RecognizeResults {
    public String response;
    public Integer htmlCode;
    public Integer bytesProcessed;

    public RecognizeResults(String response, Integer htmlCode, Integer bytesProcessed) {
        this.response = response;
        this.htmlCode = htmlCode;
        this.bytesProcessed = bytesProcessed;
    }
}