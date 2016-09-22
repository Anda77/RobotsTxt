/*
 * Copyright 2016 Piotr Andzel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.panforge.robotstxt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Robots.txt group of directives.
 */
class Group {
  private final List<String> userAgents = new ArrayList<>();
  private final AccessList accessList = new AccessList();
  private boolean anyAgent;

  /**
   * Checks if is any agent.
   * @return <code>true</code> if any agent
   */
  public boolean isAnyAgent() {
    return anyAgent;
  }
  
  /**
   * Checks if group is exact in terms of user agents.
   * @param group group to compare
   * @return {@code true} if sections are exact.
   */
  public boolean isExact(Group group) {
    if (isAnyAgent() && group.isAnyAgent()) return true;
    if ((isAnyAgent() && !group.isAnyAgent() || (!isAnyAgent() && group.isAnyAgent()))) return false;

    return group.userAgents.stream().anyMatch(sectionUserAgent->userAgents.stream().anyMatch(userAgent->userAgent.equalsIgnoreCase(sectionUserAgent)));
  }
  
  /**
   * Adds user agent.
   * @param userAgent host name
   */
  public void addUserAgent(String userAgent) {
    if (userAgent.equals("*")) {
      anyAgent = true;
    } else {
      this.userAgents.add(userAgent);
    }
  }

  /**
   * Gets access list.
   * @return access list
   */
  public AccessList getAccessList() {
    return accessList;
  }
  
  /**
   * Adds access.
   * @param access access
   */
  public void addAccess(Access access) {
    this.accessList.addAccess(access);
  }
  
  /**
   * Select any access matching input path.
   * @param userAgen user agent
   * @param relativePath path to test
   * @param matchingStrategy matcher
   * @return list of matching elements
   */
  public List<Access> select(String userAgent, String relativaPath, MatchingStrategy matchingStrategy) {
    if (userAgent==null || relativaPath==null || !matchUserAgent(userAgent)) {
      return Collections.EMPTY_LIST;
    }
    return accessList.select(relativaPath, matchingStrategy);
  }
  
  /**
   * Checks if the section is applicable for a given user agent.
   * @param userAgent requested user agent
   * @return <code>true</code> if the section is applicable for the requested user agent
   */
  public boolean matchUserAgent(String userAgent) {
    if (anyAgent) return true;
    return userAgents.stream().anyMatch(agent->agent.equalsIgnoreCase(userAgent));
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    
    if (anyAgent) {
      sb.append(String.format("User-agent: %s", "*"));
      sb.append("\n");
    }

    for (String userAgent: userAgents) {
      sb.append(String.format("User-agent: %s", userAgent));
      sb.append("\n");
    }

    sb.append(accessList);
    
    return sb.toString();
  }
}