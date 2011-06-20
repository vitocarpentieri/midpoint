/*
 * Copyright (c) 2011 Evolveum
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://www.opensource.org/licenses/cddl1 or
 * CDDLv1.0.txt file in the source code distribution.
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 *
 * Portions Copyrighted 2011 [name of copyright owner]
 * Portions Copyrighted 2010 Forgerock
 */

package com.evolveum.midpoint.web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.evolveum.midpoint.web.model.ResourceManager;
import com.evolveum.midpoint.web.model.dto.PropertyAvailableValues;
import com.evolveum.midpoint.web.model.dto.PropertyChange;
import com.evolveum.midpoint.web.model.dto.ResourceDto;
import com.evolveum.midpoint.web.model.dto.ResourceObjectShadowDto;
import com.evolveum.midpoint.xml.ns._public.common.common_1.PagingType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.PropertyReferenceListType;

/**
 * 
 * @author katuska
 */
public class ResourceTypeManagerMock extends ResourceManager {

	private static final long serialVersionUID = -2673752961587849731L;

	Map<String, ResourceDto> resourceTypeList = new HashMap<String, ResourceDto>();

	private final Class constructResourceType;

	public ResourceTypeManagerMock(Class constructResourceType) {
		this.constructResourceType = constructResourceType;
	}

	@Override
	public List<ResourceObjectShadowDto> listObjectShadows(String oid, Class resourceObjectShadowType) {
		return new ArrayList<ResourceObjectShadowDto>();
	}

	@Override
	public Collection<ResourceDto> list() {
		return resourceTypeList.values();
	}

	@Override
	public ResourceDto get(String oid, PropertyReferenceListType resolve) {
		for (ResourceDto resource : resourceTypeList.values()) {
			if (resource.getOid().equals(oid)) {
				return resource;
			}
		}
		return null;
	}

	@Override
	public ResourceDto create() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String add(ResourceDto newObject) {
		resourceTypeList.clear();
		if (newObject.getOid() == null) {
			newObject.setOid(UUID.randomUUID().toString());
		}
		resourceTypeList.put(newObject.getOid(), newObject);
		return newObject.getOid();
	}

	@Override
	public Set<PropertyChange> submit(ResourceDto changedObject) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void delete(String oid) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<PropertyAvailableValues> getPropertyAvailableValues(String oid, List<String> properties) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Collection<ResourceDto> list(PagingType paging) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
