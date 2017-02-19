/*
 * Copyright (c) Marcel (mk@mkarrasch.de)
 *
 * All rights reserved.
 */

package de.mk.extractTwitterData.API;

import de.mk.extractTwitterData.Util.SortResponseEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Marcel
 * @project extractTwitterData
 * @email mk@mkarrasch.de
 * @createdOn 26.11.2016
 * @package de.mk.extractTwitterData.API
 */

public class alchemyAPI extends APIBasics implements interfaceAPI {

    private static final Logger infoLogger = LogManager.getLogger("twitterExtraction");

    public alchemyAPI () {
        entityList = new ArrayList<>();
        APIKey = properties.getProperty("alchemyAPIKey");
        outputMode = properties.getProperty("outputMode");
    }

    /**
     * Implemented createPOST from Interface interfaceAPI (see for more details)
     *
     * @param tweet Twitter Tweet which should be posted
     * @throws UnsupportedEncodingException if text is not in Unicode
     */
    @Override
    public void createPOST (String tweet) throws UnsupportedEncodingException {
        httpclient = HttpClients.createDefault();
        httppost = new HttpPost("http://access.alchemyapi.com/calls/text/TextGetRankedNamedEntities");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(3);
        params.add(new BasicNameValuePair("text", tweet));
        params.add(new BasicNameValuePair("apikey", APIKey));
        params.add(new BasicNameValuePair("outputMode", outputMode));

        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

    }

    /**
     * Implemented receiveGET from Interface interfaceAPI (see for more details)
     *
     * @throws IOException    IO Error
     * @throws ParseException Parse Error
     */
    @Override
    public void receiveGET () throws IOException, ParseException {
        JSONArray JSONArray = readResponseJSON(_ALCHEMY, EntityUtils.toString(httpEntity, "UTF-8"));

        if (JSONArray == null) {
            System.out.println("AlchemyAPI Key expired. System shuts down");
            System.exit(0);
        }
        for (Object aJSONArray : JSONArray) {

            JSONObject object = (JSONObject) aJSONArray;
            responseEntity entity = new responseEntity();

            String s = (String) object.get("text");
            s = addEntity(s);

            // Add Entity only if it is new and has not been added before
            if (s != null) {
                entity.setEntity(s);
                entity.setConfidence(convertRelevance((String) object.get("relevance")));
                entityList.add(entity);
            }
        }

        // Sort the Array List Entities from A to Z
        Collections.sort(entityList, new SortResponseEntity());

        int i = 1;
        for (responseEntity e : entityList) {
            infoLogger.debug("Entity " + i + " is " + e.getEntity() + "(" + e.getConfidence() + ")");
            i++;
        }
    }
}
