package com.github.calve.test.part.observer.application.client;

import com.github.calve.test.part.observer.application.shared.PartDto;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.*;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.InvalidEvent;
import com.sencha.gxt.widget.core.client.event.InvalidEvent.InvalidHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ValidEvent;
import com.sencha.gxt.widget.core.client.event.ValidEvent.ValidHandler;
import com.sencha.gxt.widget.core.client.form.*;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.BooleanFilter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PagingGrid implements IsWidget {

    private final PartManagementServiceAsync service;
    private ContentPanel panel;
    private long simpleID = -1;
    private PartProperties properties = GWT.create(PartProperties.class);
    private TextField setAvailable = new TextField();
    private TextButton saveButton = new TextButton("Save");

    public PagingGrid() {
        service = GWT.create(PartManagementService.class);
    }

    @Override
    public Widget asWidget() {
        if (panel == null) {

            PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PartDto>> loader = PagingLoaderCreator.getLoader(service);

            ListStore<PartDto> store = new ListStore<>(new ModelKeyProvider<PartDto>() {
                @Override
                public String getKey(PartDto item) {
                    return "" + item.getId();
                }
            });

            loader.useLoadConfig(new FilterPagingLoadConfigBean());
            loader.setRemoteSort(true);
            loader.addLoadHandler(new LoadResultListStoreBinding<>(store));
            loader.addLoadHandler(new LoadHandler<FilterPagingLoadConfig, PagingLoadResult<PartDto>>() {
                @Override
                public void onLoad(LoadEvent<FilterPagingLoadConfig, PagingLoadResult<PartDto>> event) {requestAvailableSets();}
            });

            ColumnConfig<PartDto, String> nameColumn = new ColumnConfig<>(properties.name(), 345, "Name");
            ColumnConfig<PartDto, Integer> quantityColumn = new ColumnConfig<>(properties.quantity(),100, "Quantity");
            ColumnConfig<PartDto, Boolean> requiredColumn = new ColumnConfig<>(properties.required(),35, "Required");
            configurateRequiredColumn(requiredColumn);
            ColumnConfig<PartDto, String> delColumn = new ColumnConfig<>(properties.name(), 20, "X");
            configurateDeleteColumn(store, delColumn, loader);

            List<ColumnConfig<PartDto, ?>> columns = new ArrayList<>();
            columns.add(nameColumn);
            columns.add(quantityColumn);
            columns.add(requiredColumn);
            columns.add(delColumn);

            Grid<PartDto> grid = createGrid(loader, store, columns);
            grid.getView().setAutoExpandColumn(nameColumn);
            grid.getView().setAutoExpandColumn(requiredColumn);

            final PagingToolBar pagingToolBar = new PagingToolBar(10);
            pagingToolBar.setBorders(false);
            pagingToolBar.bind(loader);

            final GridInlineEditing<PartDto> editing = new GridInlineEditing<>(grid);
            TextField nameEditorField = new TextField();
            nameEditorField.addValidator(new Validator<String>() {

                @Override
                public List<EditorError> validate(Editor<String> editor, String value) {
                    List<EditorError> res = null;
                    if (value == null || value.trim().length() == 0) {
                        saveButton.disable();
                        List<EditorError> errors = new ArrayList<>();
                        errors.add(new DefaultEditorError(editor, "Wrong Item name", ""));
                        return errors;
                    }
                    return res;
                }
            });
            handleInvalidField(nameEditorField);
            handlValidField(nameEditorField);

            editing.addEditor(nameColumn, nameEditorField);
            IntegerField quantityEditorField = new IntegerField();
            quantityEditorField.addValidator(new Validator<Integer>() {

                @Override
                public List<EditorError> validate(Editor<Integer> editor, Integer value) {
                    List<EditorError> res = null;
                    if (value == null || value < 0) {
                        List<EditorError> errors = new ArrayList<>();
                        errors.add(new DefaultEditorError(editor, "Wrong number format", ""));
                        return errors;
                    }
                    return res;
                }
            });
            handleInvalidField(quantityEditorField);
            handlValidField(quantityEditorField);
            editing.addEditor(quantityColumn, null, quantityEditorField);
            editing.addEditor(requiredColumn, new CheckBox());

            StringFilter<PartDto> nameFilter = new StringFilter<>(properties.name());
            BooleanFilter<PartDto> booleanFilter = new BooleanFilter<>(properties.required());

            GridFilters<PartDto> filters = new GridFilters<>(loader);
            filters.initPlugin(grid);
            filters.addFilter(nameFilter);
            filters.addFilter(booleanFilter);

            panel = new ContentPanel();

            HorizontalLayoutContainer totalPCsContainer = new HorizontalLayoutContainer();
            totalPCsContainer.add(new TextButton("PCS"));

            VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
            verticalLayoutContainer.add(grid, new VerticalLayoutData(1, 1));
            verticalLayoutContainer.add(pagingToolBar, new VerticalLayoutData(1, -1));

            setAvailable.setText("123");
            setAvailable.setReadOnly(true);
            FieldLabel fieldlabel = new FieldLabel(setAvailable, "Available sets: ");
            verticalLayoutContainer.add(fieldlabel, new VerticalLayoutData(-1, -1, new Margins(5, 5, 3, 3)));

            panel.setHeading("Parts Grid");
            panel.add(verticalLayoutContainer);
            panel.setButtonAlign(BoxLayoutPack.CENTER);
            panel.addButton(new TextButton("Add Item", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    PartDto part = new PartDto();
                    part.setId(--simpleID);
                    part.setName("new Part");
                    part.setQuantity(0);
                    part.setRequired(false);
                    editing.cancelEditing();
                    store.add(0, part);
                    int row = store.indexOf(part);
                    editing.startEditing(new GridCell(row, 0));
                }
            }));
            panel.addButton(new TextButton("Reset", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    store.rejectChanges();
                }
            }));
            saveButton.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    store.commitChanges();
                    service.updateServerData(getLayerRecords(store), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable caught) {Info.display("Error", caught.getMessage());}
                        @Override
                        public void onSuccess(Void result) {
                            loader.load();
                        }
                    });
                }
            });
            panel.addButton(saveButton);
        }
        return panel;
    }

    private void handlValidField(Field<?> field) {
        field.addValidHandler(new ValidHandler() {
            @Override
            public void onValid(ValidEvent event) {
                saveButton.enable();
            }
        });
    }

    private void handleInvalidField(Field<?> field) {
        field.addInvalidHandler(new InvalidHandler() {
            @Override
            public void onInvalid(InvalidEvent event) {
                saveButton.disable();
            }
        });
    }

    private void requestAvailableSets() {
        service.refreshRequiredSet(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                setAvailable.setText(result);
            }
            @Override
            public void onFailure(Throwable caught) {
                Info.display("Error", caught.getMessage());
            }
        });
    }

    private Grid<PartDto> createGrid(PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PartDto>> loader,
                                     ListStore<PartDto> store, List<ColumnConfig<PartDto, ?>> columns) {
        ColumnModel<PartDto> cm = new ColumnModel<>(columns);

        Grid<PartDto> grid = new Grid<PartDto>(store, cm) {
            @Override
            protected void onAfterFirstAttach() {
                super.onAfterFirstAttach();
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        loader.load();
                    }
                });
            }
        };
        grid.setLoadMask(true);
        grid.setLoader(loader);
        grid.setColumnReordering(true);
        grid.setSelectionModel(new CellSelectionModel<>());
        grid.getView().setStripeRows(true);
        grid.getView().setColumnLines(true);
        return grid;
    }

    private void configurateDeleteColumn(ListStore<PartDto> store, ColumnConfig<PartDto, String> delColumn, PagingLoader<FilterPagingLoadConfig, PagingLoadResult<PartDto>> loader) {
        SafeStyles btnPaddingStyle = SafeStylesUtils.fromTrustedString("padding: 0px 0px 0px;");
        delColumn.setColumnTextClassName(CommonStyles.get().inlineBlock());
        delColumn.setResizable(false);
        delColumn.setColumnTextStyle(btnPaddingStyle);
        TextButtonCell button = new TextButtonCell() {
            @Override
            public boolean handlesSelection() {
                return false;
            }
        };
        button.setText("X");
        button.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                Context c = event.getContext();
                int rowOnDelete = c.getIndex();
                PartDto temp = store.get(rowOnDelete);

                final Dialog delConfirmDialog = new Dialog();
                delConfirmDialog.getButtonBar().clear();
                delConfirmDialog.setHeading("Dialog — Simple");
                delConfirmDialog.setWidth(300);
                delConfirmDialog.setHeight(100);
                delConfirmDialog.setResizable(false);
                delConfirmDialog.setHideOnButtonClick(true);
                delConfirmDialog.setBodyStyleName("pad-text");
                delConfirmDialog.getBody().addClassName("pad-text");
                delConfirmDialog.add(new Label("Delete record from Data base?"));
                delConfirmDialog.show();

                delConfirmDialog.addButton(new TextButton("Delete", new SelectHandler() {
                    @Override
                    public void onSelect(SelectEvent event) {
                        store.remove(rowOnDelete);
                        store.commitChanges();
                        delConfirmDialog.hide();
                        if (temp.getId() >= 0)
                            service.deletePart(temp, new AsyncCallback<Void>() {
                                @Override
                                public void onFailure(Throwable caught) {}
                                @Override
                                public void onSuccess(Void result) {
                                    loader.load();
                                }
                            });
                    }
                }));
                delConfirmDialog.addButton(new TextButton("Cancel", new SelectHandler() {
                    @Override
                    public void onSelect(SelectEvent event) {
                        delConfirmDialog.hide();
                    }
                }));
            }
        });
        delColumn.setCell(button);
    }

    private void configurateRequiredColumn(ColumnConfig<PartDto, Boolean> requiredColumn) {
        requiredColumn.setResizable(false);
        requiredColumn.setCell(new SimpleSafeHtmlCell<>(new AbstractSafeHtmlRenderer<Boolean>() {
            @Override
            public SafeHtml render(Boolean object) {
                return SafeHtmlUtils.fromTrustedString(object ? "Yes" : "No");
            }
        }));
        requiredColumn.setComparator(new Comparator<Boolean>() {
            @Override
            public int compare(Boolean o1, Boolean o2) {
                return o1.equals(o2) ? 0 : o1.equals(true) ? -1 : 1;
            }
        });
    }

    private List<PartDto> getLayerRecords(ListStore<PartDto> store) {
        List<PartDto> recordsList = new ArrayList<>();
        recordsList.addAll(store.getAll());
        return recordsList;
    }
}