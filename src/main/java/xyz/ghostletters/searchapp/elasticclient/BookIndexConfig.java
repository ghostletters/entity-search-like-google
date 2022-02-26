package xyz.ghostletters.searchapp.elasticclient;

import io.quarkus.runtime.StartupEvent;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class BookIndexConfig {

    private static final String bookIndex = "/book";

    @Inject
    RestClient restClient;

    public void onStart(@Observes StartupEvent startupEvent) throws IOException {
        Request request = new Request("PUT", bookIndex);

        if (isIndexPresent(bookIndex)) {
            return;
        }

        String indexConfig = """
                {
                   "settings": {
                     "analysis": {
                       "tokenizer": {
                         "edge_ngram_2_20": {
                           "type": "edge_ngram",
                           "min_gram": 2,
                           "max_gram": 20,
                           "token_chars": [
                             "letter"
                           ]
                         }
                       },
                       "analyzer": {
                         "autocomplete": {
                           "tokenizer": "edge_ngram_2_20",
                           "filter": [
                             "lowercase",
                             "asciifolding"
                           ]
                         },
                         "autocomplete_search": {
                           "tokenizer": "lowercase",
                           "filter": [
                             "asciifolding"
                           ]
                         }
                       }
                     }
                   },
                   "mappings": {
                    "properties" : {
                       "title" : {
                         "type": "text",
                         "analyzer": "autocomplete",
                         "search_analyzer": "autocomplete_search"
                       },
                       "author" : {
                         "type": "text",
                         "analyzer": "autocomplete",
                         "search_analyzer": "autocomplete_search"
                       },
                       "author_title_combined" : {
                         "type": "text",
                         "analyzer": "autocomplete",
                         "search_analyzer": "autocomplete_search"
                       }
                     }
                   }
                 }
                """;

        request.setJsonEntity(indexConfig);

        try {
            restClient.performRequest(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isIndexPresent(String bookIndex) throws IOException {
        return restClient.performRequest(new Request("HEAD", bookIndex))
                .getStatusLine()
                .getStatusCode() == 200;
    }
}
