package org.insightech.er.editor.persistent.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.insightech.er.editor.model.ERDiagram;
import org.insightech.er.editor.persistent.Persistent;

public class PersistentSerializeImpl extends Persistent {

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream createInputStream(final ERDiagram diagram) throws IOException {
        InputStream inputStream = null;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        final ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(diagram);
        oos.close();

        inputStream = new ByteArrayInputStream(out.toByteArray());

        return inputStream;
    }

    @Override
    public ERDiagram load(final InputStream in, final File file) throws Exception {
        return (ERDiagram) ((ObjectInputStream) in).readObject();
    }

}
