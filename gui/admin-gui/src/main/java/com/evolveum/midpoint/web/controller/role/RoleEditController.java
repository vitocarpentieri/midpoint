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
 */
package com.evolveum.midpoint.web.controller.role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.xml.bind.JAXBElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.evolveum.midpoint.model.api.ModelService;
import com.evolveum.midpoint.schema.constants.ObjectTypes;
import com.evolveum.midpoint.schema.util.JAXBUtil;
import com.evolveum.midpoint.util.logging.LoggingUtils;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.bean.AssignmentBean;
import com.evolveum.midpoint.web.bean.AssignmentBeanType;
import com.evolveum.midpoint.web.bean.BrowserBean;
import com.evolveum.midpoint.web.bean.XmlEditorBean;
import com.evolveum.midpoint.web.controller.TemplateController;
import com.evolveum.midpoint.web.controller.util.ControllerUtil;
import com.evolveum.midpoint.web.model.ObjectManager;
import com.evolveum.midpoint.web.model.ObjectTypeCatalog;
import com.evolveum.midpoint.web.model.RoleManager;
import com.evolveum.midpoint.web.model.dto.ObjectDto;
import com.evolveum.midpoint.web.model.dto.RoleDto;
import com.evolveum.midpoint.web.util.FacesUtils;
import com.evolveum.midpoint.web.util.SelectItemComparator;
import com.evolveum.midpoint.xml.ns._public.common.common_1.AccountConstructionType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.AssignmentType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ObjectReferenceType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.PropertyReferenceListType;
import com.evolveum.midpoint.xml.ns._public.common.common_1.RoleType;

/**
 * 
 * @author lazyman
 * 
 */
@Controller("roleEdit")
@Scope("session")
public class RoleEditController implements Serializable {

	public static final String PAGE_NAVIGATION = "/role/roleEdit?faces-redirect=true";
	public static final String PARAM_ASSIGNMENT_ID = "assignmentId";
	private static final long serialVersionUID = 6390559677870495118L;
	private static final Trace LOGGER = TraceManager.getTrace(RoleEditController.class);
	@Autowired(required = true)
	private transient ObjectTypeCatalog catalog;
	@Autowired(required = true)
	private transient TemplateController template;
	@Deprecated
	@Autowired(required = true)
	private transient ModelService model;
	private BrowserBean<AssignmentBean> browser;
	private XmlEditorBean<AssignmentBean> editor;
	private boolean showBrowser;
	private boolean showEditor;
	private boolean newRole = true;
	private RoleDto role;
	private boolean selectAll;

	public RoleDto getRole() {
		if (role == null) {
			role = new RoleDto(new RoleType());
		}
		return role;
	}

	public boolean isShowEditor() {
		return showEditor;
	}

	public void setShowEditor(boolean showEditor) {
		this.showEditor = showEditor;
	}

	public boolean isShowBrowser() {
		return showBrowser;
	}

	public void setShowBrowser(boolean showBrowser) {
		this.showBrowser = showBrowser;
	}

	public BrowserBean<AssignmentBean> getBrowser() {
		if (browser == null) {
			browser = new BrowserBean<AssignmentBean>();
			browser.setModel(model);
		}
		return browser;
	}

	public XmlEditorBean<AssignmentBean> getEditor() {
		if (editor == null) {
			editor = new XmlEditorBean<AssignmentBean>();
		}
		return editor;
	}

	public List<SelectItem> getAssignmentTypes() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		for (AssignmentBeanType type : AssignmentBeanType.values()) {
			items.add(new SelectItem(type.name(), FacesUtils.translateKey(type.getLocalizationKey())));
		}
		Collections.sort(items, new SelectItemComparator());

