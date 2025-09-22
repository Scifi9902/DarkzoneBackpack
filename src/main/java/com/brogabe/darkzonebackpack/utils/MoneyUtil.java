package com.brogabe.darkzonebackpack.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyUtil {

    public static String intToDollars(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        return formatter.format(amount);
    }
}
