package com.mirakl.hybris.occ.mapping.converters;

import static org.apache.commons.io.FileUtils.getTempDirectory;

import java.io.*;
import java.nio.file.Files;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;

/**
 * Bidirectional converter between {@link MultipartFile} and {@link File}
 */
@WsDTOMapping
public class MultipartFileToFileConverter extends BidirectionalConverter<MultipartFile, File> {
  private static final Logger LOG = Logger.getLogger(MultipartFileToFileConverter.class);

  @Override
  public File convertTo(MultipartFile source, Type<File> destinationType, MappingContext mappingContext) {
    if (source.getSize() == 0) {
      return null;
    }
    final String originalFilename = source.getOriginalFilename();
    final File tmpFile;
    if (originalFilename != null) {
      tmpFile = new File(getTempDirectory(), originalFilename);
      try {
        source.transferTo(tmpFile);
      } catch (IOException e) {
        LOG.error("Could not convert file to local file", e);
      }
      return tmpFile;
    }
   return null;
  }

  @Override
  public MultipartFile convertFrom(File file, Type<MultipartFile> destinationType, MappingContext mappingContext) {
    FileItem fileItem = null;
    try {
      fileItem = new DiskFileItemFactory().createItem("file", Files.probeContentType(file.toPath()), false, file.getName());
    } catch (IOException e) {
      LOG.error("Could not find file to local file", e);
    }
    if(fileItem != null) {
      try (InputStream in = new FileInputStream(file); OutputStream out = fileItem.getOutputStream()) {
        in.transferTo(out);
        return new CommonsMultipartFile(fileItem);
      } catch (Exception e) {
        LOG.error("Could not find file to local file", e);
      }
    }
    return null;
  }
}
