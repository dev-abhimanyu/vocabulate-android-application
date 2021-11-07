package com.abhimanyu.vocabulate.db;

import java.util.ArrayList;
import java.util.Random;
/**
 * Created by abhimanyu
 */
public class TastyToasts {
    public String getToast()
    {
        ArrayList<String> arrToast = new ArrayList<>();
        arrToast.add("give it a shot");
        arrToast.add("You can do it!");
        arrToast.add("clock is ticking");
        arrToast.add("take a wild guess");
        arrToast.add("you can always do eeny-meeny ;)");
        arrToast.add("persistence is the key");
        arrToast.add("it's not that hard");
        arrToast.add("giving up is not an option");
        arrToast.add("choose your favourite one");
        arrToast.add("try breaking the word");
        arrToast.add("choose the closest one");
        arrToast.add("even I know this one");
        arrToast.add("you forgot to select an option");
        arrToast.add("think harder");
        arrToast.add("where are you trying to go?");
        arrToast.add("you can't escape it!");
        arrToast.add("even my grandma knows this one");
        arrToast.add("hang in there");
        arrToast.add("almost there");
        arrToast.add("you are getting close");
        arrToast.add("if only i could help you");
        arrToast.add("FUN FACT: brain comprises 60% of fat");
        arrToast.add("FUN FACT: the longest word you can make using only four letters is \"senseless.\"");
        Random r = new Random();
        return arrToast.get(r.nextInt(arrToast.size()));
    }


}
