package com.github.calve.test.part.observer.application.server.dao;

import com.github.calve.test.part.observer.application.server.entity.Part;
import org.hibernate.ReplicationMode;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Repository("partDao")
@Transactional
public class PartDaoImpl extends AbstractDao implements PartDao {
    @Override
    public long savePart(Part part) {
        getSession().save(part);
        return part.getPartId();
    }

    @Override
    public List<Part> getAll() {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<Part> query = builder.createQuery(Part.class);
        Root<Part> root = query.from(Part.class);
        query.select(root);
        Query<Part> q = getSession().createQuery(query);
        List<Part> list = q.getResultList();
        return list;
    }

    @Override
    public void deletePart(Part part) {
        Part partOnDelete = getSession().get(Part.class, part.getPartId());
        if (partOnDelete != null) {
            getSession().delete(partOnDelete);
        }
    }

    @Override
    public void updatePart(Part part) {
        getSession().replicate(part, ReplicationMode.OVERWRITE);
    }
}
