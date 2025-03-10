package com.exclamationlabs.connid.base.connector.util.annotationFramework;

import com.exclamationlabs.connid.base.connector.adapter.EnhancedPaginationAndFiltering;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import java.util.Set;


public class AnnotationAdapterWithPaging<T extends AnnotatedIdentityModel, U extends ConnectorConfiguration>
    extends AnnotationAdapter<T, U> implements EnhancedPaginationAndFiltering {


  public AnnotationAdapterWithPaging(Class<T> AnnotatedIdentityModelClass) {
    super(AnnotatedIdentityModelClass);
  }

  @Override
  public boolean getSearchResultsContainsAllAttributes() {
    return settings.resultsContainsAllAttributes();
  }

  @Override
  public boolean getSearchResultsContainsNameAttribute() {
    return settings.resultsContainsNameAttribute();
  }

  @Override
  public boolean getOneByName() {
    return settings.getOneByName();
  }

  @Override
  public Set<String> getSearchResultsAttributesPresent() {
    return AttributeUtils.getAnnotatedSearchResultFields(clazz);
  }

  @Override
  public boolean getFilteringRequiresFullImport() {
    return settings.filteringRequiresFullImport();
  }

  @Override
  public Integer getSubsequentRequestThreadCount() {
    return EnhancedPaginationAndFiltering.super.getSubsequentRequestThreadCount();
  }

  @Override
  public Integer getImportUsingPaginationThreadCount() {
    return EnhancedPaginationAndFiltering.super.getImportUsingPaginationThreadCount();
  }
}
