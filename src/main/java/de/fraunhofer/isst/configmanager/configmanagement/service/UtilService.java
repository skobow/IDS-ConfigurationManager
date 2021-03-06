package de.fraunhofer.isst.configmanager.configmanagement.service;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.configmanager.communication.dataspaceconnector.model.BackendSource;
import de.fraunhofer.isst.configmanager.configmanagement.entities.config.BrokerStatus;
import de.fraunhofer.isst.configmanager.configmanagement.entities.routedeploymethod.DeployMethod;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Service;

/**
 * The class can be used to define auxiliary methods that are needed again and again.
 */
@Service
public class UtilService {

    /**
     * The method returns for a given enum name all enum values.
     *
     * @param enumName name of the enum
     * @return enums as string
     */
    public String getSpecificEnum(final String enumName) {
        final var jsonArray = new JSONArray();
        final var name = enumName.toLowerCase();

        if (name.contains("loglevel")) {
            final var logLevels = LogLevel.values();
            for (int i = 0; i < logLevels.length; i++) {
                jsonArray.add(i, logLevels[i].name());
            }
        }
        if (name.contains("connectorstatus")) {
            final var connectorStatuses = ConnectorStatus.values();
            for (int i = 0; i < connectorStatuses.length; i++) {
                jsonArray.add(i, connectorStatuses[i].name());
            }
        }
        if (name.contains("connectordeploymode")) {
            final var connectorDeployModes = ConnectorDeployMode.values();
            for (int i = 0; i < connectorDeployModes.length; i++) {
                jsonArray.add(i, connectorDeployModes[i].name());
            }
        }
        if (name.contains("securityprofile")) {
            final var securityProfiles = SecurityProfile.values();
            for (int i = 0; i < securityProfiles.length; i++) {
                jsonArray.add(i, securityProfiles[i].name());
            }
        }
        if (name.contains("language")) {
            final var languages = Language.values();
            for (int i = 0; i < languages.length; i++) {
                jsonArray.add(i, languages[i].name());
            }
        }
        if (name.contains("sourcetype")) {
            final var sourceTypes = BackendSource.Type.values();
            for (int i = 0; i < sourceTypes.length; i++) {
                jsonArray.add(i, sourceTypes[i].name());
            }
        }
        if (name.contains("deploymethod")) {
            final var deployMethods = DeployMethod.values();
            for (int i = 0; i < deployMethods.length; i++) {
                jsonArray.add(i, deployMethods[i].name());
            }
        }
        if (name.contains("brokerstatus")) {
            final var brokerStatuses = BrokerStatus.values();
            for (int i = 0; i < brokerStatuses.length; i++) {
                jsonArray.add(i, brokerStatuses[i].name());
            }
        }
        return jsonArray.toJSONString();
    }
}
