package net.vanolex.ghostBlocksVisualiser;

import javax.annotation.Nullable;

public class Utils {
    @Nullable
    public static Integer getCombinedId(String name) {
        if (name == null) return null;
        if (!name.startsWith("$GHOST:")) return null;

        String data = name.substring(7);
        try {
            return Integer.parseInt(data);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
