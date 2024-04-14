package edu.cornell;

/**
 * This class contains constants for the various API endpoints used within the application.
 * It provides a central location to manage URLs, making changes easier and the code cleaner.
 * The constants are used to build the full URLs for API requests.
 * <p>
 * Usage:
 * <pre>
 * String url = ENDPOINTS.BASE_ENDPOINT + ENDPOINTS.FIRST_PORT + ENDPOINTS.UPDATE_ROUTE;
 * </pre>
 * </p>
 */
public class ENDPOINTS {

    /**
     * The base URL for the API. This is typically the root address of the API server,
     * which in this case is configured to communicate with a service running in Docker.
     */
    public static final String BASE_ENDPOINT = "http://host.docker.internal";

    /**
     * The port number for the first service. This is used to construct the full URL
     * by appending it to the base endpoint.
     */
    public static final String FIRST_PORT = ":5001";

    /**
     * An alternative port number for optional services. This can be used similarly
     * to FIRST_PORT to access different services if deployed separately.
     */
    public static final String OPT_PORT = ":5002";

    /**
     * The route for updating resources via POST requests. When appended to BASE_ENDPOINT
     * and a port, it forms the full URL to which update requests should be sent.
     */
    public static final String UPDATE_ROUTE = "/update";

    /**
     * The route for adding new results via POST requests. This endpoint should be used
     * when new data needs to be communicated to the server, appended similarly to UPDATE_ROUTE.
     */
    public static final String ADD_RESULTS_ROUTE = "/add_results";

    /**
     * The route used for retrieving partitions of test classes. This endpoint is typically
     * used to get data that has been processed and partitioned by the server, formatted
     * as needed for further operations or display.
     */
    public static final String PARTITION_ROUTE = "/partition";

}
