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

package com.exclamationlabs.connid.base.connector.stub.model;

import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import org.identityconnectors.common.security.GuardedByteArray;
import org.identityconnectors.common.security.GuardedString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class StubUser implements IdentityModel {

    private String id;
    private String userName;
    private String email;
    private List<String> groupIds;
    private List<String> clubIds;

    private BigDecimal userTestBigDecimal;
    private BigInteger userTestBigInteger;
    private Boolean userTestBoolean;
    private Byte userTestByte;
    private Character userTestCharacter;
    private Double userTestDouble;
    private Float userTestFloat;
    private GuardedByteArray userTestGuardedByteArray;
    private GuardedString userTestGuardedString;
    private Integer userTestInteger;
    private Long userTestLong;
    private Map<?,?> userTestMap;
    private ZonedDateTime userTestZonedDateTime;

    @Override
    public String getIdentityIdValue() {
        return getId();
    }

    @Override
    public String getIdentityNameValue() {
        return getUserName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public BigDecimal getUserTestBigDecimal() {
        return userTestBigDecimal;
    }

    public void setUserTestBigDecimal(BigDecimal userTestBigDecimal) {
        this.userTestBigDecimal = userTestBigDecimal;
    }

    public BigInteger getUserTestBigInteger() {
        return userTestBigInteger;
    }

    public void setUserTestBigInteger(BigInteger userTestBigInteger) {
        this.userTestBigInteger = userTestBigInteger;
    }

    public Boolean getUserTestBoolean() {
        return userTestBoolean;
    }

    public void setUserTestBoolean(Boolean userTestBoolean) {
        this.userTestBoolean = userTestBoolean;
    }

    public Byte getUserTestByte() {
        return userTestByte;
    }

    public void setUserTestByte(Byte userTestByte) {
        this.userTestByte = userTestByte;
    }

    public Character getUserTestCharacter() {
        return userTestCharacter;
    }

    public void setUserTestCharacter(Character userTestCharacter) {
        this.userTestCharacter = userTestCharacter;
    }

    public Double getUserTestDouble() {
        return userTestDouble;
    }

    public void setUserTestDouble(Double userTestDouble) {
        this.userTestDouble = userTestDouble;
    }

    public Float getUserTestFloat() {
        return userTestFloat;
    }

    public void setUserTestFloat(Float userTestFloat) {
        this.userTestFloat = userTestFloat;
    }

    public GuardedByteArray getUserTestGuardedByteArray() {
        return userTestGuardedByteArray;
    }

    public void setUserTestGuardedByteArray(GuardedByteArray userTestGuardedByteArray) {
        this.userTestGuardedByteArray = userTestGuardedByteArray;
    }

    public GuardedString getUserTestGuardedString() {
        return userTestGuardedString;
    }

    public void setUserTestGuardedString(GuardedString userTestGuardedString) {
        this.userTestGuardedString = userTestGuardedString;
    }

    public Integer getUserTestInteger() {
        return userTestInteger;
    }

    public void setUserTestInteger(Integer userTestInteger) {
        this.userTestInteger = userTestInteger;
    }

    public Long getUserTestLong() {
        return userTestLong;
    }

    public void setUserTestLong(Long userTestLong) {
        this.userTestLong = userTestLong;
    }

    public Map<?, ?> getUserTestMap() {
        return userTestMap;
    }

    public void setUserTestMap(Map<?, ?> userTestMap) {
        this.userTestMap = userTestMap;
    }

    public ZonedDateTime getUserTestZonedDateTime() {
        return userTestZonedDateTime;
    }

    public void setUserTestZonedDateTime(ZonedDateTime userTestZonedDateTime) {
        this.userTestZonedDateTime = userTestZonedDateTime;
    }

    public List<String> getClubIds() {
        return clubIds;
    }

    public void setClubIds(List<String> clubIds) {
        this.clubIds = clubIds;
    }
}
