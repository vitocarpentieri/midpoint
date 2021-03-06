/*
 * Copyright (c) 2010-2018 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.gui.impl.prism.panel;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

import com.evolveum.midpoint.gui.api.prism.wrapper.*;
import com.evolveum.midpoint.gui.impl.factory.panel.ItemPanelContext;
import com.evolveum.midpoint.gui.impl.factory.panel.PrismContainerPanelContext;
import com.evolveum.midpoint.gui.impl.prism.panel.component.ListContainersPopup;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismContainerValueWrapper;
import com.evolveum.midpoint.gui.api.prism.wrapper.ResourceAttributeWrapper;
import com.evolveum.midpoint.prism.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LambdaModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

import com.evolveum.midpoint.gui.api.GuiStyleConstants;
import com.evolveum.midpoint.gui.api.component.togglebutton.ToggleIconButton;
import com.evolveum.midpoint.gui.api.util.WebModelServiceUtils;
import com.evolveum.midpoint.gui.api.factory.wrapper.WrapperContext;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.exception.SystemException;
import com.evolveum.midpoint.web.component.AjaxButton;
import com.evolveum.midpoint.web.component.prism.ValueStatus;
import com.evolveum.midpoint.web.component.util.VisibleBehaviour;
import com.evolveum.midpoint.web.component.util.VisibleEnableBehaviour;
import com.evolveum.midpoint.web.util.InfoTooltipBehavior;

/**
 * @author katka
 *
 */
public class PrismContainerValuePanel<C extends Containerable, CVW extends PrismContainerValueWrapper<C>> extends PrismValuePanel<C, PrismContainerWrapper<C>, CVW> {

    private static final long serialVersionUID = 1L;

    protected static final String ID_LABEL = "label";
    protected static final String ID_LABEL_CONTAINER = "labelContainer";
    protected static final String ID_HELP = "help";


    private static final String ID_SORT_PROPERTIES = "sortProperties";
    private static final String ID_SHOW_METADATA = "showMetadata";
    private static final String ID_ADD_CHILD_CONTAINER = "addChildContainer";
    private static final String ID_REMOVE_CONTAINER = "removeContainer";

    private static final String ID_EXPAND_COLLAPSE_BUTTON = "expandCollapseButton";
    private static final String ID_PROPERTIES_LABEL = "propertiesLabel";
    private static final String ID_CONTAINERS_LABEL = "containersLabel";
    private static final String ID_SHOW_EMPTY_BUTTON = "showEmptyButton";


    public PrismContainerValuePanel(String id, IModel<CVW> model, ItemPanelSettings settings) {
        super(id, model, settings);
    }

    @Override
    protected <PC extends ItemPanelContext> PC createPanelCtx(IModel<PrismContainerWrapper<C>> wrapper) {
        return (PC) new PrismContainerPanelContext<C>(wrapper);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(AttributeModifier.append("class", () -> {
            String cssClasses = "";
            if (getModelObject() != null && ValueStatus.ADDED == getModelObject().getStatus()) {
                cssClasses = " added-value-background";
            }
            if (getModelObject() != null && ValueStatus.DELETED == getModelObject().getStatus()) {
                cssClasses = " removed-value-background";
            }
            return cssClasses;
        }));

    }

    @Override
    protected void addToHeader(WebMarkupContainer header) {
        WebMarkupContainer labelContainer = new WebMarkupContainer(ID_LABEL_CONTAINER);
        labelContainer.setOutputMarkupId(true);

        header.add(labelContainer);

        LoadableDetachableModel<String> headerLabelModel = getLabelModel();
        AjaxButton labelComponent = new AjaxButton(ID_LABEL, headerLabelModel) {
            private static final long serialVersionUID = 1L;
            @Override
            public void onClick(AjaxRequestTarget target) {
                onExpandClick(target);
            }
        };
        labelComponent.setOutputMarkupId(true);
        labelComponent.setOutputMarkupPlaceholderTag(true);
        labelComponent.add(AttributeAppender.append("style", "cursor: pointer;"));
        labelContainer.add(labelComponent);

        labelContainer.add(getHelpLabel());

        initButtons(header);

        //TODO always visible if isObject
    }

    protected LoadableDetachableModel<String> getLabelModel() {
        return getPageBase().createStringResource("${displayName}", getModel());
    }

    @Override
    protected Component createDefaultPanel(String id) {
        WebMarkupContainer defaultPanel = new WebMarkupContainer(id);
        defaultPanel.add(createNonContainersPanel());

        defaultPanel.add(createContainersPanel());
        return defaultPanel;
    }

