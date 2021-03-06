/*
 * Copyright (C) 2020 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.axiom.lang.impl;

import com.evolveum.axiom.api.AxiomName;
import com.evolveum.axiom.api.AxiomItem;
import com.evolveum.axiom.api.AxiomValue;
import com.evolveum.axiom.api.schema.AxiomItemDefinition;
import com.evolveum.axiom.api.schema.AxiomIdentifierDefinition.Scope;
import com.evolveum.axiom.lang.api.IdentifierSpaceKey;
import com.evolveum.axiom.lang.impl.AxiomStatementRule.ActionBuilder;

public interface AxiomValueContext<V> {

    void replace(AxiomValue<?> axiomItemValue);

    default <T> AxiomItemContext<T> childItem(AxiomItemDefinition def) {
        return childItem(def.name());
    }

    <T> AxiomItemContext<T> childItem(AxiomName name);

    V currentValue();

    AxiomItemContext<V> parent();

    void mergeItem(AxiomItem<?> axiomItem);

    void register(AxiomName space, Scope scope, IdentifierSpaceKey key);

    AxiomRootContext root();

    ActionBuilder<?> newAction(String name);

    default AxiomValueContext<?> parentValue() {
        return parent().parent();
    }

    void replaceValue(V object);

    <V> AxiomValueReference<V> asReference();

    /*V requireValue() throws AxiomSemanticException;

    AxiomItemDefinition definition();

    <V> AxiomValueContext<V> createEffectiveChild(AxiomIdentifier axiomIdentifier, V value);

    Optional<V> optionalValue();

    void replace(Dependency<AxiomItemValue<?>> statement);

    AxiomValueContext<?> parent();

    void register(AxiomIdentifier space, Scope scope, IdentifierSpaceKey key);

    V requireValue(Class<V> type);


    void importIdentifierSpace(NamespaceContext namespaceContext);

    void exportIdentifierSpace(IdentifierSpaceKey namespace);

    void mergeItem(AxiomItem<?> children);

    AxiomStatementRule.Context<?> newAction(String actionName);

    AxiomStatementRule.Context<?> modify(AxiomValueContext<?> target, String actionName);

    void mergeEffectiveItemValues(AxiomItem<?> axiomItem); */

}
