package moodle.sync.javafx.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;

import static java.util.Objects.isNull;

/**
 * Class used to parse between UNIX-Timestamps and the Moodle availability.
 */
public class TimeDateElement {
    private ObjectProperty<LocalDate> localDate;
    private ObjectProperty<LocalTime> localTime;

    public TimeDateElement(LocalDate localDate, LocalTime localTime) {
        this.localDate = new SimpleObjectProperty(localDate);
        this.localTime = new SimpleObjectProperty(localTime);
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate.set(localDate);
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime.set(localTime);
    }

    public LocalDate getLocalDate() {
        return localDate.get();
    }

    public LocalTime getLocalTime() {
        if(isNull(localTime.get())){
            return LocalTime.of(0, 0);
        }
        return localTime.get();
    }

    public ObjectProperty<LocalDate> LocalDateProperty() {
        return localDate;
    }

    public ObjectProperty<LocalTime> LocalTimeProperty() {
        return localTime;
    }
}
