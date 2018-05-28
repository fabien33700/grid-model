package org.gridmodel.core;

import org.gridmodel.index.IndexAdapter;

/**
 * A class for providing adapters functions for all natives types.
 * @author Fabien
 */
@SuppressWarnings("unused")
public class Adapters {
    public static IndexAdapter<Double>  asDouble()  { return Double::parseDouble; }
    public static IndexAdapter<Float>   asFloat()   { return Float::parseFloat; }
    public static IndexAdapter<Long>    asLong()    { return Long::parseLong; }
    public static IndexAdapter<Integer> asInteger() { return Integer::parseInt; }
    public static IndexAdapter<Short>   asShort()   { return Short::parseShort; }
    public static IndexAdapter<Byte>    asByte()    { return Byte::parseByte; }

}
