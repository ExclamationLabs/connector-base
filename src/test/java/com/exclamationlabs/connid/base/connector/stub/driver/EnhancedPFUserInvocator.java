/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.connector.stub.driver;

import com.exclamationlabs.connid.base.connector.driver.DriverInvocator;
import com.exclamationlabs.connid.base.connector.filter.FilterType;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.stub.model.EnhancedPFUser;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

public class EnhancedPFUserInvocator implements DriverInvocator<EnhancedPFDriver, EnhancedPFUser> {

  static Set<EnhancedPFUser> testUserSet;
  static Map<String, EnhancedPFUser> testUserMap;

  static {
    reset();
  }

  private static void reset() {
    testUserSet = new LinkedHashSet<>();
    testUserSet.add(
        new EnhancedPFUser(
            "1001", "tcobb@test.com", "Ty", "Cobb", "Tigers", "Outfielder", "Detroit"));
    testUserSet.add(
        new EnhancedPFUser(
            "1002", "cripkenjr@test.com", "Cal", "Ripken Jr", "Orioles", "Shortstop", "Baltimore"));
    testUserSet.add(
        new EnhancedPFUser(
            "1003", "bripken@test.com", "Billy", "Ripken", "Orioles", "Second Base", "Baltimore"));
    testUserSet.add(
        new EnhancedPFUser(
            "1004", "emurray@test.com", "Eddie", "Murray", "Orioles", "First Base", "Baltimore"));
    testUserSet.add(
        new EnhancedPFUser(
            "1005",
            "jpalmer@test.com",
            "Jim",
            "Palmer",
            "Orioles",
            "Starting Pitcher",
            "Baltimore"));
    testUserSet.add(
        new EnhancedPFUser(
            "1006", "mmantle@test.com", "Mickey", "Mantle", "Yankees", "Outfielder", "New York"));
    testUserSet.add(
        new EnhancedPFUser(
            "1007", "dgooden@test.com", "Doc", "Gooden", "Mets", "Starting Pitcher", "New York"));
    testUserSet.add(
        new EnhancedPFUser(
            "1008",
            "rfingers@test.com",
            "Rollie",
            "Fingers",
            "Athletics",
            "Relief Pitcher",
            "Oakland"));
    testUserSet.add(
        new EnhancedPFUser(
            "1009",
            "rhenderson@test.com",
            "Rickie",
            "Henderson",
            "Athletics",
            "Left Fielder",
            "Oakland"));
    testUserSet.add(
        new EnhancedPFUser(
            "1010", "vblue@test.com", "Vida", "Blue", "Athletics", "Starting Pitcher", "Oakland"));
    testUserSet.add(
        new EnhancedPFUser(
            "1011",
            "rjackson@test.com",
            "Reggie",
            "Jackson",
            "Athletics",
            "Right Fielder",
            "Oakland"));
    testUserSet.add(
        new EnhancedPFUser(
            "1012",
            "apujols@test.com",
            "Albert",
            "Pujols",
            "Cardinals",
            "First Base",
            "St. Louis"));
    testUserSet.add(
        new EnhancedPFUser(
            "1013", "nryan@test.com", "Nolan", "Ryan", "Rangers", "Starting Pitcher", "Texas"));
    testUserSet.add(
        new EnhancedPFUser(
            "1014", "tgwynn@test.com", "Tony", "Gwynn", "Padres", "Outfielder", "San Diego"));
    testUserSet.add(
        new EnhancedPFUser(
            "1015", "jbench@test.com", "Johnny", "Bench", "Reds", "Catcher", "Cincinnati"));
    testUserSet.add(
        new EnhancedPFUser(
            "1016", "jmorgan@test.com", "Joe", "Morgan", "Reds", "Second Base", "Cincinnati"));
    testUserSet.add(
        new EnhancedPFUser(
            "1017", "gbrett@test.com", "George", "Brett", "Royals", "Third Base", "Kansas City"));
    testUserSet.add(
        new EnhancedPFUser(
            "1018", "bruth@test.com", "Babe", "Ruth", "Yankees", "Right Fielder", "New York"));
    testUserSet.add(
        new EnhancedPFUser(
            "1019", "gmaddux@test.com", "Greg", "Maddux", "Braves", "Starting Pitcher", "Atlanta"));
    testUserSet.add(
        new EnhancedPFUser(
            "1020", "jsmoltz@test.com", "John", "Smoltz", "Braves", "Starting Pitcher", "Atlanta"));
    testUserSet.add(
        new EnhancedPFUser(
            "1021",
            "tglavine@test.com",
            "Tom",
            "Glavine",
            "Braves",
            "Starting Pitcher",
            "Atlanta"));
    testUserSet.add(
        new EnhancedPFUser(
            "1022",
            "bgibson@test.com",
            "Bob",
            "Gibson",
            "Cardinals",
            "Starting Pitcher",
            "St. Louis"));
    testUserSet.add(
        new EnhancedPFUser(
            "1023", "kgibson@test.com", "Kirk", "Gibson", "Dodgers", "Outfielder", "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1024", "jmorris@test.com", "Jack", "Morris", "Tigers", "Starting Pitcher", "Detroit"));
    testUserSet.add(
        new EnhancedPFUser(
            "1025",
            "jverlander@test.com",
            "Justin",
            "Verlander",
            "Tigers",
            "Starting Pitcher",
            "Detroit"));
    testUserSet.add(
        new EnhancedPFUser(
            "1026",
            "ftanana@test.com",
            "Frank",
            "Tanana",
            "Angels",
            "Starting Pitcher",
            "California"));
    testUserSet.add(
        new EnhancedPFUser(
            "1027", "chough@test.com", "Charlie", "Hough", "Rangers", "Starting Pitcher", "Texas"));
    testUserSet.add(
        new EnhancedPFUser(
            "1028",
            "deckersly@test.com",
            "Dennis",
            "Eckersly",
            "Athletics",
            "Relief Pitcher",
            "Oakland"));
    testUserSet.add(
        new EnhancedPFUser(
            "1029", "fmcgriff@test.com", "Fred", "McGriff", "Padres", "First Base", "San Diego"));
    testUserSet.add(
        new EnhancedPFUser(
            "1030", "bsantiago@test.com", "Benito", "Santiago", "Padres", "Catcher", "San Diego"));
    testUserSet.add(
        new EnhancedPFUser(
            "1031",
            "ohershiser@test.com",
            "Orel",
            "Hershiser",
            "Dodgers",
            "Starting Pitcher",
            "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1032",
            "skoufax@test.com",
            "Sandy",
            "Koufax",
            "Dodgers",
            "Starting Pitcher",
            "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1033",
            "fvalenzuela@test.com",
            "Fernando",
            "Valenzuela",
            "Dodgers",
            "Starting Pitcher",
            "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1034",
            "ckershaw@test.com",
            "Clayton",
            "Kershaw",
            "Dodgers",
            "Starting Pitcher",
            "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1035",
            "ddrysdale@test.com",
            "Don",
            "Drysdale",
            "Dodgers",
            "Starting Pitcher",
            "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1036",
            "dsutton@test.com",
            "Don",
            "Sutton",
            "Dodgers",
            "Starting Pitcher",
            "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1037",
            "tbelcher@test.com",
            "Tim",
            "Belcher",
            "Dodgers",
            "Starting Pitcher",
            "Los Angeles"));
    testUserSet.add(
        new EnhancedPFUser(
            "1038", "dwells@test.com", "David", "Wells", "Tigers", "Starting Pitcher", "Detroit"));
    testUserSet.add(
        new EnhancedPFUser(
            "1039",
            "apettitte@test.com",
            "Andy",
            "Pettitte",
            "Yankees",
            "Starting Pitcher",
            "New York"));
    testUserSet.add(
        new EnhancedPFUser(
            "1040",
            "mmussina@test.com",
            "Mike",
            "Mussina",
            "Orioles",
            "Starting Pitcher",
            "Baltimore"));
    testUserSet.add(
        new EnhancedPFUser(
            "1041",
            "rclemens@test.com",
            "Roger",
            "Clemens",
            "Red Sox",
            "Starting Pitcher",
            "Boston"));
    testUserSet.add(
        new EnhancedPFUser(
            "1042",
            "rjohnson@test.com",
            "Randy",
            "Johnson",
            "Marines",
            "Starting Pitcher",
            "Seattle"));
    testUserSet.add(
        new EnhancedPFUser(
            "1043",
            "cschilling@test.com",
            "Curt",
            "Schilling",
            "Phillies",
            "Starting Pitcher",
            "Philadelphia"));
    testUserSet.add(
        new EnhancedPFUser(
            "1044",
            "pmartinez@test.com",
            "Pedro",
            "Martinez",
            "Expos",
            "Starting Pitcher",
            "Montreal"));
    testUserSet.add(
        new EnhancedPFUser(
            "1045",
            "ddrabek@test.com",
            "Doug",
            "Drabek",
            "Pirates",
            "Starting Pitcher",
            "Pittsburg"));

    testUserMap = new LinkedHashMap<>();
    testUserSet.forEach(user -> testUserMap.put(user.getIdentityIdValue(), user));
  }

