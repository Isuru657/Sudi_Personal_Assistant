import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MarkovReader {

    /**
     * @param tags file name of the tag to tags
     * @param sentences file name of the tags to sentences
     * @param transProb
     * @param obsProb
     * @return returns the map of tags that start sentences pointing to the counts of those starts
     * @throws Exception if any file is not found
     */

    public static Map<String, Double> readTransition(String tags, String sentences, Map<String, Map<String, Double>> transProb, Map<String, Map<String, Double>> obsProb) throws Exception {
        BufferedReader in= new BufferedReader(new FileReader(tags));
        String line;

        ArrayList<String> allTags= new ArrayList<>();
        Map<String, Double> startTags= new TreeMap<>();

        while ((line=in.readLine()) != null){
            String [] words= line.split(" "); //array of all the tags
            //adding all tags to ArrayList allTags
            for (int i=0; i<words.length; i++){
                allTags.add(words[i]);

                //if we are at the first tag in the line, add it to starterTags map
                if (i==0){
                    if (!startTags.containsKey(words[i])){
                        startTags.put(words[i], (double) 1);
                    }
                    else {
                        startTags.put(words[i], startTags.get(words[i])+1);
                    }
                }

                //make sure the index isnt the last one because then it otherwise wouldnt point to anything
                if (!(i==words.length-1)){

                    //if the key isnt in transitionsProb, just add it in with count equalling 1
                    if (!transProb.containsKey(words[i])){
                        transProb.put(words[i], new HashMap<String, Double>());
                        transProb.get(words[i]).put(words[i+1], (double) 1);
                    }
                    else {

                        //if the key is already in the map, need to update the map that it points to
                        //if the second key in a row is not already in the map, add it and update probabilities

                        if (!transProb.get(words[i]).containsKey(words[i+1])) {
                            transProb.get(words[i]).put(words[i+1], (double) 1);
                        }
                        else {
                            transProb.get(words[i]).put(words[i + 1], (transProb.get(words[i]).get(words[i + 1]) + 1));
                        }

                    }
                }

                //if we are at the last element in the list, add the tag if it doesnt exist, pointing to an empty map
                else {
                    if (!transProb.containsKey(words[i])) {
                        transProb.put(words[i], new HashMap<String, Double>());
                    }
                }

                if (!obsProb.containsKey(words[i])){
                    obsProb.put(words[i], new HashMap<String, Double>());
                }
            }
        }

        BufferedReader sentenceReader= new BufferedReader(new FileReader(sentences));
        int counter=0;

        while ((line=sentenceReader.readLine())!=null) {
                String[] sentwords= line.split(" ");

                for (int i=0; i<sentwords.length; i++){     //loop through each word in each line
                    counter++;

                    //make sure we are not at the last word
                    if (i!=sentwords.length-1){



                        //all tags is every tag in order, which will match the sentences file, so we keep a counter variable to keep going through the tags as we go through the sentences file
                        //update the counts - either it doesnt contain it and we add a new one, or it does and we add 1 to the count
                        if (!(obsProb.get(allTags.get(counter-1))).containsKey(sentwords[i])){
                            obsProb.get(allTags.get(counter-1)).put(sentwords[i], (double) 1);
                        }

                        //at the last tag, itll point to nothing because there is no word that follows the last tag
                        else {
                            obsProb.get(allTags.get(counter-1)).put(sentwords[i], obsProb.get(allTags.get(counter-1)).get(sentwords[i])+ (double) 1);
                        }
                    }

                    else {
                        obsProb.get(allTags.get(counter-1)).put("nothing", (double) 1);
                    }

                }
        }

        //here we update probs/log them, so go through each map and key value pair to change the value
        for (String map: transProb.keySet()){
            double total=0;

            for (String map2: transProb.get(map).keySet()){
                total += transProb.get(map).get(map2);

            }

            for (String map2: transProb.get(map).keySet()){
                transProb.get(map).put(map2, Math.log10((transProb.get(map).get(map2)/total)));
            }
        }

        //same thing for observations - go through each tag - map pair then word - probability pair
        for (String map: obsProb.keySet()){
            double total=0;

            for (String map2: obsProb.get(map).keySet()){
                total += obsProb.get(map).get(map2);

            }

            for (String map2: obsProb.get(map).keySet()){
                obsProb.get(map).put(map2, Math.log10((obsProb.get(map).get(map2)/total)));
            }
        }

    return startTags;


    }

}
