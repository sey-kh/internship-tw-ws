package order.service.impl;

import order.entity.Order;
import order.service.OrderRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    @Autowired
    private EntityManagerFactory emf;

    @Override
    public List<Order> findOrdersByAccountAndSymbol(String account, String symbol) {
        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> order = cq.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        if (symbol.length() == 0) {
            predicates.add(cb.equal(order.get("account"), account));

        }
        else{
            predicates.add(cb.equal(order.get("account"), account));
            predicates.add(cb.equal(order.get("symbol"), symbol));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public void updateQuantity(int quantity, String orderId) {
        EntityManager em = emf.createEntityManager();
        Order order = em.find(Order.class, orderId);
        em.getTransaction().begin();
        order.setQuantity(quantity);
        em.getTransaction().commit();

    }

    @Override
    public void createOrder(Order order) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(order);
        em.getTransaction().commit();
    }

}
