/*
 * Copyright (c) 2010-2013 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.evolveum.midpoint.schema.constants;

/**
 * Enumeration of standardized test connection opration codes as they are presented in the OperationResult.
 * 
 * @author lazyman
 * @author Radovan Semancik
 * 
 */
public enum ConnectorTestOperation {
	
	TEST_CONNECTION(ConnectorTestOperation.class.getName() + ".testConnection"),

	/**
	 * Check whether the configuration is valid e.g. well-formed XML, valid with regard to schema, etc.
	 */
	CONFIGURATION_VALIDATION(ConnectorTestOperation.class.getName() + ".configurationValidation"),

	/**
	 * Check whether the connector can be initialized.
	 * E.g. connector classes can be loaded, it can process configuration, etc.
	 */
	CONNECTOR_INITIALIZATION(ConnectorTestOperation.class.getName() + ".connectorInitialization"),

	/**
	 * Check whether a connection to the resource can be established.
	 */
	CONNECTOR_CONNECTION(ConnectorTestOperation.class.getName() + ".connectorConnection"),

	/**
	 * Check whether the connector can fetch and process resource schema.
	 */
	CONNECTOR_SCHEMA(ConnectorTestOperation.class.getName() + ".connectorSchema"),

	/**
	 * Check whether the connector can be used to fetch some mandatory objects (e.g. fetch a "root" user).
	 */
	CONNECTOR_SANITY(ConnectorTestOperation.class.getName() + ".connectorSanity"),

	EXTRA_TEST(ConnectorTestOperation.class.getName() + ".extraTest");

	private String operation;

	private ConnectorTestOperation(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}
}
