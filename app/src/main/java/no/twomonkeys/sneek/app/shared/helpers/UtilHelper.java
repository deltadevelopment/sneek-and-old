package no.twomonkeys.sneek.app.shared.helpers;

/**
 * Created by simenlie on 13.06.16.
 */
public class UtilHelper {

    public static String stalkersStringForNumber(int number) {
        String returnString;
        if (number == 0) {
            returnString = "stalkers";
        } else if (number == 1) {
            returnString = "stalker";
        } else {
            returnString = "stalkers";
        }
        String finalString = number + " " + returnString;
        return finalString.toUpperCase();
    }

}
