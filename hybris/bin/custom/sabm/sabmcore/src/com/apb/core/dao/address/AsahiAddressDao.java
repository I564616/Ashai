package com.apb.core.dao.address;

@FunctionalInterface
public interface AsahiAddressDao {

	String getAddressStatusMapping(String backendStatusCode);

}
