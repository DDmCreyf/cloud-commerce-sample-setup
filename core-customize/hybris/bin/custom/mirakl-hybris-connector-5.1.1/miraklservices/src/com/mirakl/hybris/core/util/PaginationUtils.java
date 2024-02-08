package com.mirakl.hybris.core.util;

import static java.lang.Math.ceil;
import static java.lang.Math.min;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.mirakl.client.request.common.AbstractMiraklFullAndPaginationRequest;
import com.mirakl.client.request.common.AbstractMiraklPaginationRequest;

public class PaginationUtils {

  private PaginationUtils() {
    // No instanciation
  }

  /**
   * Calculates the number of pages, given a total count and a page size
   *
   * @param totalCount total count
   * @param pageSize page size
   * @return The number of pages
   */
  public static int getNumberOfPages(long totalCount, double pageSize) {
    return (int) ceil(totalCount / pageSize);
  }

  /**
   * Extracts a page from a collection. Page numbers are starting from 0. Throws an exception when out of bond.
   *
   * @param pageNumber the page to return
   * @param pageSize page size
   * @param exportData the list from which the page will be extracted
   * @return The page from the given collection
   */
  public static <T> List<T> getPage(int pageNumber, int pageSize, List<T> exportData) {
    Preconditions.checkArgument(exportData != null);
    return exportData.subList(pageNumber * pageSize, min((pageNumber + 1) * pageSize, exportData.size()));
  }

  /**
   * Extracts a page from a collection. Page numbers are starting from 0. Doesn't throws an exception when out of bound.
   *
   * @param pageNumber the page to return
   * @param pageSize page size
   * @param exportData the list from which the page will be extracted
   * @return The page from the given collection
   */
  public static <T> List<T> getSafePage(int pageNumber, int pageSize, List<T> exportData) {
    Preconditions.checkArgument(exportData != null);
    final int fromIndex = pageNumber * pageSize;
    final int toIndex = min((pageNumber + 1) * pageSize, exportData.size());
    if(fromIndex < 0 || fromIndex > toIndex){
      return  Collections.emptyList();
    }
    return exportData.subList(fromIndex, toIndex);
  }

  /**
   * Apply MMP pagination params on the Mirakl input full request. The request is also returned in the output to ease integration.
   *
   * @param paginate if paginate
   * @param maximum maximum size of object to get
   * @param offset the pagination offset
   * @return request with applied params
   */
  public static final <T extends AbstractMiraklFullAndPaginationRequest> T applyMiraklFullPagination(T request, boolean paginate, int maximum, int offset){
    if(request == null){
      return null;
    }
    request.setPaginate(paginate);
    request.setMax(maximum);
    request.setOffset(offset);
    return request;
  }

  /**
   * Apply MMP pagination params on the Mirakl input request. The request is also returned in the output to ease integration.
   *
   * @param maximum maximum size of object to get
   * @param offset the pagination offset
   * @return request with applied params
   */
  public static final <T extends AbstractMiraklPaginationRequest> T applyMiraklPagination(T request, int maximum,  int offset){
    if(request == null){
      return null;
    }
    request.setMax(maximum);
    request.setOffset(offset);
    return request;
  }

}
