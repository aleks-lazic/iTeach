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
        name = "dayApi",
        version = "v1",
        resource = "day",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.patrickclivaz.example.com",
                ownerName = "backend.myapplication.patrickclivaz.example.com",
                packagePath = ""
        )
)
public class DayEndpoint {

    private static final Logger logger = Logger.getLogger(DayEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Day.class);
    }

    /**
     * Returns the {@link Day} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Day} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "day/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Day get(@Named("id") long id) throws NotFoundException {
        logger.info("Getting Day with ID: " + id);
        Day day = ofy().load().type(Day.class).id(id).now();
        if (day == null) {
            throw new NotFoundException("Could not find Day with ID: " + id);
        }
        return day;
    }

    /**
     * Inserts a new {@code Day}.
     */
    @ApiMethod(
            name = "insert",
            path = "day",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Day insert(Day day) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that day.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(day).now();
        logger.info("Created Day with ID: " + day.getId());

        return ofy().load().entity(day).now();
    }

    /**
     * Updates an existing {@code Day}.
     *
     * @param id  the ID of the entity to be updated
     * @param day the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Day}
     */
    @ApiMethod(
            name = "update",
            path = "day/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Day update(@Named("id") long id, Day day) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(day).now();
        logger.info("Updated Day: " + day);
        return ofy().load().entity(day).now();
    }

    /**
     * Deletes the specified {@code Day}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code Day}
     */
    @ApiMethod(
            name = "remove",
            path = "day/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(Day.class).id(id).now();
        logger.info("Deleted Day with ID: " + id);
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
            path = "day",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Day> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Day> query = ofy().load().type(Day.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Day> queryIterator = query.iterator();
        List<Day> dayList = new ArrayList<Day>(limit);
        while (queryIterator.hasNext()) {
            dayList.add(queryIterator.next());
        }
        return CollectionResponse.<Day>builder().setItems(dayList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(long id) throws NotFoundException {
        try {
            ofy().load().type(Day.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Day with ID: " + id);
        }
    }
}