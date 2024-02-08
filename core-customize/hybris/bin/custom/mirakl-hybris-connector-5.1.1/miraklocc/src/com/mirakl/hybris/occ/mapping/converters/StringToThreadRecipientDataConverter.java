package com.mirakl.hybris.occ.mapping.converters;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.mirakl.hybris.beans.ThreadRecipientData;
import com.mirakl.hybris.core.enums.MiraklThreadParticipantType;

import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

/**
 * Bidirectional converter between {@link String} and {@link Set<ThreadRecipientData>}
 */
@WsDTOMapping
public class StringToThreadRecipientDataConverter extends BidirectionalConverter<String, Set<ThreadRecipientData>> {
  public static final String THREAD_RECIPIENTS_DELIMITER = ",";

  @Override
  public Set<ThreadRecipientData> convertTo(String source, Type<Set<ThreadRecipientData>> destinationType,
      MappingContext mappingContext) {
    final Set<ThreadRecipientData> recipientDataSet = new HashSet<>();
    if (source != null) {
      final List<String> split = Arrays.asList(source.split(THREAD_RECIPIENTS_DELIMITER));
      if (split.contains(MiraklThreadParticipantType.SHOP.getCode())) {
        ThreadRecipientData recipientData = new ThreadRecipientData();
        recipientData.setType(MiraklThreadParticipantType.SHOP.getCode());
        recipientDataSet.add(recipientData);
      }
      if (split.contains(MiraklThreadParticipantType.OPERATOR.getCode())) {
        ThreadRecipientData recipientData = new ThreadRecipientData();
        recipientData.setType(MiraklThreadParticipantType.OPERATOR.getCode());
        recipientDataSet.add(recipientData);
      }
    }
    return recipientDataSet;
  }

  @Override
  public String convertFrom(Set<ThreadRecipientData> source, Type<String> destinationType, MappingContext mappingContext) {
    return source.stream().map(ThreadRecipientData::getType).collect(Collectors.joining(THREAD_RECIPIENTS_DELIMITER));
  }
}
