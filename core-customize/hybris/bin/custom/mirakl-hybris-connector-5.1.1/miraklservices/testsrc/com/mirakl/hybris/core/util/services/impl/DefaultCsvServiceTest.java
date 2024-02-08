package com.mirakl.hybris.core.util.services.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.dto.converter.Converter;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCsvServiceTest {

  private static final String HEADER_1 = "header1";
  private static final String HEADER_2 = "header2";
  private static final String HEADER_3 = "header3";

  private static final String LINE_1_VALUE_1 = "line1value1";
  private static final String LINE_1_VALUE_2 = "line1value2";
  private static final String LINE_2_VALUE_1 = "line2;value1";
  private static final String LINE_2_VALUE_3 = "line2 value3";

  private static final String RAW_LINE_1 = "rawLine1";
  private static final String RAW_LINE_2 = "rawLine2";

  private static final String CSV_HEADER = "\"header1\";\"header2\";\"header3\"";
  private static final String CSV_LINE_1 = "\"line1value1\";\"line1value2\";";
  private static final String CSV_LINE_2 = "\"line2;value1\";;\"line2 value3\"";

  @InjectMocks
  private DefaultCsvService testObj = new DefaultCsvService();

  @Mock
  private Converter<String, Map<String, String>> dataConverter;

  @Before
  public void setUp() throws Exception {
    when(dataConverter.convert(RAW_LINE_1)).thenReturn(ImmutableMap.of(HEADER_1, LINE_1_VALUE_1, HEADER_2, LINE_1_VALUE_2));
    when(dataConverter.convert(RAW_LINE_2)).thenReturn(ImmutableMap.of(HEADER_1, LINE_2_VALUE_1, HEADER_3, LINE_2_VALUE_3));
  }

  @Test
  public void createsCsvWithHeaders() throws IOException {
    Map<String, String> csvLine1 = ImmutableMap.of(HEADER_1, LINE_1_VALUE_1, HEADER_2, LINE_1_VALUE_2);
    Map<String, String> csvLine2 = ImmutableMap.of(HEADER_1, LINE_2_VALUE_1, HEADER_3, LINE_2_VALUE_3);

    String result = testObj.createCsvWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, asList(csvLine1, csvLine2));

    assertThat(result).isEqualTo(CSV_HEADER + "\n" + CSV_LINE_1 + "\n" + CSV_LINE_2 + "\n");
  }

  @Test(expected = IllegalArgumentException.class)
  public void createCsvWithHeadersThrowsIllegalArgumentExceptionIfHeaderArrayIsEmpty() throws IOException {
    testObj.createCsvWithHeaders(new String[] {}, Collections.<Map<String, String>>emptyList());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createCsvWithHeadersThrowsIllegalArgumentExceptionIfCellProcessorArrayIsEmpty() throws IOException {
    testObj.createCsvWithHeaders(new String[] {}, Collections.<Map<String, String>>emptyList());
  }

  @Test
  public void createsCsvWithHeadersWithNoValuesIfFieldsListIsEmpty() throws IOException {
    String result =
        testObj.createCsvWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, Collections.<Map<String, String>>emptyList());

    assertThat(result).isEqualTo(CSV_HEADER + "\n");
  }

  @Test(expected = IllegalArgumentException.class)
  public void createCsvWithHeadersThrowsIllegalArgumentExceptionIfFieldsListIsNull() throws IOException {
    testObj.createCsvWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, null);
  }

  @Test
  public void createsCsvWithHeadersWithnoValuesIfFieldListIsEmpty() throws IOException {
    String result =
        testObj.createCsvWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, Collections.<Map<String, String>>emptyList());

    assertThat(result).isEqualTo(CSV_HEADER + "\n");
  }

  @Test
  public void createCsvFileWithHeader() throws IOException {
    File csvFile = null;
    try {
      csvFile = testObj.createCsvFileWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, asList(RAW_LINE_1, RAW_LINE_2),
          dataConverter);
      assertThat(Files.readAllLines(csvFile.toPath())).containsSequence(CSV_HEADER, CSV_LINE_1, CSV_LINE_2);
    } finally {
      deleteQuietly(csvFile);
    }
  }

  @Test
  public void createCsvFileWithHeaderWhenNoData() throws IOException {
    File csvFile = null;
    try {
      csvFile = testObj.createCsvFileWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, emptyList(), dataConverter);
      assertThat(Files.readAllLines(csvFile.toPath())).containsOnly(CSV_HEADER);
    } finally {
      deleteQuietly(csvFile);
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void createCsvFileWithHeaderThrowsIllegalArgumentExceptionIfHeaderIsNull() throws Exception {
    testObj.createCsvFileWithHeaders(null, emptyList(), dataConverter);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createCsvFileWithHeaderThrowsIllegalArgumentExceptionIfDataFieldIsNull() throws Exception {
    testObj.createCsvFileWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, null, dataConverter);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createCsvFileWithHeaderThrowsIllegalArgumentExceptionIfConverterFieldIsNull() throws Exception {
    testObj.createCsvFileWithHeaders(new String[] {HEADER_1, HEADER_2, HEADER_3}, emptyList(), null);
  }

}
