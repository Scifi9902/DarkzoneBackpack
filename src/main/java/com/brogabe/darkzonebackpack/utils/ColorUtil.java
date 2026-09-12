package com.brogabe.darkzonebackpack.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ColorUtil {
    public String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public List<String> color (List<String> strings) {
        return strings.stream().map(ColorUtil::color).collect(Collectors.toList());
    }


}
