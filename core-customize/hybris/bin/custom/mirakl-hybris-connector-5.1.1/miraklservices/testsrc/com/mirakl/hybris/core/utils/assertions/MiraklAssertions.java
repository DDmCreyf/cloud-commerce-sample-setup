package com.mirakl.hybris.core.utils.assertions;

import java.util.List;

public class MiraklAssertions {

    public static MiraklExtractedFieldsAssert assertThatExtractedFields(List<?> objects, String... fieldNames) {
        return new MiraklExtractedFieldsAssert(objects, fieldNames);
    }

}