  @Override
  public String create(EnhancedPFDriver driver, EnhancedPFUser model) throws ConnectorException {
    throw new ConnectorException("not supported");
  }

  @Override
  public void update(EnhancedPFDriver driver, String userId, EnhancedPFUser model)
      throws ConnectorException {
    throw new ConnectorException("not supported");
  }

  @Override
  public void delete(EnhancedPFDriver driver, String id) throws ConnectorException {
    throw new ConnectorException("not supported");
  }

  @Override
  public Set<EnhancedPFUser> getAll(
      EnhancedPFDriver driver,
      ResultsFilter filter,
      ResultsPaginator paginator,
      Integer resultCap,
      Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    reset();
    if (prefetchDataMap == null || prefetchDataMap.size() != 2) {
      throw new IllegalArgumentException("Prefetch data map propagation not working");
    }
    Set<EnhancedPFUser> result = testUserSet;
    if (filter.hasFilter()) {
      if (filter.getFilterType().equals(FilterType.ContainsFilter) || driver.isContainsOnly()) {
        Set<EnhancedPFUser> filteredResults = new LinkedHashSet<>();
        result.stream()
            .filter(
                identity ->
                    StringUtils.containsIgnoreCase(
                        identity.getValueBySearchableAttributeName(filter.getAttribute()),
                        filter.getValue()))
            .forEachOrdered(filteredResults::add);
        result = filteredResults;
      } else if (filter.getFilterType().equals(FilterType.EqualsFilter) || driver.isEqualsOnly()) {
        Set<EnhancedPFUser> filteredResults = new LinkedHashSet<>();
        result.stream()
            .filter(
                identity ->
                    StringUtils.equalsIgnoreCase(
                        identity.getValueBySearchableAttributeName(filter.getAttribute()),
                        filter.getValue()))
            .forEachOrdered(filteredResults::add);
        result = filteredResults;
      } else if (filter.getFilterType().equals(FilterType.AndFilter)) {
        result = performAndFiltering(result, filter);
      }
    }

    if (driver.isCanPaginate() && paginator.hasPagination()) {
      Set<EnhancedPFUser> pagedResults = new LinkedHashSet<>();
      result.stream()
          .skip(paginator.getCurrentOffset() - 1)
          .limit(paginator.getPageSize())
          .forEachOrdered(pagedResults::add);
      result = pagedResults;
    }

    return result;
  }

