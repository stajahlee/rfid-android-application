package refried.rfid_inventory.database;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.PropertyName;

import java.util.Objects;

/**
 * Inventory Item Object
 */

public class InventoryItem implements Parcelable {

    public enum Field {
        DEFAULT("default"),
        NAME("name"),
        RFID_TAG_NUMBER("rfid_tag_num"),
        DESCRIPTION("description"),
        SERIAL_NUM("serial_num"),
        ORIGINAL_PRICE("original_price"),
        LAST_LOCATION("last_location"),
        TAG_COLOR("tag_color"),
        PHOTOGRAPH("photograph"),
        UNIQUE_ID("unique_id"),
        STATUS("status"),
        NAME_QUERYABLE("name_queryable")
        ;

        private final String field;

        Field(String field) {
            this.field = field;
        }

        public String getTextField() {
            return field;
        }
    }

    public enum TagColors {
        ANY("any"),
        RED("red"),
        GREEN("green"),
        NONE("none"),
        ;

        private final String tag;

        TagColors(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    private String name;
    private String rfid_tag_num;
    private String description;
    private String serial_num;
    private String original_price;
    private String last_location;
    private String tag_color;
    private String photograph;
    private String unique_id;
    private String status;
    private String name_queryable;

    public InventoryItem() {} // Empty Constructor required by Firebase

    public InventoryItem(String name, String rfid_tag_num, String description,
                         String serial_num, String original_price, String last_location,
                         String tag_color, String photoUrl, String unique_id, String status,
                         String name_queryable) {
        this.name = name;
        this.rfid_tag_num = rfid_tag_num;
        this.description = description;
        this.serial_num = serial_num;
        this.original_price = original_price;
        this.last_location = last_location;
        this.tag_color = tag_color;
        this.photograph = photoUrl;
        this.unique_id = unique_id;
        this.status = status;
        this.name_queryable = name_queryable;
    }

    // Parcel constructor:
    public InventoryItem(Parcel in) {
        name = in.readString();
        rfid_tag_num = in.readString();
        description = in.readString();
        serial_num = in.readString();
        original_price = in.readString();
        last_location = in.readString();
        tag_color = in.readString();
        photograph = in.readString();
        unique_id = in.readString();
        status = in.readString();
        name_queryable = in.readString();
    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("rfid_tag_num")
    public String getRfid_tag_num() {
        return rfid_tag_num;
    }

    @PropertyName("rfid_tag_num")
    public void setRfid_tag_num(String num) {
        this.rfid_tag_num = num;
    }

    @PropertyName("description")
    public String getDescription() {
        return description;
    }

    @PropertyName("description")
    public void setDescription(String desc) {
        this.description = desc;
    }

    @PropertyName("serial_num")
    public String getSerial_num() {
        return serial_num;
    }

    @PropertyName("serial_num")
    public void setSerial_num(String num) {
        this.serial_num = num;
    }

    @PropertyName("original_price")
    public String getOriginal_price() {
        return original_price;
    }

    @PropertyName("original_price")
    public void setOriginal_price(String price) {
        this.original_price = price;
    }

    @PropertyName("last_location")
    public String getLast_location() {
        return last_location;
    }

    @PropertyName("last_location")
    public void setLast_location(String loc) { this.last_location = loc; }

    @PropertyName("tag_color")
    public String getTag_color() {
        return tag_color;
    }

    @PropertyName("tag_color")
    public void setTag_color(String color) {
        this.tag_color = color;
    }

    @PropertyName("photograph")
    public String getPhotograph() {
        return photograph;
    }

    @PropertyName("photograph")
    public void setPhotoUrl(String photoUrl) {
        this.photograph = photoUrl;
    }

    @PropertyName("unique_id")
    public String getUnique_id() {
        return unique_id;
    }

    @PropertyName("unique_id")
    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("name_queryable")
    public String getName_queryable() {
        return name_queryable;
    }

    @PropertyName("name_queryable")
    public void setName_queryable(String name_queryable) {
        this.name_queryable = name_queryable;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Write data in any order
        dest.writeString(name);
        dest.writeString(rfid_tag_num);
        dest.writeString(description);
        dest.writeString(serial_num);
        dest.writeString(original_price);
        dest.writeString(last_location);
        dest.writeString(tag_color);
        dest.writeString(photograph);
        dest.writeString(unique_id);
        dest.writeString(status);
        dest.writeString(name_queryable);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // This is to de-serialize the object
    public static final Parcelable.Creator<InventoryItem> CREATOR = new Parcelable.Creator<InventoryItem>(){
        public InventoryItem createFromParcel(Parcel in) {
            return new InventoryItem(in);
        }

        public InventoryItem[] newArray(int size) {
            return new InventoryItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if(this == o) {
            return true;
        }
        if(!(o instanceof InventoryItem)) {
            return false;
        }
        InventoryItem it = (InventoryItem) o;
        return unique_id.equals(it.unique_id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(unique_id);
    }

    public static class Builder {
        private InventoryItem MANAGED_INSTANCE = new InventoryItem();

        public Builder() {}

        public InventoryItem build() {
            return MANAGED_INSTANCE;
        }

        public Builder named(String name) {
            MANAGED_INSTANCE.setName(name) ;
            return this;
        }

        public Builder withRFIDTag(String rfidTag) {
            MANAGED_INSTANCE.setRfid_tag_num(rfidTag); ;
            return this;
        }

        public Builder withDescription(String desc) {
            MANAGED_INSTANCE.setDescription(desc); ;
            return this;
        }

        public Builder withSerialNum(String num) {
            MANAGED_INSTANCE.setSerial_num(num);
            return this;
        }

        public Builder withPrice(String price) {
            MANAGED_INSTANCE.setOriginal_price(price);
            return this;
        }

        public Builder location(String loc) {
            MANAGED_INSTANCE.setLast_location(loc);
            return this;
        }

        public Builder withTagColor(String color) {
            MANAGED_INSTANCE.setTag_color(color);
            return this;
        }

        public Builder withPhotographURL(String photo) {
            MANAGED_INSTANCE.setPhotoUrl(photo);
            return this;
        }

        public Builder withUniqueID(String id) {
            MANAGED_INSTANCE.setUnique_id(id);
            return this;
        }

        public Builder withStatus(String status) {
            MANAGED_INSTANCE.setStatus(status);
            return this;
        }

        public Builder withNameQueryable(String nameLowercase) {
            MANAGED_INSTANCE.setName_queryable(nameLowercase);
            return this;
        }
    }
}