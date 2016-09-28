package epizza.order.checkout;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import lombok.RequiredArgsConstructor;

import static java.util.Collections.emptyList;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements
        OrderRepositoryWithNamedQuery
// SCHNIPP
        , OrderRepositoryWithCriteraQuery
// SCHNAPP
{

    private final EntityManager entityManager;

    @Override
    public Page<Order> findByNamedQuery(String name, Pageable pageable) {
        TypedQuery<Order> query = entityManager.createNamedQuery(name, Order.class);
        Long total = countByNamedQuery(name + ".count");
        return readPage(query, pageable, total);
    }

    @Override
    public Long countByNamedQuery(String name) {
        TypedQuery<Long> query = entityManager.createNamedQuery(name, Long.class);
        return query.getSingleResult();
    }

    private Page<Order> readPage(TypedQuery<Order> query, Pageable pageable, Long total) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Order> content = total > pageable.getOffset() ? query.getResultList() : emptyList();
        return new PageImpl<>(content, pageable, total);
    }

// SCHNIPP
    @Override
    public Page<Order> findUnassigned(Pageable pageable) {
        CriteriaQuery<Order> criteria = entityManager.getCriteriaBuilder().createQuery(Order.class);
        Root<Order> orders = criteria.from(Order.class);
        Path<String> deliveryBoy = orders.get("deliveryBoy");

        criteria.select(orders).where(isNull(deliveryBoy));

        TypedQuery<Order> query = entityManager.createQuery(criteria);
        Long total = countUnassigned();
        return readPage(query, pageable, total);
    }

    @Override
    public Long countUnassigned() {
        CriteriaQuery<Long> criteria = entityManager.getCriteriaBuilder().createQuery(Long.class);
        Root<Order> orders = criteria.from(Order.class);
        Path<String> deliveryBoy = orders.get("deliveryBoy");

        criteria.select(count(orders)).where(isNull(deliveryBoy));

        TypedQuery<Long> query = entityManager.createQuery(criteria);
        return query.getSingleResult();
    }

    private Predicate isNull(Path<?> path) {
        return entityManager.getCriteriaBuilder().isNull(path);
    }

    private Expression<Long> count(Root<?> root) {
        return entityManager.getCriteriaBuilder().count(root);
    }
// SCHNAPP
}
