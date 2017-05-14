package com.example.patrickclivaz.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "teacherApi",
        version = "v1",
        resource = "teacher",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.patrickclivaz.example.com",
                ownerName = "backend.myapplication.patrickclivaz.example.com",
                packagePath = ""
        )
)
public class TeacherEndpoint {

    private static final Logger logger = Logger.getLogger(TeacherEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 2;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Teacher.class);
    }

    /**
     * Returns the {@link Teacher} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Teacher} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "teacher/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Teacher get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting Teacher with ID: " + id);
        Teacher teacher = ofy().load().type(Teacher.class).id(id).now();
        if (teacher == null) {
            throw new NotFoundException("Could not find Teacher with ID: " + id);
        }
        return teacher;
    }

    /**
     * Inserts a new {@code Teacher}.
     */
    @ApiMethod(
            name = "insert",
            path = "teacher",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Teacher insert(Teacher teacher) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that teacher.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(teacher).now();
        logger.info("Created Teacher with ID: " + teacher.getId());

        return ofy().load().entity(teacher).now();
    }

    /**
     * Updates an existing {@code Teacher}.
     *
     * @param id      the ID of the entity to be updated
     * @param teacher the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Teacher}
     */
    @ApiMethod(
            name = "update",
            path = "teacher/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Teacher update(@Named("id") long id, Teacher teacher) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(teacher).now();
        logger.info("Updated Teacher: " + teacher);
        return ofy().load().entity(teacher).now();
    }

    /**
     * Deletes the specified {@code Teacher}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Teacher}
     */
    @ApiMethod(
            name = "remove",
            path = "teacher/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Teacher.class).id(id).now();
        logger.info("Deleted Teacher with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "teacher",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Teacher> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Teacher> query = ofy().load().type(Teacher.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Teacher> queryIterator = query.iterator();
        List<Teacher> teacherList = new ArrayList<Teacher>(limit);
        while (queryIterator.hasNext()) {
            teacherList.add(queryIterator.next());
        }
        return CollectionResponse.<Teacher>builder().setItems(teacherList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(Teacher.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Teacher with ID: " + id);
        }
    }
}