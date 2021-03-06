/***
 * Copyright 2013 Teoti Graphix, LLC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * 
 * @author Michael Schmalle <mschmalle@teotigraphix.com>
 */

package randori.compiler.bundle.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import randori.compiler.bundle.IBundle;
import randori.compiler.bundle.IBundleCategory;
import randori.compiler.bundle.IBundleContainer;
import randori.compiler.bundle.IBundleEntry;
import randori.compiler.bundle.IBundleFileEntry;
import randori.compiler.bundle.IBundleLibrary;

/**
 * @author Michael Schmalle
 */
public class BundleWriter extends BundleWriterBase
{
    private final ZipOutputStream zipOutputStream;

    public BundleWriter(final File bundleFile) throws FileNotFoundException
    {
        File outputDirectory = bundleFile.getParentFile();
        outputDirectory.mkdirs();

        if (!outputDirectory.exists())
            throw new FileNotFoundException("Output directory not found: "
                    + outputDirectory.getAbsolutePath());

        zipOutputStream = new ZipOutputStream(new BufferedOutputStream(
                new FileOutputStream(bundleFile)));
        zipOutputStream.setLevel(Deflater.NO_COMPRESSION);
    }

    /**
     * Create a {@link IBundle}.
     * 
     * @param fileName path to write the file to.
     */
    public BundleWriter(final String fileName) throws FileNotFoundException
    {
        this(new File(fileName));
    }

    @Override
    void prepare(IBundle bundle) throws IOException
    {
    }

    @Override
    void writeManifest(IBundle bundle) throws IOException
    {
        zipOutputStream.putNextEntry(new ZipEntry(MANIFEST_XML));
        final Writer writer = new OutputStreamWriter(zipOutputStream);
        writeManifestXML(bundle, writer);
        writer.flush();
        zipOutputStream.closeEntry();
    }

    @Override
    void writeLibrary(IBundleLibrary library) throws IOException
    {
        for (IBundleContainer container : library.getContainers())
        {
            for (IBundleCategory entry : container.getCategories())
            {
                for (IBundleEntry element : entry.getEntries())
                {
                    writeFile(element.getFile(), element.getPath());
                }
            }
        }
    }

    @Override
    void writeFile(File file, String path) throws IOException
    {
        zipOutputStream.putNextEntry(new ZipEntry(path));
        final InputStream fileInputStream = new FileInputStream(file);
        IOUtils.copy(fileInputStream, zipOutputStream);
        fileInputStream.close();
        zipOutputStream.closeEntry();
    }

    @Override
    void writeFile(IBundleFileEntry entry) throws IOException
    {
        zipOutputStream.putNextEntry(new ZipEntry(entry.getPath()));
        final InputStream fileInputStream = entry.createInputStream();
        IOUtils.copy(fileInputStream, zipOutputStream);
        fileInputStream.close();
        zipOutputStream.closeEntry();
    }

    @Override
    void finish(IBundle bundle) throws IOException
    {
        zipOutputStream.flush();
        zipOutputStream.close();
    }
}
