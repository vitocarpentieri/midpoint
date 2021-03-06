/*
 * Copyright (c) 2010-2019 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.gui.impl.prism.wrapper;

import com.evolveum.midpoint.gui.api.prism.ItemStatus;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismContainerValueWrapper;
import com.evolveum.midpoint.prism.*;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;

import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;

/**
 * Created by honchar
 */
public class ExpressionWrapper extends PrismPropertyWrapperImpl<ExpressionType> {

    private QName customQName = new QName("com.evolveum.midpoint.gui","customExtenstionType");

    private static final Trace LOGGER = TraceManager.getTrace(ExpressionWrapper.class);
    private ConstructionType construction;

    public ExpressionWrapper(@Nullable PrismContainerValueWrapper parent, PrismProperty<ExpressionType> property, ItemStatus status) {
        super(parent, property, status);
    }

    public boolean isConstructionExpression(){
        PrismContainerWrapperImpl outboundContainer = getParent() != null ? (PrismContainerWrapperImpl)getParent().getParent() : null;
        if (outboundContainer != null && MappingType.class.equals(outboundContainer.getCompileTimeClass())) {
            PrismContainerValueWrapperImpl outboundValue = (PrismContainerValueWrapperImpl) outboundContainer.getParent();
            if (outboundValue != null) {
                PrismContainerWrapperImpl associationContainer = (PrismContainerWrapperImpl) outboundValue.getParent();
                if (associationContainer != null &&
                        (ResourceObjectAssociationType.class.equals(associationContainer.getCompileTimeClass()) ||
                                ResourceAttributeDefinitionType.class.equals(associationContainer.getCompileTimeClass()))) {
                    PrismContainerValueWrapperImpl constructionContainer = (PrismContainerValueWrapperImpl) associationContainer.getParent();
                    if (constructionContainer != null && constructionContainer.getRealValue() instanceof ConstructionType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isAssociationExpression(){
        if (!getPath().last().equals(MappingType.F_EXPRESSION.last())){
            return false;
        }
        PrismContainerWrapperImpl outboundContainer = getParent() != null ? (PrismContainerWrapperImpl)getParent().getParent() : null;
        if (outboundContainer != null && MappingType.class.equals(outboundContainer.getCompileTimeClass())) {
            PrismContainerValueWrapperImpl outboundValue = (PrismContainerValueWrapperImpl) outboundContainer.getParent();
            if (outboundValue != null) {
                PrismContainerWrapperImpl associationContainer = (PrismContainerWrapperImpl) outboundValue.getParent();
                if (associationContainer != null &&
                        ResourceObjectAssociationType.class.equals(associationContainer.getCompileTimeClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAttributeExpression() {
        if (!getPath().last().equals(MappingType.F_EXPRESSION.last())){
            return false;
        }
        PrismContainerWrapperImpl outboundContainer = getParent() != null ? (PrismContainerWrapperImpl) getParent().getParent() : null;
        if (outboundContainer != null && MappingType.class.equals(outboundContainer.getCompileTimeClass())) {
            PrismContainerValueWrapperImpl outboundValue = (PrismContainerValueWrapperImpl) outboundContainer.getParent();
            if (outboundValue != null) {
                PrismContainerWrapperImpl attributeContainer = (PrismContainerWrapperImpl) outboundValue.getParent();
                if (attributeContainer != null &&
                        ResourceAttributeDefinitionType.class.equals(attributeContainer.getCompileTimeClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    public ConstructionType getConstruction() {
        if (getParent() == null) {
            return construction;
        }

        if (construction == null) {
            PrismContainerWrapperImpl outboundContainer = getParent().getParent();
            if (outboundContainer == null) {
                return construction;
            }

            PrismContainerValueWrapperImpl outboundValue = (PrismContainerValueWrapperImpl) outboundContainer.getParent();
            if (outboundValue == null) {
                return construction;
            }

            PrismContainerWrapperImpl associationContainer = (PrismContainerWrapperImpl) outboundValue.getParent();
            if (associationContainer != null) {
                PrismContainerValueWrapperImpl constructionContainer = (PrismContainerValueWrapperImpl) associationContainer.getParent();
                if (constructionContainer != null && constructionContainer.getRealValue() instanceof ConstructionType) {
                    construction = (ConstructionType) constructionContainer.getRealValue();
                }
            }

        }

        return construction;
    }

    public void setConstruction(ConstructionType construction) {
        this.construction = construction;
    }

    @Override
    public Integer getDisplayOrder() {
        if (isAssociationExpression() || isAttributeExpression()) {
            //todo MAX_VALUE doesn't guarantee that expression property
            //will be displayed the last, as further there will be properties
            //without any displayOrder displayed
            return Integer.MAX_VALUE;
        } else {
            return super.getDisplayOrder();
        }
    }

    @Override
    public QName getTypeName() {
        return customQName;
    }

}
