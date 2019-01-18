package com.github.calve.test.part.observer.application.server.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "part")
public class Part {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="PART_ID")
    private long partId;
    @Column(name="PART_NAME")
    private String partName;
    @Column(name="PART_REQUIRED")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean partRequired;
    @Column(name="PART_QUANTITY")
    private int partQuantity;

    public long getPartId() {
        return partId;
    }

    public void setPartId(long partId) {
        this.partId = partId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public boolean isPartRequired() {
        return partRequired;
    }

    public void setPartRequired(boolean partRequired) {
        this.partRequired = partRequired;
    }

    public int getPartQuantity() {
        return partQuantity;
    }

    public void setPartQuantity(int partQuantity) {
        this.partQuantity = partQuantity;
    }

    @Override
    public String toString() {
        return "Part{" +
                "partId=" + partId +
                ", partName='" + partName + '\'' +
                ", partRequired=" + partRequired +
                ", partQuantity=" + partQuantity +
                '}';
    }
}
