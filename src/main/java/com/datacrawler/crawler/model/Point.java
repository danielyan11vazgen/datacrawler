package com.datacrawler.crawler.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: Vazgen Danielyan
 * Date: 11/29/17
 * Time: 12:21 PM
 */
public class Point implements Serializable {

    private static final long serialVersionUID = -8437115665700317340L;

    //region Properties

    private final String name;

    private final BigDecimal latitude;

    private final BigDecimal longitude;

    //endregion

    //region Constructor

    public Point(final String name, final BigDecimal latitude, final BigDecimal longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //endregion

    //region Getters and setters

    public String getName() {
        return name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    //endregion

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("latitude", latitude)
                .append("longitude", longitude)
                .toString();
    }

}
