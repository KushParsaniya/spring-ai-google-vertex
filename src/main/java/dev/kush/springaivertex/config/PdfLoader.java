package dev.kush.springaivertex.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
public class PdfLoader {

    Logger logger = LoggerFactory.getLogger(PdfLoader.class);

    private final JdbcClient jdbcClient;

    private final VectorStore vectorStore;

    @Value("classpath:docs/demo.txt")
    private Resource pdfDocument;

    public PdfLoader(JdbcClient jdbcClient, VectorStore vectorStore) {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() {
        Integer count = jdbcClient.sql("""
                        select count(1) from vector_store
                        """)
                .query(Integer.class).single();

        if (count == 0) {
            logger.info("Loading pdf start----------->");

            var reader = new TextReader(pdfDocument);


//            var config = PdfDocumentReaderConfig.builder()
//                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
//                            .withNumberOfBottomTextLinesToDelete(0)
//                            .withNumberOfTopPagesToSkipBeforeDelete(0)
//                            .build())
//                    .withPagesPerDocument(1)
//                    .build();
//
//            var pdfReader = new PagePdfDocumentReader(pdfDocument, config);
            var textSplitter = new TokenTextSplitter();

            vectorStore.add(textSplitter.apply(reader.get()));

            logger.info("Loading pdf end----------->");
        }

    }
}
