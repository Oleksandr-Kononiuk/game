package com.game.repository;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;

@Repository
public class CustomPlayerRepositoryImpl implements CustomPlayerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Player> getAllWithFilters(PlayerFilterCriteria playerFilterCriteria,
                                          PlayerOrder order, Integer pageNumber, Integer pageSize) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Player> cq = cb.createQuery(Player.class);
        Root<Player> root = cq.from(Player.class);

        Sort sort = Sort.by(Sort.Direction.ASC, order.getFieldName());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        List<Predicate> predicates = getPredicates(playerFilterCriteria, root, cb);

        cq.select(root)
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.asc(root.get(order.getFieldName())));

        TypedQuery<Player> typedQuery = entityManager.createQuery(cq);
        typedQuery.setFirstResult((int)pageable.getOffset());
        typedQuery.setMaxResults(pageSize);

        return typedQuery.getResultList();
    }

    @Override
    public Integer countPlayersWithFilters(PlayerFilterCriteria filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Player> countRoot = countQuery.from(Player.class);

        List<Predicate> predicates = getPredicates(filter, countRoot, cb);

        countQuery.select(cb.count(countRoot))
                .where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult().intValue();
    }

    private List<Predicate> getPredicates(PlayerFilterCriteria filter, Root<Player> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(filter.getName())) {
            predicates.add(cb.like(root.get("name"), "%" + filter.getName() + "%"));
        }
        if (Objects.nonNull(filter.getTitle())) {
            predicates.add(cb.like(root.get("title"), "%" + filter.getTitle() + "%"));
        }
        if (Objects.nonNull(filter.getRace())) {
            predicates.add(cb.equal(root.<String>get("race"), filter.getRace()));
        }
        if (Objects.nonNull(filter.getProfession())) {
            predicates.add(cb.equal(root.<String>get("profession"), filter.getProfession()));
        }
        if (Objects.nonNull(filter.getBefore()) && Objects.nonNull(filter.getAfter())) {
            predicates.add(cb.between(root.get("birthday"), new Date(filter.getAfter()), new Date(filter.getBefore())));
        }
        if (Objects.nonNull(filter.getBefore()) && Objects.isNull(filter.getAfter())) {
            predicates.add(cb.lessThan(root.get("birthday"), new Date(filter.getBefore())));
        }
        if (Objects.isNull(filter.getBefore()) && Objects.nonNull(filter.getAfter())) {
            predicates.add(cb.greaterThan(root.get("birthday"), new Date(filter.getAfter())));
        }
        if (Objects.nonNull(filter.getBanned())) {
            predicates.add(cb.equal(root.<Boolean>get("banned"), filter.getBanned()));
        }
        if (Objects.nonNull(filter.getMinExperience()) && Objects.nonNull(filter.getMaxExperience())) {
            predicates.add(cb.between(root.get("experience"), filter.getMinExperience(), filter.getMaxExperience()));
        }
        if (Objects.nonNull(filter.getMinExperience()) && Objects.isNull(filter.getMaxExperience())) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("experience"), filter.getMinExperience()));
        }
        if (Objects.isNull(filter.getMinExperience()) && Objects.nonNull(filter.getMaxExperience())) {
            predicates.add(cb.lessThanOrEqualTo(root.get("experience"), filter.getMaxExperience()));
        }
        if (Objects.nonNull(filter.getMinLevel()) && Objects.nonNull(filter.getMaxLevel())) {
            predicates.add(cb.between(root.get("level"), filter.getMinLevel(), filter.getMaxLevel()));
        }
        if (Objects.nonNull(filter.getMinLevel()) && Objects.isNull(filter.getMaxLevel())) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("level"), filter.getMinLevel()));
        }
        if (Objects.isNull(filter.getMinLevel()) && Objects.nonNull(filter.getMaxLevel())) {
            predicates.add(cb.lessThanOrEqualTo(root.get("level"), filter.getMaxLevel()));
        }
        return predicates;
    }
}
