package com.omb.ocpp.config;

import com.omb.ocpp.server.iso15118.OcppCertificateSignedSpecification;
import com.omb.ocpp.server.iso15118.SignCertificateFeatureOperator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public enum ConfigKey {

    GUI_MODE("application.gui.mode",
            "Indicates that application should be started without GUI, possible values:  'true/false', default: %s",
            true),

    OCPP_SERVER_IP("ocpp.server.ip",
            "Ip on which server will accept OCPP connections, default:%s, works in combination " +
                    "with 'application.gui.mode:false'",
            "0.0.0.0"),

    OCPP_SERVER_PORT("ocpp.server.port", "Port on which OCPP server will accept connections, default:%s, works in" +
            " combination with 'application.gui.mode:false'",
            8887),
    OCPP_AUTH_PASSWORD("ocpp.server.auth.password", "Password that has to be sent by client in order to connect",
            ""),

    OCPP_FEATURES_PROFILE_LIST("ocpp.features.profile.list", "List of features supported by server, " +
            "default:Core,FirmwareManagement,RemoteTrigger,LocalAuthList",
            new ArrayList<>(Arrays.asList("Core", "FirmwareManagement", "RemoteTrigger", "LocalAuthList", "ISO15118", "SmartCharging", "SecuritySpec16"))),

    REST_API_PORT("rest.api.port", "port on which REST server will accept connections, default:%s, works in" +
            " combination with 'application.gui.mode:false'",
            9090),

    SSL_ENABLED("ssl.enabled",
            "Run ssl server with ssl context," +
                    "works in combination with 'application.gui.mode:false'",
            false),

    SSL_KEYSTORE_UUID("ssl.keystore.uuid",
            "if ssl enabled server will use keystore with given keystore uuid, " +
                    "works in combination with 'application.gui.mode:false'",
            "none"),

    SSL_CLIENT_AUTH("ssl.client.auth",
            "Indicates if server needs to validate client certificate, works in combination with 'application.gui" +
                    ".mode:false'",
            false),

    SSL_KEYSTORE_CIPHERS("ssl.keystore.ciphers",
            "List of keystore ciphers separated by comma, works in combination with 'application.gui.mode:false'",
            new LinkedList<String>()),

    CERTIFICATE_CHAIN_ADD_ROOT_CA_TO("certificate.chain.add.root.ca",
            "Should ROOT CA be added to certificate chain",
            false),

    CERTIFICATE_EXPIRATION_IN_MINUTES("certificate.expiration.minutes",
            "Certificate valid in minutes",
            90),

    CERTIFICATE_SIGNED_SPEC_VERSION(
            "certificate.signed.spec.version",
            String.format("Version of ocpp signed certificate. Available values are: %s. Default value is: %s", Arrays.toString(OcppCertificateSignedSpecification.values()), OcppCertificateSignedSpecification.OCPP_2_0),
            OcppCertificateSignedSpecification.OCPP_2_0.name()),

    SIGN_CERTIFICATE_FEATURE_OPERATOR(
            "sign.certificate.feature.operator",
            String.format("Choose between signing operators. Available values are: %s. Default value is: %s", Arrays.toString(SignCertificateFeatureOperator.values()), SignCertificateFeatureOperator.IONITY),
            SignCertificateFeatureOperator.IONITY);

    private String key;
    private String comment;
    private Object defaultValue;

    ConfigKey(String key, String comment, Object defaultValue) {
        this.key = key;
        this.comment = comment;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getComment() {
        return String.format(comment, defaultValue);
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
