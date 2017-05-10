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
        name = "studentApi",
        version = "v1",
        resource = "student",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.patrickclivaz.example.com",
                ownerName = "backend.myapplication.patrickclivaz.example.com",
                packagePath = ""
        )
)
public class StudentEndpoint {

    private static final Logger logger = Logger.getLogger(StudentEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Student.class);
    }

    /**
     * Returns the {@link Student} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Student} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "student/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Student get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting Student with ID: " + id);
        Student student = ofy().load().type(Student.class).id(id).now();
        if (student == null) {
            throw new NotFoundException("Could not find Student with ID: " + id);
        }
        return student;
    }

    /**
     * Inserts a new {@code Student}.
     */
    @ApiMethod(
            name = "insert",
            path = "student",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Student insert(Student student) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that student.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(student).now();
        logger.info("Created Student with ID: " + student.getId());

        return ofy().load().entity(student).now();
    }

    /**
     * Updates an existing {@code Student}.
     *
     * @param id      the ID of the entity to be updated
     * @param student the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Student}
     */
    @ApiMethod(
            name = "update",
            path = "student/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Student update(@Named("id") long id, Student student) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(student).now();
        logger.info("Updated Student: " + student);
        return ofy().load().entity(student).now();
    }

    /**
     * Deletes the specified {@code Student}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Student}
     */
    @ApiMethod(
            name = "remove",
            path = "student/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Student.class).id(id).now();
        logger.info("Deleted Student with ID: " + id);
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
            path = "student",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Student> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Student> query = ofy().load().type(Student.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Student> queryIterator = query.iterator();
        List<Student> studentList = new ArrayList<Student>(limit);
        while (queryIterator.hasNext()) {
            studentList.add(queryIterator.next());
        }
        return CollectionResponse.<Student>builder().setItems(studentList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(Student.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Student with ID: " + id);
        }
    }
}