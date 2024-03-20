package garden.carrot.mqttclient.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "mothership_users")
public class MothershipUser {

    @Id
    private UUID uid;

    @Indexed(unique = true)
    private String name;

    private String color;
    private String icon;

    public MothershipUser() {
        // Default constructor required for deserialization
    }

    public MothershipUser(String name, String color, String icon) {
        this.uid = UUID.randomUUID(); // Generate UUID for uid
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    public MothershipUser(String name) {
        this.uid = UUID.randomUUID(); // Generate UUID for uid
        this.name = name;
        this.color = "gray";
        this.icon = "default";
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
