/*******************************************************************************
 * Trombone is a flexible text processing and analysis library used
 * primarily by Voyant Tools (voyant-tools.org).
 * 
 * Copyright (©) 2007-2012 Stéfan Sinclair & Geoffrey Rockwell
 * 
 * This file is part of Trombone.
 * 
 * Trombone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Trombone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Trombone.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.voyanttools.trombone.tool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.voyanttools.trombone.document.StoredDocumentSource;
import org.voyanttools.trombone.input.source.InputSource;
import org.voyanttools.trombone.input.source.InputSourcesBuilder;
import org.voyanttools.trombone.storage.Storage;
import org.voyanttools.trombone.storage.StoredDocumentSourceStorage;
import org.voyanttools.trombone.util.FlexibleParameters;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * @author sgs
 *
 */
@XStreamAlias("storedDocuments")
public class DocumentStorer extends AbstractTool {

	//@XStreamImplicit(itemFieldName="id")
	private List<String> ids = new ArrayList<String>();
	
	@XStreamOmitField
	private List<StoredDocumentSource> storedDocumentSources = new ArrayList<StoredDocumentSource>();

	/**
	 * @param parameters
	 */
	public DocumentStorer(Storage storage, FlexibleParameters parameters) {
		super(storage, parameters);
	}

	@Override
	public void run() throws IOException {
		InputSourcesBuilder inputSourcesBuilder = new InputSourcesBuilder(parameters);
		List<InputSource> inputSources = inputSourcesBuilder.getInputSources();
		
		// make sure that all input sources are stored
		StoredDocumentSourceStorage storedDocumentStorage = storage.getStoredDocumentSourceStorage();
		for (InputSource inputSource : inputSources) {
			StoredDocumentSource storedDocumentSource = storedDocumentStorage.getStoredDocumentSource(inputSource);
			storedDocumentSources.add(storedDocumentSource);
			ids.add(storedDocumentSource.getId());
		}

	}

	public List<StoredDocumentSource> getStoredDocumentSources() {
		return storedDocumentSources;
	}
	
}