  @Override
  public EnhancedPFUser getOne(
      EnhancedPFDriver driver, String id, Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    if (prefetchDataMap == null || prefetchDataMap.size() < 2 || prefetchDataMap.size() > 3) {
      throw new IllegalArgumentException("Prefetch data map propagation not working");
    }
    EnhancedPFUser result = testUserMap.get(id);
    result.setDetail(UUID.randomUUID().toString());
    return result;
  }

  @Override
  public EnhancedPFUser getOneByName(
      EnhancedPFDriver driver, String objectName, Map<String, Object> prefetchDataMap)
      throws ConnectorException {
    if (prefetchDataMap == null || prefetchDataMap.size() != 2) {
      throw new IllegalArgumentException("Prefetch data map propagation not working");
    }

    EnhancedPFUser match =
        testUserSet.stream()
            .filter(
                identity ->
                    StringUtils.equalsIgnoreCase(objectName, identity.getIdentityNameValue()))
            .findFirst()
            .orElse(null);
    if (match != null) {
      return getOne(driver, match.getUserId(), prefetchDataMap);
    } else {
      return null;
    }
  }

  @Override
  public Map<String, Object> getPrefetch(EnhancedPFDriver driver) {
    return new HashMap<>(Map.of("ying", "yang", "lorem", "ipsum"));
  }

  private Set<EnhancedPFUser> performAndFiltering(
      Set<EnhancedPFUser> result, ResultsFilter filter) {
    for (Map.Entry<String, String> entry : filter.getAndFilterDataMap().entrySet()) {
      Set<EnhancedPFUser> filteredResults = new LinkedHashSet<>();
      if (filter.getAndFilterType() == FilterType.EqualsFilter) {
        result.stream()
            .filter(
                identity ->
                    StringUtils.equalsIgnoreCase(
                        identity.getValueBySearchableAttributeName(entry.getKey()),
                        entry.getValue()))
            .forEachOrdered(filteredResults::add);
      } else {
        result.stream()
            .filter(
                identity ->
                    StringUtils.containsIgnoreCase(
                        identity.getValueBySearchableAttributeName(entry.getKey()),
                        entry.getValue()))
            .forEachOrdered(filteredResults::add);
      }
      result = filteredResults;
    }
    return result;
  }
}
