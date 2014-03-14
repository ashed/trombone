package org.voyanttools.trombone.tool;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.junit.Test;
import org.voyanttools.trombone.lucene.LuceneManager;
import org.voyanttools.trombone.model.CorpusTerm;
import org.voyanttools.trombone.storage.Storage;
import org.voyanttools.trombone.storage.memory.MemoryStorage;
import org.voyanttools.trombone.tool.build.RealCorpusCreator;
import org.voyanttools.trombone.util.FlexibleParameters;

public class CorpusTermsTest {

	@Test
	public void test() throws IOException {
		Storage storage = new MemoryStorage();
		Document document;
		LuceneManager luceneManager = storage.getLuceneManager();
		document = new Document();
		document.add(new TextField("lexical", "dark and stormy night in document one", Field.Store.YES));
		luceneManager.addDocument(document);
		
		FlexibleParameters parameters;
		
		parameters = new FlexibleParameters();
		parameters.addParameter("string",  "It was a dark and stormy night.");
		parameters.addParameter("string", "It was the best of times it was the worst of times.");
		parameters.addParameter("tool", "StepEnabledIndexedCorpusCreator");

		RealCorpusCreator creator = new RealCorpusCreator(storage, parameters);
		creator.run();
		parameters.setParameter("corpus", creator.getStoredId());
		
		parameters.setParameter("tool", "CorpusTermFrequencies");
		
		CorpusTerm corpusTerm;
		CorpusTerms corpusTermFrequencies;
		List<CorpusTerm> corpusTerms;

		parameters.setParameter("query", "dar*");
		corpusTermFrequencies = new CorpusTerms(storage, parameters);
		corpusTermFrequencies.run();		
		corpusTerms = corpusTermFrequencies.getCorpusTerms();
		assertEquals(1, corpusTerms.size());
		corpusTerm = corpusTerms.get(0);
		assertEquals("dark", corpusTerm.getTerm());
		assertEquals(1, corpusTerm.getRawFrequency());
//		assertEquals(0, corpusTerm);
		
		parameters.setParameter("query", "it was");
		corpusTermFrequencies = new CorpusTerms(storage, parameters);
		corpusTermFrequencies.run();		
		// we sort by reverse frequency by default
		corpusTerms = corpusTermFrequencies.getCorpusTerms();
		assertEquals(1, corpusTerms.size());
		corpusTerm = corpusTerms.get(0);
//		assertEquals(1, corpusTerm.getDocumentIndex());
		assertEquals("it was", corpusTerm.getTerm());
		assertEquals(3, corpusTerm.getRawFrequency());

		
		// all terms 
		parameters.removeParameter("query");
		corpusTermFrequencies = new CorpusTerms(storage, parameters);
		corpusTermFrequencies.run();
		corpusTerms = corpusTermFrequencies.getCorpusTerms();
		assertEquals(15, corpusTerms.size());
		corpusTerm = corpusTerms.get(0);
		assertEquals("it", corpusTerm.getTerm());
		assertEquals(3, corpusTerm.getRawFrequency());
		
		// limit 1 (top frequency word)
		parameters.setParameter("limit", "1");
		corpusTermFrequencies = new CorpusTerms(storage, parameters);
		corpusTermFrequencies.run();
		corpusTerms = corpusTermFrequencies.getCorpusTerms();
		assertEquals(1, corpusTerms.size());
		corpusTerm = corpusTerms.get(0);
		assertEquals("it", corpusTerm.getTerm());
		assertEquals(3, corpusTerm.getRawFrequency());

		// start 1, limit 1
		parameters.setParameter("start", "1");
		corpusTermFrequencies = new CorpusTerms(storage, parameters);
		corpusTermFrequencies.run();
		corpusTerms = corpusTermFrequencies.getCorpusTerms();
		assertEquals(1, corpusTerms.size());
		corpusTerm = corpusTerms.get(0);
		assertEquals("was", corpusTerm.getTerm());
		assertEquals(3, corpusTerm.getRawFrequency());

		// start 50, limit 1 (empty)
		parameters.setParameter("start", "50");
		corpusTermFrequencies = new CorpusTerms(storage, parameters);
		corpusTermFrequencies.run();
		corpusTerms = corpusTermFrequencies.getCorpusTerms();
		assertEquals(0, corpusTerms.size());
		
		// with stopwords
		parameters.removeParameter("start");
		parameters.removeParameter("limit");
		parameters.setParameter("stopList", "stop.en.taporware.txt");
		corpusTermFrequencies = new CorpusTerms(storage, parameters);
		corpusTermFrequencies.run();
		corpusTerms = corpusTermFrequencies.getCorpusTerms();
		assertEquals(7, corpusTerms.size());
		
		storage.destroy();
		
	}

}