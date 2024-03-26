package garden.carrot.mqttclient.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import garden.carrot.mqttclient.model.MothershipUser;

import java.util.List;

public class UsersMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String convertUsersToJson(List<MothershipUser> users) {
        try {
            return objectMapper.writeValueAsString(users);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
