package local.wallet.analyzing.model;

import java.io.Serializable;
import java.util.Calendar;

import local.wallet.analyzing.R;

/**
 * Created by huynh.thanh.huan on 2/22/2016.
 */
public class Event implements Serializable {

    private int         id;
    private String      name;
    private Calendar    startDate;
    private Calendar    endDate;

    public Event() { }

    public Event(int id, String name, Calendar startDate, Calendar endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", startDate = " + (startDate != null ? (startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1) + "/" + startDate.get(Calendar.YEAR)) : "null") +
                ", endDate = "   + (endDate != null ? (endDate.get(Calendar.DAY_OF_MONTH) + "/" + (endDate.get(Calendar.MONTH) + 1) + "/" + endDate.get(Calendar.YEAR)) : "null") +
                '}';
    }
}
