package com.omb.ocpp.gui;

import com.omb.ocpp.config.Config;
import com.omb.ocpp.config.ConfigKey;
import com.omb.ocpp.server.Feature;
import com.omb.ocpp.server.OcppServerService;
import com.omb.ocpp.server.SessionsListener;
import com.omb.ocpp.server.iso15118.dto.InstallCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.SignedUpdateFirmwareRequest;
import com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto.ExtendedTriggerMessageRequest;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.SessionInformation;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionRequest;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import eu.chargetime.ocpp.model.smartcharging.ClearChargingProfileRequest;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.glassfish.hk2.api.ServiceLocator;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.omb.ocpp.gui.StubRequestsFactory.toRequest;

@Service
public class CommunicatorTab {
    private static final Logger logger = LoggerFactory.getLogger(CommunicatorTab.class);

    private final ListView<String> sessionsList = new ListView<>();
    private final TextField selectedClientField = new TextField();
    private final ComboBox<Class<? extends Request>> messageTypeCombo = new ComboBox<>();
    private final TextArea messageTextArea = new TextArea();
    private final Button sendButton = new Button("Send Message");
    private final Config config;

    private final OcppServerService ocppServerService;

    @Inject
    public CommunicatorTab(ServiceLocator applicationContext) {
        this.ocppServerService = applicationContext.getService(OcppServerService.class);
        this.config = applicationContext.getService(Config.class);
    }

    public Tab constructTab(Stage primaryStage) {
        Tab tab = new Tab();
        tab.setText("Communicator");
        tab.setClosable(false);

        ObservableList<String> items = FXCollections.observableArrayList(
                getSessionsListFormatted(ocppServerService.getSessionList()));
        sessionsList.setItems(items);
        sessionsList.setMinWidth(200);
        sessionsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                selectedClientField.setText(newValue));
        ocppServerService.setSessionsListener(new GuiSessionsListener());
        selectedClientField.setPromptText("Selected client");
        selectedClientField.setEditable(false);

        messageTypeCombo.setItems(FXCollections.observableArrayList(getMessagesAvailableForSend()));
        messageTypeCombo.prefWidthProperty().bind(primaryStage.widthProperty());
        messageTypeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Class<? extends Request> object) {
                if (object == null) {
                    return "Select request";
                }
                return object.getSimpleName();
            }

            @Override
            public Class<? extends Request> fromString(String string) {
                try {
                    return Class.forName(string).asSubclass(Request.class);
                } catch (ClassNotFoundException e) {
                    return SendLocalListRequest.class;
                }
            }
        });
        messageTypeCombo.setOnAction(event -> {
            if (messageTypeCombo.getValue() != null) {
                messageTextArea.setText(StubRequestsFactory.getStubRequest(messageTypeCombo.getValue()));
            }
        });
        messageTextArea.prefWidthProperty().bind(primaryStage.widthProperty());
        sendButton.prefWidthProperty().bind(primaryStage.widthProperty());

        VBox vBox = new VBox();
        VBox.setVgrow(messageTextArea, Priority.ALWAYS);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(5));
        vBox.getChildren().addAll(selectedClientField, messageTypeCombo, messageTextArea, sendButton);
        vBox.setFillWidth(true);

        sendButton.setOnAction(event -> {
            if (selectedClientField.getText() != null
                    && !selectedClientField.getText().isEmpty()
                    && !selectedClientField.getText().equals("NONE")) {
                Optional<? extends Request> request = toRequest(messageTextArea.getText(), messageTypeCombo.getValue());
                if (request.isPresent()) {
                    ocppServerService.send(request.get(), selectedClientField.getText());
                } else {
                    logger.error("Request parsing error, request: {}", messageTextArea.getText());
                }
            } else {
                logger.error("Client to send for is not selected");
            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(sessionsList, vBox);

        tab.setContent(hBox);
        return tab;
    }

    private Set<Class<? extends Request>> getMessagesAvailableForSend() {
        Set<Class<? extends Request>> messages = new LinkedHashSet<>();

        //Core profile messages
        Collection<String> featuresList = config.getStringCollection(ConfigKey.OCPP_FEATURES_PROFILE_LIST);

        messages.add(ChangeAvailabilityRequest.class);
        messages.add(ChangeConfigurationRequest.class);
        messages.add(ClearCacheRequest.class);
        messages.add(DataTransferRequest.class);
        messages.add(GetConfigurationRequest.class);
        messages.add(MeterValuesRequest.class);
        messages.add(RemoteStartTransactionRequest.class);
        messages.add(RemoteStopTransactionRequest.class);
        messages.add(ResetRequest.class);
        messages.add(UnlockConnectorRequest.class);

        if (featuresList.contains(Feature.FIRMWARE_MANAGEMENT.getKey())) {
            messages.add(DiagnosticsStatusNotificationRequest.class);
            messages.add(FirmwareStatusNotificationRequest.class);
            messages.add(GetDiagnosticsRequest.class);
            messages.add(UpdateFirmwareRequest.class);
        }

        if (featuresList.contains(Feature.LOCAL_AUTH_LIST.getKey())) {
            messages.add(GetLocalListVersionRequest.class);
            messages.add(SendLocalListRequest.class);
        }

        if (featuresList.contains(Feature.REMOTE_TRIGGER.getKey())) {
            messages.add(TriggerMessageRequest.class);
        }

        if (featuresList.contains(Feature.SMART_CHARGING.getKey())) {
            messages.add(SetChargingProfileRequest.class);
            messages.add(ClearChargingProfileRequest.class);
        }

        if (featuresList.contains(Feature.ISO_15118.getKey())) {
            messages.add(com.omb.ocpp.server.iso15118.dto.TriggerMessageRequest.class);
            messages.add(SignedUpdateFirmwareRequest.class);
            messages.add(InstallCertificateRequest.class);
        }

        if(featuresList.contains(Feature.SECURITY_SPEC_16.getKey())) {
            messages.add(ExtendedTriggerMessageRequest.class);
        }

        return messages;
    }


    private static List<String> getSessionsListFormatted(Map<UUID, SessionInformation> sessions) {
        return sessions.values().stream()
                .map(sessionInformation ->
                        String.format("%s (%s)", sessionInformation.getIdentifier(), sessionInformation.getAddress()))
                .collect(Collectors.toList());
    }

    private class GuiSessionsListener implements SessionsListener {
        @Override
        public void onSessionsCountChange(Map<UUID, SessionInformation> sessions) {
            Platform.runLater(() -> {
                ObservableList<String> items = FXCollections.observableArrayList(
                        getSessionsListFormatted(sessions));
                sessionsList.setItems(items);
            });
        }
    }
}


