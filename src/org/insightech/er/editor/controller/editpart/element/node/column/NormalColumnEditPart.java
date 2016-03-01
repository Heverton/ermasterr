package org.insightech.er.editor.controller.editpart.element.node.column;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.insightech.er.editor.controller.editpart.element.node.TableViewEditPart;
import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.model.diagram_contents.element.connection.ConnectionElement;
import org.insightech.er.editor.model.diagram_contents.element.connection.Relation;
import org.insightech.er.editor.model.diagram_contents.element.node.table.ERTable;
import org.insightech.er.editor.model.diagram_contents.element.node.table.TableView;
import org.insightech.er.editor.model.diagram_contents.element.node.table.column.NormalColumn;
import org.insightech.er.editor.model.diagram_contents.not_element.group.ColumnGroup;
import org.insightech.er.editor.model.settings.Settings;
import org.insightech.er.editor.model.tracking.UpdatedNodeElement;
import org.insightech.er.editor.view.figure.table.TableFigure;
import org.insightech.er.editor.view.figure.table.column.NormalColumnFigure;
import org.insightech.er.util.Format;

public class NormalColumnEditPart extends ColumnEditPart {

    private boolean selected;

    @Override
    protected IFigure createFigure() {
        final NormalColumnFigure figure = new NormalColumnFigure();
        return figure;
    }

    @Override
    public void refreshTableColumns(final UpdatedNodeElement updated) {
        final ERDiagram diagram = getDiagram();

        final NormalColumnFigure columnFigure = (NormalColumnFigure) getFigure();

        final NormalColumn normalColumn = (NormalColumn) getModel();

        final TableViewEditPart parent = (TableViewEditPart) getParent();
        parent.getContentPane().add(figure);

        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

        if (notationLevel != Settings.NOTATION_LEVLE_TITLE) {
            final TableFigure tableFigure = (TableFigure) parent.getFigure();

            final List<NormalColumn> selectedReferencedColulmnList = getSelectedReferencedColulmnList();
            final List<NormalColumn> selectedForeignKeyColulmnList = getSelectedForeignKeyColulmnList();

            final boolean isSelectedReferenced = selectedReferencedColulmnList.contains(normalColumn);
            final boolean isSelectedForeignKey = selectedForeignKeyColulmnList.contains(normalColumn);

            boolean isAdded = false;
            boolean isUpdated = false;
            if (updated != null) {
                isAdded = updated.isAdded(normalColumn);
                isUpdated = updated.isUpdated(normalColumn);
            }

            if ((notationLevel == Settings.NOTATION_LEVLE_KEY) && !normalColumn.isPrimaryKey() && !normalColumn.isForeignKey() && !normalColumn.isReferedStrictly()) {
                columnFigure.clearLabel();
                return;
            }

            addColumnFigure(diagram, tableFigure, columnFigure, normalColumn, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, false);

            if (selected) {
                columnFigure.setBackgroundColor(ColorConstants.titleBackground);
                columnFigure.setForegroundColor(ColorConstants.titleForeground);
            }

        } else {
            columnFigure.clearLabel();
            return;
        }
    }

    public static void addColumnFigure(final ERDiagram diagram, final TableFigure tableFigure, final NormalColumnFigure columnFigure, final NormalColumn normalColumn, final boolean isSelectedReferenced, final boolean isSelectedForeignKey, final boolean isAdded, final boolean isUpdated, final boolean isRemoved) {
        final int notationLevel = diagram.getDiagramContents().getSettings().getNotationLevel();

        final String type = diagram.filter(Format.formatType(normalColumn.getType(), normalColumn.getTypeData(), diagram.getDatabase(), true));

        boolean displayKey = true;
        if (notationLevel == Settings.NOTATION_LEVLE_COLUMN) {
            displayKey = false;
        }

        boolean displayDetail = false;
        if (notationLevel == Settings.NOTATION_LEVLE_KEY || notationLevel == Settings.NOTATION_LEVLE_EXCLUDE_TYPE || notationLevel == Settings.NOTATION_LEVLE_DETAIL) {
            displayDetail = true;
        }

        boolean displayType = false;
        if (notationLevel == Settings.NOTATION_LEVLE_DETAIL) {
            displayType = true;
        }

        tableFigure.addColumn(columnFigure, diagram.getDiagramContents().getSettings().getViewMode(), diagram.filter(normalColumn.getPhysicalName()), diagram.filter(normalColumn.getLogicalName()), type, normalColumn.isPrimaryKey(), normalColumn.isForeignKey(), normalColumn.isNotNull(), normalColumn.isUniqueKey(), displayKey, displayDetail, displayType, isSelectedReferenced, isSelectedForeignKey, isAdded, isUpdated, isRemoved);
    }

