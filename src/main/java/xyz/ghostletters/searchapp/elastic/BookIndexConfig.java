package xyz.ghostletters.searchapp.elastic;

import io.quarkus.runtime.StartupEvent;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class BookIndexConfig {

    public static final String BOOK = "book";
    @Inject
    RestClient restClient;

    public void onStart(@Observes StartupEvent startupEvent) throws IOException {
        if (isIndexPresent(BOOK)) {
            return;
        }

        String jsonConfig = """
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
                        "properties": {
                          "title": {
                            "type": "text",
                            "analyzer": "autocomplete",
                            "search_analyzer": "autocomplete_search"
                          },
                          "author": {
                            "type": "text",
                            "analyzer": "autocomplete",
                            "search_analyzer": "autocomplete_search"
                          },
                          "all": {
                            "type": "text",
                            "analyzer": "autocomplete",
                            "search_analyzer": "autocomplete_search"
                          }
                        }
                      }
                    }            
                """;

        Request put = new Request("PUT", BOOK);
        put.setJsonEntity(jsonConfig);

        restClient.performRequest(put);
    }

    private boolean isIndexPresent(String book) throws IOException {
        return restClient.performRequest(new Request("HEAD", book))
                .getStatusLine()
                .getStatusCode() == 200;
    }
}
