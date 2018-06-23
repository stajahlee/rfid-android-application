package refried.rfid_inventory.database;

import android.support.annotation.NonNull;

import com.google.common.collect.ComparisonChain;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ServerValue;

import java.util.Map;
import java.util.Objects;

/**
 * Object to represent one inventory audit.
 */

public class InventoryAudit implements Comparable<InventoryAudit> {

    public enum Field {
        DEFAULT("default"),
        UID("item_uid"),
        SEEN_DATE("seen_date"),
        USER("seen_by_user"),
        LOCATION("location"),
        ;

        private final String field;

        Field(String field) {
            this.field = field;
        }

        public String getTextField() {
            return field;
        }
    }

    private String uid;
    private Long seenDate;
    private String user;
    private String location;

    private InventoryAudit() {

    }

    @PropertyName("item_uid")
    public String getUid() {
        return uid;
    }

    @PropertyName("item_uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    @PropertyName("seen_date")
    public Map<String, String> getSeenDate() {
        return ServerValue.TIMESTAMP;
    }

    @Exclude
    // Exclude from Firebase; use above when setting properties
    public Long getSeenDateLong() {
        return seenDate;
    }

    @PropertyName("seen_date")
    public void setSeenDate(Long seenDate) {
        this.seenDate = seenDate;
    }

    @PropertyName("seen_by_user")
    public String getUser() {
        return user;
    }

    @PropertyName("seen_by_user")
    public void setUser(String user) {
        this.user = user;
    }

    @PropertyName("location")
    public String getLocation() {
        return location;
    }

    @PropertyName("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if (!InventoryAudit.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        InventoryAudit other = (InventoryAudit) o;
        return (uid.equals(other.uid)
                && seenDate.equals(other.seenDate));
    }

    @Override
    public int hashCode () {
        return Objects.hash(uid, seenDate, user, location);
    }

    @Override
    public int compareTo(@NonNull InventoryAudit otherAudit) {
        return ComparisonChain.start()
                .compare(uid, otherAudit.uid)
                .compare(seenDate, otherAudit.seenDate)
                .result();
    }

    /**
     * Builder for audits, to make construction easier.
     */
    public static class Builder {
        private InventoryAudit MANAGED_INSTANCE = new InventoryAudit();

        /**
         * Build an {@link InventoryAudit} from this {@link Builder}
         * @return The generated {@link InventoryAudit}
         */
        public InventoryAudit build() {
            return MANAGED_INSTANCE;
        }

        /**
         * Set the item unique ID
         * @return A chained {@link Builder}
         */
        public Builder withItemUID(String uid) {
            MANAGED_INSTANCE.setUid(uid);
            return this;
        }

        /**
         * Set the date of this audit
         * @param date A date string to parse
         * @return A chained {@link Builder}
         */
        public Builder seenOn(Long date) {
            MANAGED_INSTANCE.setSeenDate(date);
            return this;
        }

        /**
         * Set the user who conducted this audit
         * @return A chained {@link Builder}
         */
        public Builder by(String user) {
            MANAGED_INSTANCE.setUser(user);
            return this;
        }

        /**
         * Set the location this objected was located
         * @return A chained {@link Builder}
         */
        public Builder locatedAt(String location) {
            MANAGED_INSTANCE.setLocation(location);
            return this;
        }

    }
}