    @Override
    protected <PV extends PrismValue> PV createNewValue(PrismContainerWrapper<C> itemWrapper) {
        return (PV) itemWrapper.getItem().createNewValue();
    }

    private <IW extends ItemWrapper<?,?>> WebMarkupContainer createNonContainersPanel() {
        WebMarkupContainer propertiesLabel = new WebMarkupContainer(ID_PROPERTIES_LABEL);
        propertiesLabel.setOutputMarkupId(true);

        IModel<List<IW>> nonContainerWrappers = createNonContainerWrappersModel();

        ListView<IW> properties = new ListView<IW>("properties", nonContainerWrappers) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<IW> item) {
                populateNonContainer(item);
            }
        };
        properties.setOutputMarkupId(true);
        add(propertiesLabel);
        propertiesLabel.add(properties);

        AjaxButton labelShowEmpty = new AjaxButton(ID_SHOW_EMPTY_BUTTON) {
            private static final long serialVersionUID = 1L;
            @Override
            public void onClick(AjaxRequestTarget target) {
                onShowEmptyClick(target);
            }

            @Override
            public IModel<?> getBody() {
                return getNameOfShowEmptyButton();
            }
        };
        labelShowEmpty.setOutputMarkupId(true);
        labelShowEmpty.add(AttributeAppender.append("style", "cursor: pointer;"));
        labelShowEmpty.add(new VisibleEnableBehaviour() {

            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible() {
                return nonContainerWrappers.getObject() != null && !nonContainerWrappers.getObject().isEmpty()
                        && getModelObject().isExpanded();// && !model.getObject().isShowEmpty();
            }
        });
        propertiesLabel.add(labelShowEmpty);
        return propertiesLabel;
    }

    private WebMarkupContainer createContainersPanel() {
        WebMarkupContainer containersLable = new WebMarkupContainer(ID_CONTAINERS_LABEL);
        add(containersLable);
        ListView<PrismContainerWrapper<?>> containers = new ListView<PrismContainerWrapper<?>>("containers", new PropertyModel<>(getModel(), "containers")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<PrismContainerWrapper<?>> item) {
                populateContainer(item);
            }
        };

        containers.setReuseItems(true);
        containers.setOutputMarkupId(true);
        containersLable.add(containers);
        return containersLable;

    }

    private <IW extends ItemWrapper<?,?>> IModel<List<IW>> createNonContainerWrappersModel() {
        return new IModel<List<IW>>() {

            private static final long serialVersionUID = 1L;

            @Override
            public List<IW> getObject() {
                return getNonContainerWrappers();
            }
        };
    }

    private <IW extends ItemWrapper<?,?>> List<IW> getNonContainerWrappers() {
        CVW containerValueWrapper = getModelObject();
        List<? extends ItemWrapper<?, ?>> nonContainers = containerValueWrapper.getNonContainers();

        Locale locale = WebModelServiceUtils.getLocale();
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Collator collator = Collator.getInstance(locale);
        collator.setStrength(Collator.SECONDARY);       // e.g. "a" should be different from "á"
        collator.setDecomposition(Collator.FULL_DECOMPOSITION);
        ItemWrapperComparator<?> comparator = new ItemWrapperComparator<>(collator, getModelObject().isSorted());
        if (CollectionUtils.isNotEmpty(nonContainers)) {
            nonContainers.sort((Comparator) comparator);

            int visibleProperties = 0;

            for (ItemWrapper<?,?> item : nonContainers) {
                if (item.isVisible(getModelObject(), getVisibilityHandler())) {
                    visibleProperties++;
                }

                if (visibleProperties % 2 == 0) {
                    item.setStripe(false);
                } else {
                    item.setStripe(true);
                }

            }
        }

        return (List<IW>) nonContainers;
    }

    private <IW extends ItemWrapper<?,?>> void populateNonContainer(ListItem<IW> item) {
        item.setOutputMarkupId(true);
        IW itemWrapper = item.getModelObject();
        try {
            QName typeName = itemWrapper.getTypeName();
            if(item.getModelObject() instanceof ResourceAttributeWrapper) {
                typeName = new QName("ResourceAttributeDefinition");
            }

            Panel panel = getPageBase().initItemPanel("property", typeName, item.getModel(), getSettings().copy());
            panel.setOutputMarkupId(true);
            panel.add(AttributeModifier.append("class", appendStyleClassModel(item.getModel())));
            panel.add(new VisibleEnableBehaviour() {
                @Override
                public boolean isVisible() {
                    return itemWrapper.isVisible(getModelObject(), getVisibilityHandler());
                }

                @Override
                public boolean isEnabled() {
                    return !itemWrapper.isReadOnly();
                }
            });
            item.add(panel);
        } catch (SchemaException e1) {
            throw new SystemException("Cannot instantiate " + itemWrapper.getTypeName());
        }
    }

    private void populateContainer(ListItem<PrismContainerWrapper<?>> container) {
        PrismContainerWrapper<?> itemWrapper = container.getModelObject();
        try {
            Panel panel = getPageBase().initItemPanel("container", itemWrapper.getTypeName(), container.getModel(), getSettings().copy());
            panel.setOutputMarkupId(true);
            panel.add(new VisibleEnableBehaviour() {
                @Override
                public boolean isVisible() {
                    return itemWrapper.isVisible(getModelObject(), getVisibilityHandler());
                }

                @Override
                public boolean isEnabled() {
                    return !itemWrapper.isReadOnly();
                }
            });
            container.add(panel);
        } catch (SchemaException e) {
            throw new SystemException("Cannot instantiate panel for: " + itemWrapper.getDisplayName());
        }

    }

     private StringResourceModel getNameOfShowEmptyButton() {
            return getPageBase().createStringResource("ShowEmptyButton.showMore.${showEmpty}", getModel());

        }

        private void onShowEmptyClick(AjaxRequestTarget target) {

            CVW wrapper = getModelObject();
            wrapper.setShowEmpty(!wrapper.isShowEmpty());
            refreshPanel(target);
        }

    private <IW extends ItemWrapper<?,?>> IModel<String> appendStyleClassModel(final IModel<IW> wrapper) {
        return new IModel<String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getObject() {
                ItemWrapper<?, ?> property = wrapper.getObject();
                return property.isStripe() ? "stripe" : null;
            }
        };
    }

    private void initButtons(WebMarkupContainer header) {
        header.add(createExpandCollapseButton());
//        header.add(createMetadataButton());
        header.add(createSortButton());
        header.add(createAddMoreButton());
    }

    private void onExpandClick(AjaxRequestTarget target) {

        CVW wrapper = getModelObject();
        wrapper.setExpanded(!wrapper.isExpanded());
        refreshPanel(target);
    }

    protected Label getHelpLabel() {

        Label help = new Label(ID_HELP);
        help.add(AttributeModifier.replace("title", LambdaModel.of(getModel(), PrismContainerValueWrapper::getHelpText)));
        help.add(new InfoTooltipBehavior());
        help.add(new VisibleBehaviour(() -> StringUtils.isNotEmpty(getModelObject().getHelpText())));
        help.setOutputMarkupId(true);
        return help;
    }

