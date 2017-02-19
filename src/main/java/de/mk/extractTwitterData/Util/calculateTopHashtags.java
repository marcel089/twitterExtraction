package de.mk.extractTwitterData.Util;

import java.util.*;

/**
 * @author Marcel
 * @project extractTwitterData
 * @email mk@mkarrasch.de
 * @createdOn 29.12.2016
 * @package de.mk.extractTwitterData.Util
 */

public class calculateTopHashtags {
    private ArrayList<String> list = new ArrayList<>();
    private Map<String, Integer> allHashtagsMap = new HashMap<>();
    private Map<String, Integer> topHashtagsMap = new HashMap<>();

    public calculateTopHashtags (ArrayList<String> list) {
        this.list = list;
    }

    public calculateTopHashtags () {
    }

    /**
     * Add a Hashtag to a list of Strings. No handling of dublicates yet
     *
     * @param s Hashtag which should be added.
     */
    public void add (String s) {
        list.add(s);
    }

    /**
     * All Hashtags have been identified. No we need to remove duplicates and cound their quantity.
     * After the dublicates have been removed and the cpunting is complete it will be published on the logger.
     */
    public Map<String, Integer> topHashtags (int i) {

        // list contains all hashtags incl. the dublicates
        for (String temp : list) {
            // Get current quantity of a hashtag which sould be added
            Integer count = allHashtagsMap.get(temp);
            // Add Hashtag to a new Map incl the (new) quantity
            allHashtagsMap.put(temp, (count == null) ? 1 : count + 1);
        }

        allHashtagsMap = sortByValue(allHashtagsMap);

        int j = 0;
        // Copy Top 10 Hashtags into new Map
        for (Map.Entry<String, Integer> entry : allHashtagsMap.entrySet()) {
            topHashtagsMap.put(entry.getKey(), entry.getValue());
            j++;
            if (j == i) {
                break;
            }
        }
        topHashtagsMap = sortByValue(topHashtagsMap);

        return topHashtagsMap;
    }

    /**
     * Sort a Map List by its Value.
     * Inspired by: https://www.mkyong.com/java/how-to-sort-a-map-in-java/
     *
     * @param unsortMap Unsorted Map. Included all Hashtags including the quantity
     * @return Sorted map. Sort criteria is quantity.
     */
    private Map<String, Integer> sortByValue (Map<String, Integer> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare (Map.Entry<String, Integer> o2,
                                Map.Entry<String, Integer> o1) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}
