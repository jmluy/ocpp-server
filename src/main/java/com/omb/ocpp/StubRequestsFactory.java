package com.omb.ocpp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.omb.ocpp.server.iso15118.dto.FirmwareType;
import com.omb.ocpp.server.iso15118.dto.InstallCertificateRequest;
import com.omb.ocpp.server.iso15118.dto.MessageTrigger;
import com.omb.ocpp.server.iso15118.dto.SignedUpdateFirmwareRequest;
import com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto.ExtendedTriggerMessage;
import com.omb.ocpp.server.security.spec16ed2.extended.trigger.message.dto.ExtendedTriggerMessageRequest;
import eu.chargetime.ocpp.JSONCommunicator;
import eu.chargetime.ocpp.PropertyConstraintException;
import eu.chargetime.ocpp.model.Request;
import eu.chargetime.ocpp.model.core.AvailabilityType;
import eu.chargetime.ocpp.model.core.ChangeAvailabilityRequest;
import eu.chargetime.ocpp.model.core.ChangeConfigurationRequest;
import eu.chargetime.ocpp.model.core.ChargingProfile;
import eu.chargetime.ocpp.model.core.ChargingProfileKindType;
import eu.chargetime.ocpp.model.core.ChargingProfilePurposeType;
import eu.chargetime.ocpp.model.core.ChargingRateUnitType;
import eu.chargetime.ocpp.model.core.ChargingSchedule;
import eu.chargetime.ocpp.model.core.ChargingSchedulePeriod;
import eu.chargetime.ocpp.model.core.ClearCacheRequest;
import eu.chargetime.ocpp.model.core.DataTransferRequest;
import eu.chargetime.ocpp.model.core.GetConfigurationRequest;
import eu.chargetime.ocpp.model.core.MeterValue;
import eu.chargetime.ocpp.model.core.MeterValuesRequest;
import eu.chargetime.ocpp.model.core.RecurrencyKindType;
import eu.chargetime.ocpp.model.core.RemoteStartTransactionRequest;
import eu.chargetime.ocpp.model.core.RemoteStopTransactionRequest;
import eu.chargetime.ocpp.model.core.ResetRequest;
import eu.chargetime.ocpp.model.core.ResetType;
import eu.chargetime.ocpp.model.core.SampledValue;
import eu.chargetime.ocpp.model.core.UnlockConnectorRequest;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatus;
import eu.chargetime.ocpp.model.firmware.DiagnosticsStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.FirmwareStatus;
import eu.chargetime.ocpp.model.firmware.FirmwareStatusNotificationRequest;
import eu.chargetime.ocpp.model.firmware.GetDiagnosticsRequest;
import eu.chargetime.ocpp.model.firmware.UpdateFirmwareRequest;
import eu.chargetime.ocpp.model.localauthlist.GetLocalListVersionRequest;
import eu.chargetime.ocpp.model.localauthlist.SendLocalListRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequest;
import eu.chargetime.ocpp.model.remotetrigger.TriggerMessageRequestType;
import eu.chargetime.ocpp.model.smartcharging.ClearChargingProfileRequest;
import eu.chargetime.ocpp.model.smartcharging.SetChargingProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class StubRequestsFactory {

    private static final Logger logger = LoggerFactory.getLogger(StubRequestsFactory.class);

    private static final String NOT_SUPPORTED = "Request not supported";
    private static final String REQUEST_CONSTRUCTION_ERROR = "Request construction error";
    private static final JSONCommunicator jsonCommunicator = new JSONCommunicator(null);

    private static ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .registerModule(new JavaTimeModule());

    private StubRequestsFactory() {
    }


    static String getStubRequest(Class<? extends Request> requestClass) {
        try {
            if (requestClass.equals(ChangeAvailabilityRequest.class)) {
                return getChangeAvailabilityRequest();
            } else if (requestClass.equals(ChangeConfigurationRequest.class)) {
                return getChangeConfigurationRequest();
            } else if (requestClass.equals(ClearCacheRequest.class)) {
                return getClearCacheRequest();
            } else if (requestClass.equals(DataTransferRequest.class)) {
                return getDataTransferRequest();
            } else if (requestClass.equals(GetConfigurationRequest.class)) {
                return getGetConfigurationRequest();
            } else if (requestClass.equals(MeterValuesRequest.class)) {
                return getMeterValuesRequest();
            } else if (requestClass.equals(RemoteStartTransactionRequest.class)) {
                return getRemoteStartTransactionRequest();
            } else if (requestClass.equals(RemoteStopTransactionRequest.class)) {
                return getRemoteStopTransactionRequest();
            } else if (requestClass.equals(ResetRequest.class)) {
                return getResetRequest();
            } else if (requestClass.equals(UnlockConnectorRequest.class)) {
                return getUnlockConnectorRequest();
            } else if (requestClass.equals(DiagnosticsStatusNotificationRequest.class)) {
                return getDiagnosticsStatusNotificationRequest();
            } else if (requestClass.equals(FirmwareStatusNotificationRequest.class)) {
                return getFirmwareStatusNotificationRequest();
            } else if (requestClass.equals(GetDiagnosticsRequest.class)) {
                return getGetDiagnosticsRequest();
            } else if (requestClass.equals(UpdateFirmwareRequest.class)) {
                return getUpdateFirmwareRequest();
            } else if (requestClass.equals(GetLocalListVersionRequest.class)) {
                return getGetLocalListVersionRequest();
            } else if (requestClass.equals(SendLocalListRequest.class)) {
                return getSendLocalListRequest();
            } else if (requestClass.equals(TriggerMessageRequest.class)) {
                return getTriggerMessageRequest();
            } else if (requestClass.equals(SetChargingProfileRequest.class)) {
                return getSetChargingProfileRequest();
            } else if (requestClass.equals(ClearChargingProfileRequest.class)) {
                return getClearChargingProfileRequest();
            } else if (requestClass.equals(com.omb.ocpp.server.iso15118.dto.TriggerMessageRequest.class)) {
                return getIso15118TriggerMessageRequest();
            } else if (requestClass.equals(SignedUpdateFirmwareRequest.class)) {
                return getSignedUpdateFirmwareRequest();
            } else if (requestClass.equals(InstallCertificateRequest.class)) {
                return getInstallCertificateRequest();
            } else if (requestClass.equals(ExtendedTriggerMessageRequest.class)) {
                return getExtendedTriggerMessageRequest();
            } else {
                return NOT_SUPPORTED;
            }
        } catch (PropertyConstraintException | JsonProcessingException e) {
            logger.error(REQUEST_CONSTRUCTION_ERROR, e);
            return REQUEST_CONSTRUCTION_ERROR;
        }
    }

    private static String getChangeAvailabilityRequest() throws JsonProcessingException {
        ChangeAvailabilityRequest changeAvailabilityRequest = new ChangeAvailabilityRequest(1, AvailabilityType.Operative);
        return objectMapper.writeValueAsString(changeAvailabilityRequest);
    }

    private static String getChangeConfigurationRequest() throws JsonProcessingException {
        ChangeConfigurationRequest changeConfigurationRequest = new ChangeConfigurationRequest("AuthorizationCacheEnabled", "false");
        return objectMapper.writeValueAsString(changeConfigurationRequest);
    }

    private static String getClearCacheRequest() throws JsonProcessingException {
        ClearCacheRequest clearCacheRequest = new ClearCacheRequest();
        return objectMapper.writeValueAsString(clearCacheRequest);
    }

    private static String getDataTransferRequest() throws JsonProcessingException {
        DataTransferRequest dataTransferRequest = new DataTransferRequest("VendorId");
        dataTransferRequest.setData("Data message");
        dataTransferRequest.setMessageId("MessageId");
        return objectMapper.writeValueAsString(dataTransferRequest);
    }

    private static String getGetConfigurationRequest() throws JsonProcessingException {
        GetConfigurationRequest getConfigurationRequest = new GetConfigurationRequest();
        getConfigurationRequest.setKey(new String[]{"AuthorizationCacheEnabled"});
        return objectMapper.writeValueAsString(getConfigurationRequest);
    }

    private static String getMeterValuesRequest() throws JsonProcessingException {
        SampledValue sampledValue = new SampledValue("100");
        sampledValue.setValue("100");
        MeterValue meterValue = new MeterValue(ZonedDateTime.now(),new SampledValue[]{sampledValue});
        MeterValuesRequest meterValuesRequest = new MeterValuesRequest(1);
        meterValuesRequest.setMeterValue(new MeterValue[]{meterValue});
        meterValuesRequest.setTransactionId(123456);
        return objectMapper.writeValueAsString(meterValuesRequest);
    }

    private static String getRemoteStartTransactionRequest() throws JsonProcessingException {
        RemoteStartTransactionRequest remoteStartTransactionRequest = new RemoteStartTransactionRequest("idTag");
        remoteStartTransactionRequest.setConnectorId(1);
        remoteStartTransactionRequest.setChargingProfile(new ChargingProfile());
        return objectMapper.writeValueAsString(remoteStartTransactionRequest);
    }

    private static String getRemoteStopTransactionRequest() throws JsonProcessingException {
        RemoteStopTransactionRequest remoteStopTransactionRequest = new RemoteStopTransactionRequest(123456);
        return objectMapper.writeValueAsString(remoteStopTransactionRequest);
    }

    private static String getResetRequest() throws JsonProcessingException {
        ResetRequest resetRequest = new ResetRequest(ResetType.Soft);
        return objectMapper.writeValueAsString(resetRequest);
    }

    private static String getUnlockConnectorRequest() throws JsonProcessingException {
        UnlockConnectorRequest unlockConnectorRequest = new UnlockConnectorRequest(1);
        return objectMapper.writeValueAsString(unlockConnectorRequest);
    }

    private static String getDiagnosticsStatusNotificationRequest() throws JsonProcessingException {
        DiagnosticsStatusNotificationRequest diagnosticsStatusNotificationRequest = new DiagnosticsStatusNotificationRequest(DiagnosticsStatus.Idle);
        return objectMapper.writeValueAsString(diagnosticsStatusNotificationRequest);
    }

    private static String getFirmwareStatusNotificationRequest() throws JsonProcessingException {
        FirmwareStatusNotificationRequest firmwareStatusNotificationRequest = new FirmwareStatusNotificationRequest(FirmwareStatus.Idle);
        return objectMapper.writeValueAsString(firmwareStatusNotificationRequest);
    }

    private static String getGetDiagnosticsRequest() throws JsonProcessingException {
        ZonedDateTime startDate = ZonedDateTime.now().plus(1, ChronoUnit.DAYS);
        ZonedDateTime stopDate = ZonedDateTime.now().plus(2, ChronoUnit.DAYS);
        GetDiagnosticsRequest getDiagnosticsRequest = new GetDiagnosticsRequest("ftp://localhost/downloadFolder");
        getDiagnosticsRequest.setRetries(2);
        getDiagnosticsRequest.setRetryInterval(5);
        getDiagnosticsRequest.setStartTime(startDate);
        getDiagnosticsRequest.setStopTime(stopDate);
        return objectMapper.writeValueAsString(getDiagnosticsRequest);
    }

    private static String getUpdateFirmwareRequest() throws JsonProcessingException {
        ZonedDateTime startDate = ZonedDateTime.now().plus(1, ChronoUnit.DAYS);
        UpdateFirmwareRequest updateFirmwareRequest = new UpdateFirmwareRequest("ftp://localhost/downloadFolder", startDate);
        updateFirmwareRequest.setRetries(2);
        updateFirmwareRequest.setRetryInterval(5);
        return objectMapper.writeValueAsString(updateFirmwareRequest);
    }

    private static String getSignedUpdateFirmwareRequest() throws JsonProcessingException {

        Calendar now = Calendar.getInstance();

        Calendar retrieveDate = (Calendar) now.clone();
        retrieveDate.add(Calendar.DAY_OF_MONTH, 1);

        Calendar installDate = (Calendar) now.clone();
        installDate.add(Calendar.DAY_OF_MONTH, 2);

        FirmwareType firmwareType = new FirmwareType();
        firmwareType.setSignature("signature");
        firmwareType.setSigningCertificate("signingCertificate");
        firmwareType.setLocation("ftp://localhost/downloadFolder");
        firmwareType.setRetrieveDateTime(retrieveDate);
        firmwareType.setInstallDateTime(installDate);

        SignedUpdateFirmwareRequest request = new SignedUpdateFirmwareRequest();
        request.setRequestId(1);
        request.setFirmware(firmwareType);
        request.setRetries(2);
        request.setRetryInterval(5);

        return objectMapper.writeValueAsString(request);
    }

    private static String getGetLocalListVersionRequest() throws JsonProcessingException {
        GetLocalListVersionRequest getLocalListVersionRequest = new GetLocalListVersionRequest();
        return objectMapper.writeValueAsString(getLocalListVersionRequest);
    }

    private static String getSendLocalListRequest() throws JsonProcessingException {
        SendLocalListRequest sendLocalListRequest = new SendLocalListRequest(0, null);
        return objectMapper.writeValueAsString(sendLocalListRequest);
    }

    private static String getTriggerMessageRequest() throws JsonProcessingException {
        TriggerMessageRequest triggerMessageRequest = new TriggerMessageRequest(TriggerMessageRequestType.Heartbeat);
        triggerMessageRequest.setConnectorId(1);
        return objectMapper.writeValueAsString(triggerMessageRequest);
    }

    private static String getExtendedTriggerMessageRequest() throws JsonProcessingException {
        ExtendedTriggerMessageRequest extendedTriggerMessageRequest = new ExtendedTriggerMessageRequest();
        extendedTriggerMessageRequest.setConnectorId(1);
        extendedTriggerMessageRequest.setMessageTriggerType(ExtendedTriggerMessage.SIGN_CHARGE_POINT_CERTIFICATE);
        return objectMapper.writeValueAsString(extendedTriggerMessageRequest);
    }

    private static String getSetChargingProfileRequest() throws JsonProcessingException {
        ChargingSchedulePeriod chargingSchedulePeriod1 = new ChargingSchedulePeriod(0, 20d);
        chargingSchedulePeriod1.setNumberPhases(3);

        ChargingSchedulePeriod chargingSchedulePeriod2 = new ChargingSchedulePeriod((int) ChronoUnit.DAYS.getDuration().getSeconds(), 30d);
        chargingSchedulePeriod2.setNumberPhases(3);

        ChargingProfileBuilder chargingProfileBuilder = new ChargingProfileBuilder()
                .withChargingProfileId(1234)
                .withChargingProfilePurposeType(ChargingProfilePurposeType.TxDefaultProfile)
                .withChargingProfileKindType(ChargingProfileKindType.Relative)
                .withChargingRateUnitType(ChargingRateUnitType.A)
                .withStackLevel(1)
                .withMinChargingRate(10d)
                .withValidFrom(Instant.now().minus(1, ChronoUnit.DAYS))
                .withValidTo(Instant.now().plus(1, ChronoUnit.DAYS))
                .withStartSchedule(Instant.now().minus(1, ChronoUnit.DAYS))
                .withChargingScheduleDuration((int) ChronoUnit.DAYS.getDuration().getSeconds() * 2)
                .withChargingSchedulePeriods(chargingSchedulePeriod1, chargingSchedulePeriod2);

        SetChargingProfileRequest setChargingProfileRequest = new SetChargingProfileRequest(1, chargingProfileBuilder.build());

        return objectMapper.writeValueAsString(setChargingProfileRequest);
    }

    private static String getClearChargingProfileRequest() throws JsonProcessingException {
        ClearChargingProfileRequest clearChargingProfileRequest = new ClearChargingProfileRequest();
        clearChargingProfileRequest.setConnectorId(0);
        clearChargingProfileRequest.setChargingProfilePurpose(ChargingProfilePurposeType.TxDefaultProfile);
        clearChargingProfileRequest.setId(1234);
        clearChargingProfileRequest.setStackLevel(1);

        return objectMapper.writeValueAsString(clearChargingProfileRequest);
    }

    private static String getIso15118TriggerMessageRequest() {
        com.omb.ocpp.server.iso15118.dto.TriggerMessageRequest triggerMessageRequest =
                new com.omb.ocpp.server.iso15118.dto.TriggerMessageRequest();
        triggerMessageRequest.setConnectorId(1);
        triggerMessageRequest.setRequestedMessage(MessageTrigger.SIGN_V2G_CERTIFICATE);

        return (String) jsonCommunicator.packPayload(triggerMessageRequest);
    }

    private static String getInstallCertificateRequest() {
        InstallCertificateRequest installCertificateRequest = new InstallCertificateRequest();
        installCertificateRequest.setCertificate("certificate");
        installCertificateRequest.setCertificateType("ROOT_CA");
        return (String) jsonCommunicator.packPayload(installCertificateRequest);
    }

    public static <T extends Request> Optional<T> toRequest(String request, Class<T> requestClass) {
        try {
            return Optional.of(objectMapper.readValue(request, requestClass));
        } catch (IOException e) {
            logger.error("Request parsing error", e);
            return Optional.empty();
        }
    }

    public static String toJson(Request request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            logger.error("Request parsing error", e);
            return "";
        }
    }

    private static class ChargingProfileBuilder {
        private int chargingProfileId;
        private Instant validFrom;
        private Instant validTo;
        private ChargingProfileKindType chargingProfileKindType;
        private ChargingProfilePurposeType chargingProfilePurposeType;
        private RecurrencyKindType recurrencyKindType;
        private int stackLevel;
        private Integer transactionId;

        private ChargingRateUnitType chargingRateUnitType;
        private int chargingScheduleDuration;
        private Double minChargingRate;
        private Instant startSchedule;

        private List<ChargingSchedulePeriod> chargingSchedulePeriods = new LinkedList<>();

        public ChargingProfileBuilder withChargingProfileId(int chargingProfileId) {
            this.chargingProfileId = chargingProfileId;
            return this;
        }

        public ChargingProfileBuilder withValidFrom(Instant validFrom) {
            this.validFrom = validFrom;
            return this;
        }

        public ChargingProfileBuilder withValidTo(Instant validTo) {
            this.validTo = validTo;
            return this;
        }

        public ChargingProfileBuilder withChargingProfileKindType(ChargingProfileKindType chargingProfileKindType) {
            this.chargingProfileKindType = chargingProfileKindType;
            return this;
        }

        public ChargingProfileBuilder withChargingProfilePurposeType(ChargingProfilePurposeType chargingProfilePurposeType) {
            this.chargingProfilePurposeType = chargingProfilePurposeType;
            return this;
        }

        public ChargingProfileBuilder withRecurrencyKindType(RecurrencyKindType recurrencyKindType) {
            this.recurrencyKindType = recurrencyKindType;
            return this;
        }

        public ChargingProfileBuilder withStackLevel(int stackLevel) {
            this.stackLevel = stackLevel;
            return this;
        }

        public ChargingProfileBuilder withTransactionId(int transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public ChargingProfileBuilder withChargingRateUnitType(ChargingRateUnitType chargingRateUnitType) {
            this.chargingRateUnitType = chargingRateUnitType;
            return this;
        }

        public ChargingProfileBuilder withChargingScheduleDuration(int chargingScheduleDuration) {
            this.chargingScheduleDuration = chargingScheduleDuration;
            return this;
        }

        public ChargingProfileBuilder withMinChargingRate(Double minChargingRate) {
            this.minChargingRate = minChargingRate;
            return this;
        }

        public ChargingProfileBuilder withStartSchedule(Instant startSchedule) {
            this.startSchedule = startSchedule;
            return this;
        }

        public ChargingProfileBuilder withChargingSchedulePeriods(ChargingSchedulePeriod... chargingSchedulePeriods) {
            this.chargingSchedulePeriods = Arrays.asList(chargingSchedulePeriods);
            return this;
        }

        public ChargingProfileBuilder withChargingSchedulePeriod(ChargingSchedulePeriod chargingSchedulePeriod) {
            this.chargingSchedulePeriods.add(chargingSchedulePeriod);
            return this;
        }

        public ChargingProfile build() {
            ChargingSchedule chargingSchedule = new ChargingSchedule(chargingRateUnitType, chargingSchedulePeriods.toArray(new ChargingSchedulePeriod[0]));
            chargingSchedule.setDuration(chargingScheduleDuration);
            chargingSchedule.setMinChargingRate(minChargingRate);
            chargingSchedule.setStartSchedule(ZonedDateTime.ofInstant(startSchedule, ZoneOffset.UTC));

            ChargingProfile chargingProfile = new ChargingProfile(chargingProfileId, stackLevel, chargingProfilePurposeType, chargingProfileKindType, chargingSchedule);
            chargingProfile.setTransactionId(transactionId);
            chargingProfile.setRecurrencyKind(recurrencyKindType);
            chargingProfile.setValidFrom(ZonedDateTime.ofInstant(validFrom, ZoneOffset.UTC));
            chargingProfile.setValidTo(ZonedDateTime.ofInstant(validTo, ZoneOffset.UTC));
            return chargingProfile;
        }
    }
}
