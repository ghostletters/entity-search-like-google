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

    public void onStart(@Observes StartupEvent startupEvent) {
        Request request = new Request("PUT", bookIndex);

        try {
            if (isIndexPresent(bookIndex)) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String indexConfig = """
                {
                  "settings": {
                    "analysis": {
                      "analyzer": {
                        "my_text_field_analyzer": {
                          "tokenizer": "standard",
                          "filter": [
                            "lowercase",
                            "my_text_asciifolding_filter"
                          ]
                        }
                      },
                      "filter": {
                        "my_text_asciifolding_filter": {
                          "type": "asciifolding",
                          "preserve_original": true
                        }
                      }
                    }
                  },
                  "mappings": {
                    "properties": {
                      "title": {
                        "type": "text",
                        "analyzer": "my_text_field_analyzer"
                      },
                      "author": {
                        "type": "text",
                        "analyzer": "my_text_field_analyzer"
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
