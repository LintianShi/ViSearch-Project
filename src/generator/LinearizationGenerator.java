package generator;

import datatype.AbstractDataType;
import trace.Invocation;

import java.util.List;

public interface LinearizationGenerator {
    List<Invocation> generate(int length, GeneratorSetting generatorSetting);
}
