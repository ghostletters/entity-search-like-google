package xyz.ghostletters.mainapp;

import xyz.ghostletters.common.entity.Author;
import xyz.ghostletters.common.repository.AuthorRepository;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/author")
public class AuthorResource {

    @Inject
    AuthorRepository autherRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Author hello() {
        return autherRepository.getOne(1L);
    }


}