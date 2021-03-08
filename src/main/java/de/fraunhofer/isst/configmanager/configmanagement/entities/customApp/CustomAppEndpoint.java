package de.fraunhofer.isst.configmanager.configmanagement.entities.customApp;

import de.fraunhofer.iais.eis.AppEndpoint;
import de.fraunhofer.isst.configmanager.configmanagement.entities.customGenericEndpoint.BackendConfig;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * A custom endpoint entity which extends from BackendConfig. This solution is needed to be able to persist endpoint in
 * the internal database.
 */
@Entity
@NoArgsConstructor
public class CustomAppEndpoint extends BackendConfig<AppEndpoint> {

    public CustomAppEndpoint(AppEndpoint appEndpoint) {
        super(appEndpoint);
    }

}