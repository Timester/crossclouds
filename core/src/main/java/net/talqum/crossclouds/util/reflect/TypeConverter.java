package net.talqum.crossclouds.util.reflect;

import com.google.common.reflect.TypeToken;

/**
 * Created by Imre on 2015.03.07..
 */
public class TypeConverter {

    public static <T> TypeToken<T> typeToken(Class<T> in) {
        return TypeToken.of(in);
    }
}
