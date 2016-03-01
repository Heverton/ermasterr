package org.insightech.er.db.impl.db2.tablespace;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.insightech.er.common.widgets.CompositeFactory;
import org.insightech.er.editor.model.diagram_contents.not_element.tablespace.TablespaceProperties;
import org.insightech.er.editor.view.dialog.outline.tablespace.TablespaceDialog;
import org.insightech.er.util.Format;

public class DB2TablespaceDialog extends TablespaceDialog {

    // (REGULAR/LARGI/SYSTEM TEMPORARY/USER TEMPORARY)
    private Text type;

    private Text pageSize;

    private Text managedBy;

    private Text container;

    // private Text containerDirectoryPath;
    //
    // private Text containerFilePath;
    //
    // private Text containerPageNum;
    //
    // private Text containerDevicePath;

    private Text extentSize;

    private Text prefetchSize;

    private Text bufferPoolName;

    @Override
    protected void initialize(final Composite composite) {
        super.initialize(composite);

        type = CompositeFactory.createText(this, composite, "label.tablespace.type", false, false);
        pageSize = CompositeFactory.createText(this, composite, "label.tablespace.page.size", false, false);
        managedBy = CompositeFactory.createText(this, composite, "label.tablespace.managed.by", false, false);
        container = CompositeFactory.createText(this, composite, "label.tablespace.container", false, false);
        // this.containerDirectoryPath = this.createText(composite,
        // "label.tablespace.container.directory.path");
        // this.containerFilePath = this.createText(composite,
        // "label.tablespace.container.file.path");
        // this.containerPageNum = this.createText(composite,
        // "label.tablespace.container.page.num");
        // this.containerDevicePath = this.createText(composite,
        // "label.tablespace.container.device.path");
        extentSize = CompositeFactory.createText(this, composite, "label.tablespace.extent.size", false, false);
        prefetchSize = CompositeFactory.createText(this, composite, "label.tablespace.prefetch.size", false, false);
        bufferPoolName = CompositeFactory.createText(this, composite, "label.tablespace.buffer.pool.name", false, false);
    }

    @Override
    protected TablespaceProperties setTablespaceProperties() {
        final DB2TablespaceProperties tablespaceProperties = new DB2TablespaceProperties();

        tablespaceProperties.setType(type.getText().trim());
        tablespaceProperties.setPageSize(pageSize.getText().trim());
        tablespaceProperties.setManagedBy(managedBy.getText().trim());
        tablespaceProperties.setContainer(container.getText().trim());
        // tablespaceProperties.setContainerDirectoryPath(this.containerDirectoryPath.getText()
        // .trim());
        // tablespaceProperties.setContainerFilePath(this.containerFilePath.getText().trim());
        // tablespaceProperties.setContainerPageNum(this.containerPageNum.getText().trim());
        // tablespaceProperties
        // .setContainerDevicePath(this.containerDevicePath.getText()
        // .trim());
        tablespaceProperties.setExtentSize(extentSize.getText().trim());
        tablespaceProperties.setPrefetchSize(prefetchSize.getText().trim());
        tablespaceProperties.setBufferPoolName(bufferPoolName.getText().trim());

        return tablespaceProperties;
    }

    @Override
    protected void setData(final TablespaceProperties tablespaceProperties) {
        if (tablespaceProperties instanceof DB2TablespaceProperties) {
            final DB2TablespaceProperties properties = (DB2TablespaceProperties) tablespaceProperties;

            type.setText(Format.toString(properties.getType()));
            pageSize.setText(Format.toString(properties.getPageSize()));
            managedBy.setText(Format.toString(properties.getManagedBy()));
            container.setText(Format.toString(properties.getContainer()));
            // this.containerDirectoryPath.setText(Format.toString(properties
            // .getContainerDirectoryPath()));
            // this.containerFilePath.setText(Format.toString(properties
            // .getContainerFilePath()));
            // this.containerPageNum.setText(Format.toString(properties
            // .getContainerPageNum()));
            // this.containerDevicePath.setText(Format.toString(properties
            // .getContainerDevicePath()));
            extentSize.setText(Format.toString(properties.getExtentSize()));
            prefetchSize.setText(Format.toString(properties.getPrefetchSize()));
            bufferPoolName.setText(Format.toString(properties.getBufferPoolName()));
        }
    }

}
