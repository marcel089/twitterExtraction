package de.mk.extractTwitterData;

import de.mk.extractTwitterData.API.alchemyAPI;
import de.mk.extractTwitterData.API.dandelionAPI;
import de.mk.extractTwitterData.API.meaningCloudAPI;
import de.mk.extractTwitterData.ReadFile.readFile;
import de.mk.extractTwitterData.ReadFile.readTwitterJSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * @author Marcel
 * @project extractTwitterData
 * @email mk@mkarrasch.de
 * @createdOn 26.11.2016
 * @package de.mk.extractTwitterData
 */


public class twitterExtraction {

    private static final Logger infoLogger = LogManager.getLogger("twitterExtraction");

    public static void main (String[] args) {

        int numberRetweets = 0;
        int numberTweets = 0;
        int skippedTweets = 0;
        Double[] ratingArray = new Double[]{0.0, 0.0, 0.0};
        Double[] rttArray = new Double[]{0.0, 0.0, 0.0};
        int numberOfAPICalls = 0;

        long timeStart;
        long timeEnd;

        try {
            // Start reading inputfile
            readFile inputFile = new readFile("input/file.txt");
            String inputLine = inputFile.nextLine();

            while (inputLine != null) {
                //Check if Line is a JSON Twitter Object
                if (inputLine.startsWith("{\"contributors\":")) {
                    numberTweets += 1;

                    readTwitterJSON twitterJSON = new readTwitterJSON(inputLine);

                    // Only call API if it is not a ReTweet
                    if (!twitterJSON.isRT()) {

                        String tweet = twitterJSON.getTweet();

                        infoLogger.debug("----------- Start -------------------");
                        infoLogger.debug(tweet);

                        //Case 1: URL and # sign included
                        //Case 2: URL not included but # sign included
                        twitterJSON.removeUrl();
                        //Case 3: URL and # Sign not included
                        twitterJSON.removeHastags();

                        tweet = twitterJSON.getTweet();

                        // Extract all Hashtags
                        ArrayList<String> twitterHastags = twitterJSON.extractTweetHastags();
                        // Sort the Array List Entities from A to Z
                        if (twitterHastags.size() > 0) {
                            infoLogger.debug("----------- Hashtags (" + twitterJSON.numberOfHastags() + ") --------------");

                            int i = 1;
                            for (String s : twitterHastags) {
                                infoLogger.debug("Hashtag " + i + " is " + s);
                                i++;
                            }

                            infoLogger.debug("=> Number: " + twitterJSON.numberOfHastags());

                            // Call dandelion API and handle respone
                            infoLogger.debug("----------- Dandelion API -----------");
                            dandelionAPI dandelionAPI = new dandelionAPI();
                            timeStart = System.nanoTime();
                            dandelionAPI.createPOST(tweet);
                            dandelionAPI.executePOST();
                            dandelionAPI.receiveGET();
                            timeEnd = System.nanoTime();
                            double j = dandelionAPI.calculateRating(twitterHastags);
                            ratingArray[0] = ratingArray[0] + j;
                            rttArray[0] = rttArray[0] + (timeEnd - timeStart) / 1000000;
                            infoLogger.debug("=> Number: " + dandelionAPI.getNumberEntities());
                            infoLogger.debug("=> Percentage: " + String.format("%.2f", j) + "%");


                            // Call alcheymy API and handle respone
                            infoLogger.debug("----------- Alchemy API -------------");
                            alchemyAPI alchemyAPI = new alchemyAPI();
                            timeStart = System.nanoTime();
                            alchemyAPI.createPOST(tweet);
                            alchemyAPI.executePOST();
                            alchemyAPI.receiveGET();
                            timeEnd = System.nanoTime();
                            j = alchemyAPI.calculateRating(twitterHastags);
                            ratingArray[1] = ratingArray[1] + j;
                            rttArray[1] = rttArray[1] + (timeEnd - timeStart) / 1000000;
                            infoLogger.debug("=> Number: " + alchemyAPI.getNumberEntities());
                            infoLogger.debug("=> Percentage: " + String.format("%.2f", j) + "%");


                            // Call meaingCloud API and handle respone
                            infoLogger.debug("----------- meaningCloud API -------------");
                            meaningCloudAPI meaningCloudAPI = new meaningCloudAPI();
                            timeStart = System.nanoTime();
                            meaningCloudAPI.createPOST(tweet);
                            meaningCloudAPI.executePOST();
                            meaningCloudAPI.receiveGET();
                            timeEnd = System.nanoTime();
                            infoLogger.debug("API Call Dauer " + (timeEnd - timeStart) / 1000000 + " Millisek.");
                            j = meaningCloudAPI.calculateRating(twitterHastags);
                            ratingArray[2] = ratingArray[2] + j;
                            rttArray[2] = rttArray[2] + (timeEnd - timeStart) / 1000000;
                            infoLogger.debug("=> Number: " + meaningCloudAPI.getNumberEntities());
                            infoLogger.debug("=> Percentage: " + String.format("%.2f", j) + "%");

                            infoLogger.debug("----------- END ---------------------");
                            infoLogger.debug("");

                            numberOfAPICalls += 1;
                            if (numberOfAPICalls == 1000) {
                                infoLogger.warn("Daily API Limit (1000 Requests) reached");
                                break;
                            }
                        } else {
                            infoLogger.warn("No Hashtags - Skip Record: " + tweet);
                            skippedTweets += 1;
                        }

                    } else {
                        numberRetweets += 1;
                    }
                } else {
                    infoLogger.warn("Skip " + inputLine);
                }

                inputLine = inputFile.nextLine();
            }

            infoLogger.info("----------- Rating ---------------------");
            infoLogger.info("Number of Provided tweets  " + numberTweets);
            infoLogger.info("Number of Re-Tweets        " + numberRetweets);
            infoLogger.info("Number of API Calls        " + numberOfAPICalls);
            infoLogger.info("Number of Skipped Records  " + skippedTweets);
            infoLogger.info("Rating Alchemy             " + String.format("%.2f", ratingArray[1] / numberOfAPICalls) + "%");
            infoLogger.info("RTT Alchemy                " + String.format("%.2f", rttArray[1] / numberOfAPICalls) + " Millisekunden");

            infoLogger.info("Rating Dandelion           " + String.format("%.2f", ratingArray[0] / numberOfAPICalls) + "%");
            infoLogger.info("RTT Dandelion              " + String.format("%.2f", rttArray[0] / numberOfAPICalls) + " Millisekunden");

            infoLogger.info("Rating meaningCloud        " + String.format("%.2f", ratingArray[2] / numberOfAPICalls) + "%");
            infoLogger.info("RTT meaningCloud           " + String.format("%.2f", rttArray[2] / numberOfAPICalls) + " Millisekunden");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}