    private List<NormalColumn> getSelectedReferencedColulmnList() {
        final List<NormalColumn> referencedColulmnList = new ArrayList<NormalColumn>();

        final TableViewEditPart parent = (TableViewEditPart) getParent();
        final TableView tableView = (TableView) parent.getModel();

        for (final Object object : parent.getSourceConnections()) {
            final ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;

            final int selected = connectionEditPart.getSelected();

            if (selected == EditPart.SELECTED || selected == EditPart.SELECTED_PRIMARY) {
                final ConnectionElement connectionElement = (ConnectionElement) connectionEditPart.getModel();

                if (connectionElement instanceof Relation) {
                    final Relation relation = (Relation) connectionElement;

                    if (relation.isReferenceForPK()) {
                        referencedColulmnList.addAll(((ERTable) tableView).getPrimaryKeys());

                    } else if (relation.getReferencedComplexUniqueKey() != null) {
                        referencedColulmnList.addAll(relation.getReferencedComplexUniqueKey().getColumnList());

                    } else {
                        referencedColulmnList.add(relation.getReferencedColumn());
                    }
                }
            }

        }
        return referencedColulmnList;
    }

    private List<NormalColumn> getSelectedForeignKeyColulmnList() {
        final List<NormalColumn> foreignKeyColulmnList = new ArrayList<NormalColumn>();

        final TableViewEditPart parent = (TableViewEditPart) getParent();

        for (final Object object : parent.getTargetConnections()) {
            final ConnectionEditPart connectionEditPart = (ConnectionEditPart) object;

            final int selected = connectionEditPart.getSelected();

            if (selected == EditPart.SELECTED || selected == EditPart.SELECTED_PRIMARY) {
                final ConnectionElement connectionElement = (ConnectionElement) connectionEditPart.getModel();

                if (connectionElement instanceof Relation) {
                    final Relation relation = (Relation) connectionElement;

                    foreignKeyColulmnList.addAll(relation.getForeignKeyColumns());
                }
            }
        }

        return foreignKeyColulmnList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelected(final int value) {
        final NormalColumnFigure figure = (NormalColumnFigure) getFigure();

        if (value != 0 && getParent() != null && getParent().getParent() != null) {
            final List selectedEditParts = getViewer().getSelectedEditParts();

            if (selectedEditParts != null && selectedEditParts.size() == 1) {
                final NormalColumn normalColumn = (NormalColumn) getModel();

                if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
                    for (final Object child : getParent().getChildren()) {
                        final AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;

                        final NormalColumn column = (NormalColumn) childEditPart.getModel();
                        if (column.getColumnHolder() == normalColumn.getColumnHolder()) {
                            setGroupColumnFigureColor((TableViewEditPart) getParent(), (ColumnGroup) normalColumn.getColumnHolder(), true);
                        }
                    }

                } else {
                    figure.setBackgroundColor(ColorConstants.titleBackground);
                    figure.setForegroundColor(ColorConstants.titleForeground);
                    selected = true;
                }

                super.setSelected(value);
            }

        } else {
            final NormalColumn normalColumn = (NormalColumn) getModel();

            if (normalColumn.getColumnHolder() instanceof ColumnGroup) {
                for (final Object child : getParent().getChildren()) {
                    final AbstractGraphicalEditPart childEditPart = (AbstractGraphicalEditPart) child;

                    final NormalColumn column = (NormalColumn) childEditPart.getModel();
                    if (column.getColumnHolder() == normalColumn.getColumnHolder()) {
                        setGroupColumnFigureColor((TableViewEditPart) getParent(), (ColumnGroup) normalColumn.getColumnHolder(), false);
                    }
                }

            } else {
                figure.setBackgroundColor(null);
                figure.setForegroundColor(null);
                selected = false;
            }

            super.setSelected(value);
        }

    }

    private void setGroupColumnFigureColor(final TableViewEditPart parentEditPart, final ColumnGroup columnGroup, final boolean selected) {
        for (final NormalColumn column : columnGroup.getColumns()) {
            for (final Object editPart : parentEditPart.getChildren()) {
                final NormalColumnEditPart childEditPart = (NormalColumnEditPart) editPart;
                if (childEditPart.getModel() == column) {
                    final NormalColumnFigure columnFigure = (NormalColumnFigure) childEditPart.getFigure();
                    if (selected) {
                        columnFigure.setBackgroundColor(ColorConstants.titleBackground);
                        columnFigure.setForegroundColor(ColorConstants.titleForeground);

                    } else {
                        columnFigure.setBackgroundColor(null);
                        columnFigure.setForegroundColor(null);
                    }

                    childEditPart.selected = selected;
                    break;
                }
            }
        }
    }
}
