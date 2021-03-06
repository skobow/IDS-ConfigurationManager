package de.fraunhofer.isst.configmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

public interface EndpointUIApi {

    // APIs to manage the generic endpoints
    @PostMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Creates a generic endpoint")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Created a generic endpoint")})
    ResponseEntity<String> createGenericEndpoint(@RequestParam(value = "accessURL") String accessURL,
                                                 @RequestParam(value = "username", required = false) String username,
                                                 @RequestParam(value = "password", required = false) String password);

    @GetMapping(value = "/generic/endpoints", produces = "application/ld+json")
    @Operation(summary = "Returns a list of generic endpoints")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Returned a list of generic endpoints")})
    ResponseEntity<String> getGenericEndpoints();

    @GetMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Returns a specific generic endpoint")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Returned a specific backend connection")})
    ResponseEntity<String> getGenericEndpoint(@RequestParam(value = "endpointId") URI endpointId);

    @DeleteMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Deletes a generic endpoint")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Deleted a generic endpoint")})
    ResponseEntity<String> deleteGenericEndpoint(@RequestParam(value = "endpointId") URI endpointId);

    @PutMapping(value = "/generic/endpoint", produces = "application/ld+json")
    @Operation(summary = "Updates a generic endpoint")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Updated a generic endpoint")})
    ResponseEntity<String> updateGenericEndpoint(@RequestParam(value = "id") URI id,
                                                 @RequestParam(value = "accessURL", required = false) String accessURL,
                                                 @RequestParam(value = "username", required = false) String username,
                                                 @RequestParam(value = "password", required = false) String password);

    // APIs to manage the connector endpoints
    @GetMapping(value = "/connector/endpoints", produces = "application/ld+json")
    @Operation(summary = "Returns the connector endpoints")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successfully returned the connector endpoints")})
    ResponseEntity<String> getConnectorEndpoints();

    @GetMapping(value = "/connector/endpoints/client", produces = "application/ld+json")
    @Operation(summary = "Returns a list of connector endpoints")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successfully returned a list of connector endpoints")})
    ResponseEntity<String> getConnectorEndpointsFromClient(@RequestParam("accessUrl") String accessUrl,
                                                           @RequestParam(value = "resourceId", required = false) String resourceId);

    @GetMapping(value = "/connector/endpoint", produces = "application/ld+json")
    @Operation(summary = "Returns the connector endpoint")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successfully returned the connector endpoint")})
    ResponseEntity<String> getConnectorEndpoint(@RequestParam("connectorEndpointId") URI connectorEndpointId);

    @PostMapping(value = "/connector/endpoint", produces = "application/ld+json")
    @Operation(summary = "Creates a new connector endpoint for the connector")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successfully created the connector endpoint " +
            "for the connector")})
    ResponseEntity<String> createConnectorEndpoint(@RequestParam("accessUrl") String accessUrl);

}
