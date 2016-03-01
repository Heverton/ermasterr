package org.insightech.er.preference.page.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.insightech.er.ERDiagramActivator;
import org.insightech.er.ResourceString;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.db.DBManager;
import org.insightech.er.db.DBManagerFactory;
import org.insightech.er.editor.model.settings.JDBCDriverSetting;
import org.insightech.er.preference.PreferenceInitializer;
import org.insightech.er.util.Format;

public class JDBCPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private Table table;

    private Button addButton;

    private Button editButton;

    private Button deleteButton;

    @Override
    public void init(final IWorkbench workbench) {}

    @Override
    protected Control createContents(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;

        composite.setLayout(gridLayout);

        initTable(composite);
        createButton(composite);
        addListener();

        return composite;
    }

    private void initTable(final Composite parent) {
        table = new Table(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);

        final GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.heightHint = 400;
        gridData.horizontalSpan = 3;

        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        final TableColumn nameColumn = new TableColumn(table, SWT.NONE);
        nameColumn.setText(ResourceString.getResourceString("label.database"));
        nameColumn.setWidth(200);

        final TableColumn driverClassNameColumn = new TableColumn(table, SWT.NONE);
        driverClassNameColumn.setText(ResourceString.getResourceString("label.driver.class.name"));
        driverClassNameColumn.setWidth(200);

        final TableColumn pathColumn = new TableColumn(table, SWT.NONE);
        pathColumn.setText(ResourceString.getResourceString("label.path"));
        pathColumn.setWidth(200);

        setData();
    }

    private void createButton(final Composite parent) {
        addButton = CompositeFactory.createSmallButton(parent, "label.button.add");
        editButton = CompositeFactory.createSmallButton(parent, "label.button.edit");
        deleteButton = CompositeFactory.createSmallButton(parent, "label.button.delete");

        deleteButton.setEnabled(false);
    }

    private void setData() {
        table.removeAll();

        for (final JDBCDriverSetting setting : PreferenceInitializer.getJDBCDriverSettingList()) {
            final TableItem tableItem = new TableItem(table, SWT.NONE);
            tableItem.setBackground(ColorConstants.white);
            tableItem.setText(0, Format.null2blank(setting.getDb()));
            tableItem.setText(1, Format.null2blank(setting.getClassName()));
            tableItem.setText(2, Format.null2blank(setting.getPath()));
        }
    }

    @Override
    protected void performDefaults() {
        PreferenceInitializer.clearJDBCDriverInfo();

        setData();

        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        PreferenceInitializer.clearJDBCDriverInfo();

        for (int i = 0; i < table.getItemCount(); i++) {
            final TableItem tableItem = table.getItem(i);

            final String db = tableItem.getText(0);
            final String driverClassName = tableItem.getText(1);
            final String path = tableItem.getText(2);

            PreferenceInitializer.addJDBCDriver(db, driverClassName, path);
        }

        return super.performOk();
    }

    private void addListener() {
        table.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent selectionevent) {
                final int index = table.getSelectionIndex();
                if (index == -1) {
                    return;
                }

                final TableItem item = table.getItem(index);

                final String db = item.getText(0);
                final String driverClassName = item.getText(1);

                final DBManager dbManager = DBManagerFactory.getDBManager(db);

                if (!dbManager.getDriverClassName().equals(driverClassName)) {
                    deleteButton.setEnabled(true);

                } else {
                    deleteButton.setEnabled(false);
                }
            }

        });

        table.addMouseListener(new MouseAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void mouseDoubleClick(final MouseEvent e) {
                edit();
            }
        });

        addButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final List<JDBCDriverSetting> otherDriverSettingList = getOtherDriverSettingList(-1);

                final JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), null, null, null, otherDriverSettingList, true);

                if (dialog.open() == IDialogConstants.OK_ID) {
                    PreferenceInitializer.addJDBCDriver(dialog.getDatabase(), Format.null2blank(dialog.getDriverClassName()), Format.null2blank(dialog.getPath()));

                    setData();
                }
            }

        });

        editButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                edit();
            }

        });

        deleteButton.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(final SelectionEvent event) {
                try {
                    final int index = table.getSelectionIndex();

                    if (index == -1) {
                        return;
                    }

                    final TableItem item = table.getItem(index);

                    final String db = item.getText(0);
                    final String driverClassName = item.getText(1);

                    final DBManager dbManager = DBManagerFactory.getDBManager(db);

                    if (!dbManager.getDriverClassName().equals(driverClassName)) {
                        table.remove(index);
                    }

                } catch (final Exception e) {
                    ERDiagramActivator.showExceptionDialog(e);
                }
            }
        });
    }

    private List<JDBCDriverSetting> getOtherDriverSettingList(final int index) {
        final List<JDBCDriverSetting> list = new ArrayList<JDBCDriverSetting>();

        for (int i = 0; i < table.getItemCount(); i++) {
            if (i != index) {
                final TableItem tableItem = table.getItem(i);

                final String db = tableItem.getText(0);
                final String driverClassName = tableItem.getText(1);
                final String path = tableItem.getText(2);

                final JDBCDriverSetting driverSetting = new JDBCDriverSetting(db, driverClassName, path);
                list.add(driverSetting);
            }
        }

        return list;
    }

    private void edit() {
        try {
            final int index = table.getSelectionIndex();
            if (index == -1) {
                return;
            }

            final TableItem item = table.getItem(index);

            final List<JDBCDriverSetting> otherDriverSettingList = getOtherDriverSettingList(index);

            final JDBCPathDialog dialog = new JDBCPathDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), item.getText(0), item.getText(1), item.getText(2), otherDriverSettingList, true);

            if (dialog.open() == IDialogConstants.OK_ID) {
                item.setText(1, dialog.getDriverClassName());
                item.setText(2, dialog.getPath());
            }

        } catch (final Exception e) {
            ERDiagramActivator.showExceptionDialog(e);
        }
    }
}