//    private ToggleIconButton<String> createMetadataButton() {
//        ToggleIconButton<String> showMetadataButton = new ToggleIconButton<String>(ID_SHOW_METADATA,
//                GuiStyleConstants.CLASS_ICON_SHOW_METADATA, GuiStyleConstants.CLASS_ICON_SHOW_METADATA) {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public void onClick(AjaxRequestTarget target) {
//                onShowMetadataClicked(target);
//            }
//
//            @Override
//            public boolean isOn() {
//                return PrismContainerValuePanel.this.getModelObject().isShowMetadata();
//            }
//
//
//        };
//        showMetadataButton.add(new AttributeModifier("title", new StringResourceModel("PrismContainerValuePanel.showMetadata.${showMetadata}", getModel())));
//        showMetadataButton.add(new VisibleBehaviour(() -> getModelObject().hasMetadata() && shouldBeButtonsShown()));
//        showMetadataButton.setOutputMarkupId(true);
//        showMetadataButton.setOutputMarkupPlaceholderTag(true);
//        return showMetadataButton;
//    }

    private ToggleIconButton<String> createSortButton() {
        ToggleIconButton<String> sortPropertiesButton = new ToggleIconButton<String>(ID_SORT_PROPERTIES,
                GuiStyleConstants.CLASS_ICON_SORT_ALPHA_ASC, GuiStyleConstants.CLASS_ICON_SORT_AMOUNT_ASC) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                onSortClicked(target);
            }

            @Override
            public boolean isOn() {
                return PrismContainerValuePanel.this.getModelObject().isSorted();
            }
        };
        sortPropertiesButton.add(new VisibleBehaviour(() -> shouldBeButtonsShown()));
        sortPropertiesButton.setOutputMarkupId(true);
        sortPropertiesButton.setOutputMarkupPlaceholderTag(true);
        return sortPropertiesButton;
    }

    private AjaxLink createAddMoreButton() {

         AjaxLink<String> addChildContainerButton = new AjaxLink<String>(ID_ADD_CHILD_CONTAINER, new StringResourceModel("PrismContainerValuePanel.addMore")) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    initMoreContainersPopup(target);
                }
            };

            addChildContainerButton.add(new VisibleEnableBehaviour() {

                private static final long serialVersionUID = 1L;

                @Override
                public boolean isEnabled() {
                    if (getModelObject() != null) {
                        if(getModelObject().getParent() != null) {
                            return !getModelObject().getParent().isReadOnly();
                        } else {
                            return !getModelObject().isReadOnly();
                        }
                    }
                    return false;
                }

                @Override
                public boolean isVisible() {
                    return shouldBeButtonsShown() && getModelObject()!= null && getModelObject().isHeterogenous();
                }
            });
            addChildContainerButton.setOutputMarkupId(true);
            addChildContainerButton.setOutputMarkupPlaceholderTag(true);
            return addChildContainerButton;
    }

    private void initMoreContainersPopup(AjaxRequestTarget parentTarget) {


        ListContainersPopup<C, CVW> listContainersPopup = new ListContainersPopup<C, CVW>(getPageBase().getMainPopupBodyId(), getModel()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void processSelectedChildren(AjaxRequestTarget target, List<PrismContainerDefinition<?>> selected) {
                prepareNewContainers(target, selected);
            }

        };
        listContainersPopup.setOutputMarkupId(true);

        getPageBase().showMainPopup(listContainersPopup, parentTarget);
    }

    private void prepareNewContainers(AjaxRequestTarget target, List<PrismContainerDefinition<?>> containers) {
        getPageBase().hideMainPopup(target);

        Task task = getPageBase().createSimpleTask("Create child containers");
        WrapperContext ctx = new WrapperContext(task, task.getResult());
        containers.forEach(container -> {
            try {
                ItemWrapper iw = getPageBase().createItemWrapper(container, getModelObject(), ctx);
                if (iw != null) {
                    ((List) getModelObject().getItems()).add(iw);
                }
            } catch (SchemaException e) {
                OperationResult result = ctx.getResult();
                result.recordFatalError(createStringResource("PrismContainerValuePanel.message.prepareNewContainers.fatalError", container).getString(), e);
                getPageBase().showResult(ctx.getResult());
            }
        });

        refreshPanel(target);

    }

    private boolean shouldBeButtonsShown() {
        return getModelObject().isExpanded();
    }

    private void onSortClicked(AjaxRequestTarget target) {
        CVW wrapper = getModelObject();
        wrapper.setSorted(!wrapper.isSorted());

        refreshPanel(target);
    }

