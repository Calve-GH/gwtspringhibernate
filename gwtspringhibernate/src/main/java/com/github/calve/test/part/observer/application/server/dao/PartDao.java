package com.github.calve.test.part.observer.application.server.dao;

import com.github.calve.test.part.observer.application.server.entity.Part;

import java.util.List;

public interface PartDao {

    long savePart(Part part);

    List<Part> getAll();

    void deletePart(Part part);

    void updatePart(Part part);
}