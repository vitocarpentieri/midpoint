/*
 * Copyright (C) 2020 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.axiom.api;

import java.util.Optional;

import com.evolveum.axiom.api.schema.AxiomItemDefinition;

public abstract class AbstractAxiomItem<V> implements AxiomItem<V> {


    private final AxiomItemDefinition definition;

    public AbstractAxiomItem(AxiomItemDefinition definition) {
        this.definition = definition;
    }

    @Override
    public Optional<AxiomItemDefinition> definition() {
        return Optional.of(definition);
    }

    @Override
    public AxiomName name() {
        return definition.name();
    }
}