//    private void onShowMetadataClicked(AjaxRequestTarget target) {
//        CVW wrapper = getModelObject();
//        wrapper.setShowMetadata(!wrapper.isShowMetadata());
//        refreshPanel(target);
//    }


    private void refreshPanel(AjaxRequestTarget target) {
        target.add(PrismContainerValuePanel.this);
        target.add(getPageBase().getFeedbackPanel());
    }

    protected ToggleIconButton<?> createExpandCollapseButton() {
        ToggleIconButton<?> expandCollapseButton = new ToggleIconButton<Void>(ID_EXPAND_COLLAPSE_BUTTON,
                GuiStyleConstants.CLASS_ICON_EXPAND_CONTAINER, GuiStyleConstants.CLASS_ICON_COLLAPSE_CONTAINER) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                onExpandClick(target);
            }

            @Override
            public boolean isOn() {
                return PrismContainerValuePanel.this.getModelObject().isExpanded();
            }
        };
        expandCollapseButton.setOutputMarkupId(true);
        return expandCollapseButton;
    }

    @Override
    protected void removeValue(CVW valueToRemove, AjaxRequestTarget target) throws SchemaException {
        throw new UnsupportedOperationException("Must be implemented in calling panel");
    }

    @Override
    protected boolean isRemoveButtonVisible() {
        return super.isRemoveButtonVisible() && getModelObject().isExpanded() && !(getModelObject() instanceof PrismObjectValueWrapper);
    }
}
