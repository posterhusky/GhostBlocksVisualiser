package net.vanolex.ghostBlocksVisualiser;

import com.avaje.ebean.validation.NotNull;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class Utils {
    @Nullable
    public static Integer getCombinedId(String name) {
        if (name == null) return null;
        String[] payload = name.split(":");
        if (payload.length < 2) return null;
        if (!payload[0].equals("$GHOST")) return null;

        try {
            return Integer.parseInt(payload[1], 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @NotNull
    public static Pair<Double, Double> calcSlab(double iMin, double iMax, double iStart, double di) { // pair<Tenter, Texit>
        if (di == 0.0) {
            return iStart > iMin && iStart < iMax ?
                    Pair.of(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY) : Pair.of(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        }
        double t1 = (iMin-iStart)/di;
        double t2 = (iMax-iStart)/di;

        return t1 < t2 ? Pair.of(t1, t2) : Pair.of(t2, t1);
    }
}