		return items;
	}

	/**
	 * True if this controller is used for creating new role, false if we're
	 * editing existing role
	 */
	public boolean isNewRole() {
		return newRole;
	}

	void setNewRole(boolean newRole) {
		this.newRole = newRole;
	}

	void setRole(RoleDto role) {
		Validate.notNull(role, "Role must not be null.");
		this.role = role;
		newRole = false;
		template.setSelectedLeftId("leftRoleEdit");
	}

	public String initController() {
		role = new RoleDto(new RoleType());
		newRole = true;
		template.setSelectedLeftId("leftRoleCreate");

		return PAGE_NAVIGATION;
	}

	public void save(ActionEvent evt) {
		if (role == null) {
			FacesUtils.addErrorMessage("Role must not be null.");
			return;
		}

		try {
			role.normalizeAssignments();
			RoleManager manager = ControllerUtil.getRoleManager(catalog);
			if (isNewRole()) {
				manager.add(role);
			} else {
				manager.submit(getRole());
			}
		} catch (Exception ex) {
			LoggingUtils.logException(LOGGER, "Couldn't submit role {}", ex, role.getName());
			FacesUtils.addErrorMessage("Couldn't submit role '" + role.getName() + "'.", ex);
		}
	}

	public boolean isSelectAll() {
		return selectAll;
	}

	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	public void selectAllPerformed(ValueChangeEvent event) {
		ControllerUtil.selectAllPerformed(event, role.getAssignments());
	}

	public void selectPerformed(ValueChangeEvent evt) {
		this.selectAll = ControllerUtil.selectPerformed(evt, role.getAssignments());
	}

	private int getNewId() {
		int id = 0;
		for (AssignmentBean bean : role.getAssignments()) {
			if (bean.getId() > id) {
				id = bean.getId();
			}
		}

		return ++id;
	}

	public void addAssignment() {
		getRole().getAssignments().add(new AssignmentBean(getNewId(), new AssignmentType()));
		selectAll = false;
	}

	public void deleteAssignments() {
		Iterator<AssignmentBean> iterator = role.getAssignments().iterator();
		while (iterator.hasNext()) {
			AssignmentBean bean = iterator.next();
			if (bean.isSelected()) {
				iterator.remove();
			}
		}

		selectAll = false;
	}

	public void editAssignmentObject() {
		AssignmentBean bean = getBean(FacesUtils.getRequestParameter(PARAM_ASSIGNMENT_ID));
		if (bean == null) {
			return;
		}

		switch (bean.getType()) {
			case TARGET:
			case TARGET_REF:
				browser.setObject(bean);
				setShowBrowser(true);
				break;
			case ACCOUNT_CONSTRUCTION:
				try {
					AccountConstructionType construction = bean.getAccountConstruction();
					String xml = JAXBUtil.marshalWrap(construction);
					editor.setText(xml);
					editor.setObject(bean);
					setShowEditor(true);
				} catch (Exception ex) {
					LoggingUtils.logException(LOGGER, "Couldn't parse account construction", ex);
					FacesUtils.addErrorMessage("Couldn't parse account construction.", ex);
				}
		}
	}

	/**
	 * called when user clicks on CANCEL button in object browser
	 */
	public void cancelBrowseAction() {
		browser.cleanup();
		setShowBrowser(false);
	}

	/**
	 * called when user clicks on OK button in object browser
	 */
	public void okBrowseAction() {
		// we get selected object oid from object browser
		String objectOid = FacesUtils.getRequestParameter(BrowserBean.PARAM_OBJECT_OID);
		if (StringUtils.isEmpty(objectOid)) {
			FacesUtils.addErrorMessage("Object oid from browser was not defined.");
			return;
		}

		ObjectManager<ObjectDto<ObjectType>> manager = ControllerUtil.getObjectManager(catalog);
		ObjectDto<ObjectType> objectDto = manager.get(objectOid, new PropertyReferenceListType());
		if (objectDto == null || objectDto.getXmlObject() == null) {
			return;
		}

		ObjectType object = objectDto.getXmlObject();
		AssignmentBean bean = browser.getObject();
		switch (bean.getType()) {
			case TARGET:
				bean.setTarget(object);
				browser.cleanup();
				setShowBrowser(false);
				break;
			case TARGET_REF:
				bean.setTargetRef(createObjectReference(object));
				browser.cleanup();
				setShowBrowser(false);
		}
	}

	private ObjectReferenceType createObjectReference(ObjectType object) {
		ObjectReferenceType reference = new ObjectReferenceType();
		reference.setOid(object.getOid());
		reference.setType(ObjectTypes.getObjectType(object.getClass()).getTypeQName());

		return reference;
	}

	private AssignmentBean getBean(String id) {
		if (StringUtils.isEmpty(id) || !id.matches("[0-9]+")) {
			FacesUtils.addErrorMessage("Couldn't get internal assignment bean id.");
			return null;
		}

		int beanId = Integer.parseInt(id);
		AssignmentBean bean = null;
		for (AssignmentBean assignmentBean : role.getAssignments()) {
			if (assignmentBean.getId() == beanId) {
				bean = assignmentBean;
				break;
			}
		}

		if (bean == null) {
			FacesUtils.addErrorMessage("Couldn't find assignment bean with selected internal id.");
		}

		return bean;
	}

	public void editAssignment() {
		AssignmentBean bean = getBean(FacesUtils.getRequestParameter(PARAM_ASSIGNMENT_ID));
		if (bean == null) {
			return;
		}

		bean.setEditing(true);
	}

	/**
	 * called when user clicks on OK button in xml editor
	 */
	@SuppressWarnings("unchecked")
	public void okEditorAction() {
		String text = editor.getText();
		if (StringUtils.isEmpty(text)) {
			return;
		}

		AccountConstructionType construction = null;
		try {
			JAXBElement<AccountConstructionType> element = (JAXBElement<AccountConstructionType>) JAXBUtil
					.unmarshal(text);
			construction = element.getValue();
		} catch (Exception ex) {
			LoggingUtils.logException(LOGGER, "Couldn't parser account construction", ex);
			FacesUtils.addErrorMessage("Coulnd't parse account construction.", ex);
		}

		if (construction == null) {
			return;
		}

		AssignmentBean bean = editor.getObject();
		bean.setAccountConstruction(construction);
		editor.cleanup();
		setShowEditor(false);
	}

	/**
	 * called when user clicks on CANCEL button in xml editor
	 */
	public void cancelEditAction() {
		editor.cleanup();
		setShowEditor(false);
	}
}
