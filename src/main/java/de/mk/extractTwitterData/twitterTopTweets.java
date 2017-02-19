package de.mk.extractTwitterData;

import de.mk.extractTwitterData.ReadFile.readFile;
import de.mk.extractTwitterData.ReadFile.readTwitterJSON;
import de.mk.extractTwitterData.Util.calculateTopHashtags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Marcel
 * @project twitterTopTweets
 * @email mk@mkarrasch.de
 * @createdOn 07.02.2017
 * @package de.mk.extractTwitterData
 */


public class twitterTopTweets {

    // Different log4j2 logger
    private static final Logger infoLogger = LogManager.getLogger("twitterTopTweets");

    // Start programm
    public static void main (String[] args) {
        int numberRetweets = 0;
        int numberTweets = 0;
        int skippedTweets = 0;
        int numberAnalzedTweets;

        calculateTopHashtags top = new calculateTopHashtags();

        int count = 0;

        infoLogger.info("Start");
        try {
            // Start reading inputfile
            readFile inputFile = new readFile("E:/Twitter_Corpus/twitter_travel_complete/twitter_travel_complete.txt");
            String inputLine = inputFile.nextLine();

            while (inputLine != null) {
                //Check if Input Line is JSON Twitter Object
                if (inputLine.startsWith("{\"contributors\":")) {
                    numberTweets += 1;

                    readTwitterJSON twitterJSON = new readTwitterJSON(inputLine);
                    // Only call API if it is not a ReTweet
                    if (!twitterJSON.isRT()) {
                        // Extract all Hashtags from one tweet
                        ArrayList<String> twitterHastags = twitterJSON.extractTweetHastags();
                        // Sort the Array List Entities from A to Z
                        if (twitterHastags.size() > 0) {
                            // Add all hashtags of one tweet to calculateTopHashtags()
                            for (String s : twitterHastags) {
                                top.add(s.toLowerCase());
                            }
                        } else {
                            // +1 if no Hashtag was found in Tweet
                            skippedTweets += 1;
                        }

                    } else {
                        // +1 Number of retweets
                        numberRetweets += 1;
                    }
                }

                // Read next line
                inputLine = inputFile.nextLine();
                count++;
            }

            /*
            Status: All input lines have been processed. All Hashtags have been identified and are stored in an
            huge Hashmaps.
                => Task completed.
             */
            System.out.println("!Task 1 finished");

            //Receive Top 10 Hashtags from Task 1 huge Hashmap
            Map<String, Integer> topHashtagsMap;
            topHashtagsMap = top.topHashtags(10);

            //One new Hashmap for each one top 10 Hashtag
            ArrayList<String> list0 = new ArrayList<>();
            ArrayList<String> list1 = new ArrayList<>();
            ArrayList<String> list2 = new ArrayList<>();
            ArrayList<String> list3 = new ArrayList<>();
            ArrayList<String> list4 = new ArrayList<>();
            ArrayList<String> list5 = new ArrayList<>();
            ArrayList<String> list6 = new ArrayList<>();
            ArrayList<String> list7 = new ArrayList<>();
            ArrayList<String> list8 = new ArrayList<>();
            ArrayList<String> list9 = new ArrayList<>();


            //Read File Again
            // Start reading inputfile
            inputFile = new readFile("E:/Twitter_Corpus/twitter_travel_complete/twitter_travel_complete.txt");
            inputLine = inputFile.nextLine();

            count = 0;

            while (inputLine != null) {
                //Check if Input Line is JSON Twitter Object
                if (inputLine.startsWith("{\"contributors\":")) {

                    readTwitterJSON twitterJSON = new readTwitterJSON(inputLine);

                    // Onle handle if it is not a RT and Tweet contains some Hashtags
                    if (!twitterJSON.isRT() && twitterJSON.numberOfHastags() > 0) {
                        ArrayList<String> twitterHastags = twitterJSON.extractTweetHastags();

                        int i = 0;
                        // For every Hashtag in tweet check if this tweet contains a top 10 hashtag. If so ssave the
                        // other Hashtags in a Hashmap for each Top 10 Tweet
                        for (Map.Entry<String, Integer> entry : topHashtagsMap.entrySet()) {
                            for (String s : twitterHastags) {
                                if (s.toLowerCase().equals(entry.getKey())) {
                                    for (String j : twitterHastags) {
                                        if (!j.toLowerCase().equals(entry.getKey())) {
                                            switch (i) {
                                                case 0:
                                                    list0.add(j.toLowerCase());
                                                    break;
                                                case 1:
                                                    list1.add(j.toLowerCase());
                                                    break;
                                                case 2:
                                                    list2.add(j.toLowerCase());
                                                    break;
                                                case 3:
                                                    list3.add(j.toLowerCase());
                                                    break;
                                                case 4:
                                                    list4.add(j.toLowerCase());
                                                    break;
                                                case 5:
                                                    list5.add(j.toLowerCase());
                                                    break;
                                                case 6:
                                                    list6.add(j.toLowerCase());
                                                    break;
                                                case 7:
                                                    list7.add(j.toLowerCase());
                                                    break;
                                                case 8:
                                                    list8.add(j.toLowerCase());
                                                    break;
                                                case 9:
                                                    list9.add(j.toLowerCase());
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                            i++;
                        }
                    }
                }
                inputLine = inputFile.nextLine();
                count++;
            }
            /*
            Status: Alle Input File have been processed again. Now all Hashtags are saved which has been mentioned
            with one of the Top 10 Hashtags.
            E.g. Hashtag #IoT has been mentions XX times with Hashtags #IBM
                => Task completed.
             */
            System.out.println("!Task 2 finished");

            infoLogger.info("----------- Rating ---------------------");
            infoLogger.info("Number of Provided Tweets  " + numberTweets);

            numberAnalzedTweets = numberTweets - numberRetweets - skippedTweets;
            infoLogger.info("Number of Analyzed Tweets  " + numberAnalzedTweets);
            infoLogger.info("Number of Re-Tweets        " + numberRetweets);
            infoLogger.info("Number of Skipped Records  " + skippedTweets);

            int i = 0;

            // Print all connected Hashtags
            // E.g. Hashtag #IoT has been mentions XX times with Hashtags #IBM
            for (Map.Entry<String, Integer> entry : topHashtagsMap.entrySet()) {
                infoLogger.info("Nr. " + String.format("%02d", i + 1) + ": " + String.format("%03d", entry.getValue()) + " #" + entry.getKey());


                calculateTopHashtags topSubset;

                switch (i) {
                    case 0:
                        topSubset = new calculateTopHashtags(list0);
                        break;
                    case 1:
                        topSubset = new calculateTopHashtags(list1);
                        break;
                    case 2:
                        topSubset = new calculateTopHashtags(list2);
                        break;
                    case 3:
                        topSubset = new calculateTopHashtags(list3);
                        break;
                    case 4:
                        topSubset = new calculateTopHashtags(list4);
                        break;
                    case 5:
                        topSubset = new calculateTopHashtags(list5);
                        break;
                    case 6:
                        topSubset = new calculateTopHashtags(list6);
                        break;
                    case 7:
                        topSubset = new calculateTopHashtags(list7);
                        break;
                    case 8:
                        topSubset = new calculateTopHashtags(list8);
                        break;
                    case 9:
                        topSubset = new calculateTopHashtags(list9);
                        break;
                    default:
                        topSubset = new calculateTopHashtags();
                }

                Map<String, Integer> topHashtagsMapSubset;
                topHashtagsMapSubset = topSubset.topHashtags(5);

                int j = 1;
                for (Map.Entry<String, Integer> entrySubset : topHashtagsMapSubset.entrySet()) {
                    infoLogger.info("         " + String.format("%02d", entrySubset.getValue()) + " #" + entrySubset.getKey());
                    j++;
                }

                i++;
            }
            infoLogger.info("End");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}