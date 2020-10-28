package control;

import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.CharArrayArray;
import com.sun.xml.internal.messaging.saaj.util.CharReader;
import model.CompactDisc;

import javax.print.DocFlavor;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Jean-Pierre on 21.10.2016.
 */
public class CDCollectionHandler {

    private CompactDisc[][] allCDs;

    /**
     * Die Anzahl an Platzgrößen gibt die Anzahl an CD-Ständern vor - hier 4.
     * Die CD-Ständer an sich sind so groß wie die jeweilige Platzgröße.
     *
     * @param amounts - Platzgrößen der einzelnen CD-Ständer.
     */
    public CDCollectionHandler(int[] amounts) {
        allCDs = new CompactDisc[amounts.length][];

        for (int i = 0; i < allCDs.length; i++) {
            allCDs[i] = new CompactDisc[amounts[i]];
        }

    }

    /**
     * @param box    - Gewählter CD-Ständer
     * @param place  - Gewählter Platz
     * @param artist - Künstername/Bandname
     * @param title  - Albumtitel
     * @return - true, falls ein Platz frei war und die CD hinzugefügt werden konnte, sonst false.
     */
    public boolean addNewCD(int box, int place, String artist, String title) {
        if (allCDs[box][place] == null) {
            allCDs[box][place] = new CompactDisc(artist, title);
            return true;
        }
        return false;
    }

    /**
     * Diese Methode dient dazu, die Daten einer bestimmten Position im zweidimensionalem Array auszugeben.
     *
     * @param box   - Gewählter CD-Ständer
     * @param place - Gewählter Platz
     * @return - Entweder ein String-Array mit "Künstler" - "Titel" oder mit "Empty" - "Empty".
     */
    public String[] getInfo(int box, int place) {
        String[] output = new String[2];
        if (allCDs[box][place] != null) {
            output[0] = allCDs[box][place].getArtist();
            output[1] = allCDs[box][place].getTitle();
        } else {
            output[0] = "Empty";
            output[1] = "Empty";
        }
        return output;
    }

    /**
     * Diese Methode dient dem Entfernen einer CD.
     *
     * @param box   - Gewählter CD-Ständer
     * @param place - Gewählter Platz
     * @return - true, falls eine vorhandene CD entfernt wurde, false, falls keine Cd zum entfernen vorhanden war.
     */
    public boolean releaseCD(int box, int place) {
        if (allCDs[box][place] != null) {
            allCDs[box][place] = null;
            return true;
        }
        return false;
    }

    /**
     * Diese Methode dient dazu, die enthaltenen Daten aufzubereiten und als String-Array auszugeben.
     *
     * @param index - CD-Ständer, um den es sich handelt.
     * @return Ein Array, das abwechselnd den jeweiligen Künstler und den jeweiligen Albumtitel enthält. Leere Plätze werden mit "Empty" gefüllt.
     */
    public String[] getAllCDsFrom(int index) {
        String[] str = new String[allCDs[index].length * 2];
        for (int i = 0; i < allCDs[index].length; i++) {
            str[2 * i] = getInfo(index, i)[0];
            str[2 * i + 1] = getInfo(index, i)[1];
        }
        return str;
    }

    /**
     * Diese Methode dient dazu, einen CD-Ständer zu komprimieren. Dabei rücken spätere CDs einfach auf. Die vorhandene Sortierung bleibt erhalten.
     *
     * @param box - Gewählter CD-Ständer
     */
    public void pack(int box) {
        boolean cd = true;
        for (int i = 0; i < allCDs[box].length && cd; i++) {
            if (allCDs[box][i] != null && allCDs[box][i].getArtist().equals("Empty") && allCDs[box][i].getTitle().equals("Empty")) {
                allCDs[box][i] = null;
            }
            if(allCDs[box][i] == null) {
                cd = false;
                for (int j = i+1; j < allCDs[box].length && !cd; j++) {
                    if (allCDs[box][j] != null) {
                        allCDs[box][i] = allCDs[box][j];
                        allCDs[box][j] = null;
                        cd = true;
                    }
                }
            }
        }
    }

    /**
     * Diese Methode dient dazu, einen CD-Ständer zu sortieren nach Artist+Title. Gleichzeitig wird der CD-Ständer komprimiert.
     *
     * @param box - Gewählter CD-Ständer
     */
    public void sort(int box) {
        pack(box);

        for (int i = 0; i < allCDs[box].length; i++) {
            for (int j = allCDs[box].length-1; j > i; j--) {
                if (allCDs[box][j] != null && allCDs[box][j - 1] != null) {
                    char[] artistChars1 = allCDs[box][j].getArtist().toCharArray();
                    char[] artistChars2 = allCDs[box][j-1].getArtist().toCharArray();
                    char[] titleChars1 = allCDs[box][j].getTitle().toCharArray();
                    char[] titleChars2 = allCDs[box][j-1].getTitle().toCharArray();

                    boolean a = true;
                    for(int h = 0; h < artistChars1.length && h < artistChars2.length && a; h++){
                        if(Character.getNumericValue(artistChars1[h])<Character.getNumericValue(artistChars2[h])){
                            CompactDisc help = allCDs[box][j];
                            allCDs[box][j] = allCDs[box][j - 1];
                            allCDs[box][j - 1] = help;
                            a = false;
                        }else if(Character.getNumericValue(artistChars1[h])>Character.getNumericValue(artistChars2[h])){
                            a = false;
                        }
                    }
                    if(a && artistChars1.length < artistChars2.length){
                        CompactDisc help = allCDs[box][j];
                        allCDs[box][j] = allCDs[box][j - 1];
                        allCDs[box][j - 1] = help;
                        a = false;
                    }else if(a && artistChars1.length > artistChars2.length){
                        a = false;
                    }

                    for(int h = 0; h < titleChars1.length && h < titleChars2.length && a; h++){
                        if(Character.getNumericValue(titleChars1[h])<Character.getNumericValue(titleChars2[h])){
                            CompactDisc help = allCDs[box][j];
                            allCDs[box][j] = allCDs[box][j - 1];
                            allCDs[box][j - 1] = help;
                            a = false;
                        }else if(Character.getNumericValue(titleChars1[h])>Character.getNumericValue(titleChars2[h])){
                            a = false;
                        }
                    }
                    if(a && titleChars1.length < titleChars2.length){
                        CompactDisc help = allCDs[box][j];
                        allCDs[box][j] = allCDs[box][j - 1];
                        allCDs[box][j - 1] = help;
                        a = false;
                    }else if(a && titleChars1.length > titleChars2.length){
                        a = false;
                    }
                }
            }
        }
    }
}
