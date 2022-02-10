package xyz.ghostletters.searchapp.elasticclient;

import io.vertx.core.json.JsonObject;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class BookSearchViewRepository {

    @Inject
    RestClient restClient;

    public Response save(BookSearchView bookSearchView) throws IOException {
        Request putRequest = new Request(
                "PUT",
                "/book/_doc/" + bookSearchView.isbn());

        putRequest.setJsonEntity(JsonObject.mapFrom(bookSearchView).toString());

        return restClient.performRequest(putRequest);
    }
}
