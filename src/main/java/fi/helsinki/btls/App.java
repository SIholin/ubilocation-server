package fi.helsinki.btls;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fi.helsinki.ubipositioning.datamodels.Beacon;
import fi.helsinki.ubipositioning.datamodels.Location;
import fi.helsinki.ubipositioning.datamodels.Observation;
import fi.helsinki.ubipositioning.datamodels.Observer;
import fi.helsinki.ubipositioning.mqtt.IMqttService;
import fi.helsinki.ubipositioning.mqtt.MqttService;
import fi.helsinki.ubipositioning.trilateration.ILocationService;
import fi.helsinki.ubipositioning.trilateration.LocationService;
import fi.helsinki.ubipositioning.trilateration.RssiToMilliMeters;
import fi.helsinki.ubipositioning.utils.IObserverService;
import fi.helsinki.ubipositioning.utils.IResultConverter;
import fi.helsinki.ubipositioning.utils.ObservationGenerator;
import fi.helsinki.ubipositioning.utils.ObserverService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

public class App {
    private static Map<String, Beacon> beacons;

    public static void main(String[] args) {
        Gson gson = createGson();
        beacons = new HashMap<>();

        PropertiesHandler mqttConfig = new PropertiesHandler("config/mqttConfig.properties");

        String subscribeTopic = mqttConfig.getProperty("subscribeTopic");
        String publishTopic = mqttConfig.getProperty("publishTopic");
        String mqttUrl = mqttConfig.getProperty("mqttUrl");
        boolean debug = Boolean.parseBoolean(mqttConfig.getProperty("debug"));

        IMqttService observationData = new MqttService(mqttUrl, subscribeTopic, publishTopic);
        observationData.connect(s -> {
            try {
                Observation obs = gson.fromJson(s, Observation.class);
                addObservation(obs);
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        });

        PropertiesHandler appConfig = new PropertiesHandler("config/appConfig.properties");
        int positionsDimension = Boolean.parseBoolean(appConfig.getProperty("threeDimensional")) ? 3 : 2;

        IObserverService observerService = new ObserverService(positionsDimension);
        PropertiesHandler observerConfig = new PropertiesHandler("config/rasps.properties");

        List<Observer> all = new ArrayList<>();
        List<String> observerKeys = new ArrayList<>();

        String regexForRasp = "/";
        observerConfig.getAllProperties().forEach((key, value) -> {
            String[] rasp = value.split(regexForRasp);
            double[] temp = new double[positionsDimension];

            for (int i = 0; i < positionsDimension; i++) {
                temp[i] = Double.parseDouble(rasp[i]);
            }

            Observer obs = new Observer(key);
            obs.setPosition(temp);
            all.add(obs);

            observerKeys.add(key);
        });

        if (!observerService.addAllObservers(all)) {
            return;
        }

        String config = mqttConfig.getProperty("observerConfigTopic");
        String configStatus = mqttConfig.getProperty("observerConfigStatusTopic");

        IMqttService observerData = new MqttService(mqttUrl, config, configStatus);
        Map<String, String> keysConfig = new PropertiesHandler("config/keys.properties").getAllProperties();

        StringBuilder keyBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(keysConfig.get("configPublicKey")))) {
            stream.forEach(keyBuilder::append);
        } catch (IOException e) {
            System.out.println("file not found: " + e.toString());
        }

        observerData.connect(s -> {
            try {
                Observer[] obs = gson.fromJson(s, Observer[].class);
                String message;

                if (observerService.addAllObservers(Arrays.asList(obs))) {
                    message = "success";

                    for (Observer observer : obs) {
                        double[] position = observer.getPosition();
                        String pos = position[0] + regexForRasp + position[1] + regexForRasp + position[2];
                        observerConfig.saveProperty(observer.getObserverId(), pos);
                    }

                    observerConfig.persistProperties();
                } else {
                    message = "error";
                }

                observerData.publish(message);
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        });

        IResultConverter resultAs = positionsDimension == 3 ? new ResultAs3D() : new ResultAs2D();

        ObservationGenerator obsMock = new ObservationGenerator(12, 30, observerKeys);
        ILocationService service = new LocationService(observerService,
                new RssiToMilliMeters(2), resultAs);

        while (true) {
            try {
                Thread.sleep(1000);
                List<Beacon> data;

                if (debug) {
                    data = obsMock.getBeacons();
                } else {
                    data = new ArrayList<>(beacons.values());
                }

                List<Location> locations = new ArrayList<>();
                for (Beacon b : data) {
                    try {
                        Location location = service.calculateLocation(b);
                        locations.add(location);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                    }
                }

                observationData.publish(gson.toJson(locations));
            } catch (Exception ex) {
                System.out.println(ex.toString());
            }
        }
    }

    /**
     * Creates Gson instance that serializes data that doesn't fit into JSONs specs.
     *
     * @return Gson object.
     */
    private static Gson createGson() {
        GsonBuilder gb = new GsonBuilder();
        gb.serializeSpecialFloatingPointValues();
        gb.enableComplexMapKeySerialization();
        gb.serializeNulls();
        gb.setLenient();

        return gb.create();
    }

    private static void addObservation(Observation observation) {
        if (!beacons.containsKey(observation.getBeaconId())) {
            int observationLifetime = 5000;
            beacons.put(observation.getBeaconId(), new Beacon(observation.getBeaconId(), observationLifetime));
        }

        Beacon beacon = beacons.get(observation.getBeaconId());
        List<Observation> observations = beacon.getObservations();

        int maxMessages = 10000;
        if (observations.size() >= maxMessages) {
            observations.remove(0);
        }

        observation.setTimestamp(LocalDateTime.now());
        observations.add(observation);
        beacon.setObservations(observations);
    }
}
