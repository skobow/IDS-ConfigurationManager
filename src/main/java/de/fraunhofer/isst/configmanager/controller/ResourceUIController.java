package de.fraunhofer.isst.configmanager.controller;

import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.isst.configmanager.communication.clients.DefaultConnectorClient;
import de.fraunhofer.isst.configmanager.configmanagement.service.ResourceService;
import de.fraunhofer.isst.configmanager.util.ValidateApiInput;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * The controller class implements the ResourceUIApi and offers the possibilities to manage
 * the resources in the configuration manager.
 */
@RestController
@RequestMapping("/api/ui")
@Slf4j
@Tag(name = "Resource Management", description = "Endpoints for managing the resource in the " +
        "configuration manager")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceUIController implements ResourceUIApi {
    transient ResourceService resourceService;
    transient DefaultConnectorClient client;
    transient Serializer serializer;

    @Autowired
    public ResourceUIController(final ResourceService resourceService,
                                final DefaultConnectorClient client, final Serializer serializer) {
        this.resourceService = resourceService;
        this.client = client;
        this.serializer = serializer;
    }

    /**
     * This method returns a resource from the connector with the given paraemter.
     *
     * @param resourceId id of the resource
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getResource(final URI resourceId) {
        log.info(">> GET /resource resourceId: " + resourceId);

        if (ValidateApiInput.notValid(resourceId.toString())) {
            return ResponseEntity.badRequest().body("All validated parameter have undefined as " +
                    "value!");
        }

        final var resource = resourceService.getResource(resourceId);

        if (resource != null) {
            try {
                return ResponseEntity.ok(serializer.serialize(resource));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not " +
                        "serialize resource!");
            }
        } else {
            return ResponseEntity.badRequest().body("Could not determine the resource");
        }
    }

    /**
     * This method returns all resources from the connector.
     *
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getResources() {
        log.info(">> GET /resources");
        return ResponseEntity.ok(resourceService.getOfferedResourcesAsJsonString());
    }

    @Override
    public ResponseEntity<String> getRequestedResources() {
        log.info(">> GET /resources/requested");
        return ResponseEntity.ok(resourceService.getRequestedResourcesAsJsonString());
    }

    /**
     * This method returns a specific resource in JSON format.
     *
     * @param resourceId if of the resource
     * @return a suitable http response depending on success
     */
    @Override
    public ResponseEntity<String> getResourceInJson(final URI resourceId) {
        log.info(">> GET /resource/json resourceId: " + resourceId);

        if (ValidateApiInput.notValid(resourceId.toString())) {
            return ResponseEntity.badRequest().body("All validated parameter have undefined as " +
                    "value!");
        }

        final var resource = resourceService.getResource(resourceId);

        final var resourceJson = new JSONObject();
        resourceJson.put("title", resource.getTitle().get(0).getValue());
        resourceJson.put("description", resource.getDescription().get(0).getValue());
        resourceJson.put("keyword", resource.getKeyword());
        resourceJson.put("version", resource.getVersion());
        resourceJson.put("standardlicense", resource.getStandardLicense().toString());
        resourceJson.put("publisher", resource.getPublisher().toString());

        return ResponseEntity.ok(resourceJson.toJSONString());
    }

    /**
     * This method deletes the resource from the connector and the app route with the given
     * parameter.
     * If both are deleted the dataspace connector is informed about the change.
     *
     * @param resourceId id of the resource
     * @return http response from the target connector
     */
    @Override
    public ResponseEntity<String> deleteResource(final URI resourceId) {
        log.info(">> DELETE /resource resourceId: " + resourceId);

        if (ValidateApiInput.notValid(resourceId.toString())) {
            return ResponseEntity.badRequest().body("All validated parameter have undefined as " +
                    "value!");
        }

        try {
            final var response = client.deleteResource(resourceId);
            resourceService.deleteResourceFromAppRoute(resourceId);
            final var jsonObject = new JSONObject();
            jsonObject.put("connectorResponse", response);
            jsonObject.put("resourceID", resourceId.toString());
            return ResponseEntity.ok(jsonObject.toJSONString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body("Could not send delete request to connector");
        }
    }

    /**
     * This method creates a resource with the given parameters. The special feature here is that
     * the created resource
     * is included once in the app route and once in the resource catalog of the connector.
     *
     * @param title           title of the resource
     * @param description     description of the resource
     * @param language        language of the resource
     * @param keywords        keywords for the resource
     * @param version         version of the resource
     * @param standardlicense standard license for the resource
     * @param publisher       the publisher of the resource
     * @return response from the target connector
     */
    @Override
    public ResponseEntity<String> createResource(final String title, final String description,
                                                 final String language,
                                                 final ArrayList<String> keywords,
                                                 final String version, final String standardlicense,
                                                 final String publisher) {
        log.info(">> POST /resource title: " + title + " description: " + description + " " +
                "language: " + language + " keywords: " + keywords + " version: " + version + " " +
                "standardlicense: " + standardlicense
                + " publisher: " + publisher);

        if (ValidateApiInput.notValid(title, description, language, version, standardlicense,
                publisher)) {
            return ResponseEntity.badRequest().body("All validated parameter have undefined as " +
                    "value!");
        }


        final var resource = resourceService.createResource(title, description, language,
                keywords,
                version, standardlicense, publisher);

        // Save and send request to dataspace connector
        final var jsonObject = new JSONObject();
        try {
            jsonObject.put("resourceID", resource.getId().toString());
            final var response = client.registerResource(resource);
            jsonObject.put("connectorResponse", response);
            return ResponseEntity.ok(jsonObject.toJSONString());
        } catch (IOException e) {
            jsonObject.put("message", "Could not register resource at connector");
            log.error(e.getMessage(), e);
            return ResponseEntity.badRequest().body(jsonObject.toJSONString());
        }
    }

    /**
     * This method updates a resource with the given parameters. The special feature here is that
     * the resource
     * is updated once in the app route and once in the resource catalog of the connector.
     *
     * @param resourceId      id of the resource
     * @param title           title of the resource
     * @param description     description of the resource
     * @param language        language of the resource
     * @param keywords        keywords for the resource
     * @param version         version of the resource
     * @param standardlicense standard license for the resource
     * @param publisher       the publisher of the resource
     * @return response from the target connector
     */
    @Override
    public ResponseEntity<String> updateResource(final URI resourceId, final String title,
                                                 final String description, final String language,
                                                 final ArrayList<String> keywords,
                                                 final String version, final String standardlicense,
                                                 final String publisher) {
        log.info(">> PUT /resource title: " + title + " description: " + description + " language" +
                ": " + language + " keywords: " + keywords + " version: " + version + " " +
                "standardlicense: " + standardlicense
                + " publisher: " + publisher);

        if (ValidateApiInput.notValid(resourceId.toString(), title, description, language, version, standardlicense, publisher)) {
            return ResponseEntity.badRequest().body("All validated parameter have undefined as " +
                    "value!");
        }

        // Save the updated resource and update the resource in the dataspace connector
        try {
            final var updatedResource = resourceService.updateResource(resourceId, title,
                    description, language, keywords,
                    version, standardlicense, publisher);
            if (updatedResource != null) {
                final var response = client.updateResource(resourceId, updatedResource);
                resourceService.updateResourceInAppRoute(updatedResource);
                final var jsonObject = new JSONObject();
                jsonObject.put("connectorResponse", response);
                jsonObject.put("resourceID", resourceId.toString());
                return ResponseEntity.ok(jsonObject.toJSONString());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("No " +
                        "resource with ID %s was found!", resourceId));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
