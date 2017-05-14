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
        name = "lectureApi",
        version = "v1",
        resource = "lecture",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.patrickclivaz.example.com",
                ownerName = "backend.myapplication.patrickclivaz.example.com",
                packagePath = ""
        )
)
public class LectureEndpoint {

    private static final Logger logger = Logger.getLogger(LectureEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Lecture.class);
    }

    /**
     * Returns the {@link Lecture} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Lecture} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "lecture/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Lecture get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting Lecture with ID: " + id);
        Lecture lecture = ofy().load().type(Lecture.class).id(id).now();
        if (lecture == null) {
            throw new NotFoundException("Could not find Lecture with ID: " + id);
        }
        return lecture;
    }

    /**
     * Inserts a new {@code Lecture}.
     */
    @ApiMethod(
            name = "insert",
            path = "lecture",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Lecture insert(Lecture lecture) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that lecture.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.

        //save the teacher in the database
        ofy().save().entity(lecture.getTeacher()).now();

        ofy().save().entity(lecture).now();
        logger.info("Created Lecture with ID: " + lecture.getId());

        return ofy().load().entity(lecture).now();
    }

    /**
     * Updates an existing {@code Lecture}.
     *
     * @param id      the ID of the entity to be updated
     * @param lecture the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Lecture}
     */
    @ApiMethod(
            name = "update",
            path = "lecture/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Lecture update(@Named("id") long id, Lecture lecture) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);

        //save the teacher in the database
        ofy().save().entity(lecture.getTeacher()).now();

        ofy().save().entity(lecture).now();
        logger.info("Updated Lecture: " + lecture);
        return ofy().load().entity(lecture).now();
    }

    /**
     * Deletes the specified {@code Lecture}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Lecture}
     */
    @ApiMethod(
            name = "remove",
            path = "lecture/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Lecture.class).id(id).now();
        logger.info("Deleted Lecture with ID: " + id);
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
            path = "lecture",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Lecture> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Lecture> query = ofy().load().type(Lecture.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Lecture> queryIterator = query.iterator();
        List<Lecture> lectureList = new ArrayList<Lecture>(limit);
        while (queryIterator.hasNext()) {
            lectureList.add(queryIterator.next());
        }
        return CollectionResponse.<Lecture>builder().setItems(lectureList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(Lecture.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Lecture with ID: " + id);
        }
    }
}