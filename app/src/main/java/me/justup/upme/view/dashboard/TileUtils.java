package me.justup.upme.view.dashboard;


import android.content.Context;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

public class TileUtils {

    /** Returns inverted list by step that take. for example if our list is {1, 2, 3, 4, 5, 6,
     * 7 ,8 ,9} and step is 3 inverted list is this: {3, 2, 1, 6, 5, 4, 9, 8, 7}
     */
    public static <E> ArrayList<E> invert(List<E> source, int step){
        List<E> inverted = new ArrayList<E>();
        for(int i = 0; i < source.size(); i++){
            if((i + 1) % step == 0){
                for(int j = i, count = 0; count < step; j--, count++){
                    inverted.add(source.get(j));
                }
            }
        }

        //
        // When (source.size() % step) is not 0 acts.this is for last of list. add last part
        // of the source that wasn't add.
        //
        int remainder = source.size() % step;
        if((remainder) != 0 ){
            for (int j = source.size() - 1, count = 0; count < (remainder); j--, count++) {
                inverted.add(source.get(j));
            }
        }

        return (ArrayList<E>) inverted;

    }

    public static boolean isPointWithin(int x, int y, int x1, int x2, int y1, int y2) {
        return (x <= x2 && x >= x1 && y <= y1 && y >= y2);
    }

    // Covert Dp to Pixel Second Method
    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
