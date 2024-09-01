package dev.kush.springaivertex.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
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

    @Value("file:/home/kushparsaniya/Personal/Notes/springBootNotes/KafkaAndRabbitMQBooks/Learning-Apache-Kafka-(2nd Edition).pdf")
    private Resource pdfDocument;

    public PdfLoader(JdbcClient jdbcClient, VectorStore vectorStore) {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
    }

//    @PostConstruct
    public void init() {
        Integer count = jdbcClient.sql("""
                        select count(1) from vector_store
                        """)
                .query(Integer.class).single();

        if (count == 0) {
            logger.info("Loading pdf start----------->");

            var config = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                            .withNumberOfBottomTextLinesToDelete(0)
                            .withNumberOfTopPagesToSkipBeforeDelete(0)
                            .build())
                    .withPagesPerDocument(1)
                    .build();

            var pdfReader = new PagePdfDocumentReader(pdfDocument, config);
            var textSplitter = new TokenTextSplitter();

            vectorStore.add(textSplitter.apply(pdfReader.get()));

            logger.info("Loading pdf end----------->");
        }

    }
}
