package order.service.impl;

import order.entity.Order;
import order.exceptions.OrderNotFoundException;
import order.exceptions.UserError;
import order.service.OrderRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    @Autowired
    private EntityManagerFactory emf;

    public String getDateNow() {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        return now;
    }

    @Override
    public List<Order> findOrdersByAccountAndSymbol(String account, String symbol) {

        EntityManager em = emf.createEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> order = cq.from(Order.class);
        cq.orderBy(cb.desc(order.get("orderDate")));

        List<Predicate> predicates = new ArrayList<>();

        // Define Predicate conditions
        if (symbol.length() == 0) {
            predicates.add(cb.equal(order.get("account"), account));

        } else {
            predicates.add(cb.equal(order.get("account"), account));
            predicates.add(cb.equal(order.get("symbol"), symbol));
        }
        cq.where(predicates.toArray(new Predicate[0]));

        List<Order> results = em.createQuery(cq).getResultList();
        em.close();

        return results;
    }

    @Override
    public Order updateQuantity(int quantity, String orderId) {
        EntityManager em = emf.createEntityManager();

        try {
            Order order = em.find(Order.class, orderId);
            if (order.getStatus() == "confirmed") {
                em.getTransaction().begin();
                order.setQuantity(quantity);
                order.setModifiedDate(this.getDateNow());
                em.getTransaction().commit();
                em.close();
                return order;
            } else {
                throw new UserError("You tried to update to cancelled order");
            }

        } catch (NullPointerException e) {
            em.close();
            throw new OrderNotFoundException("There is no id-" + orderId);
        }

    }

    @Override
    public String createOrder(Order order) {

        String uniqueID = UUID.randomUUID().toString();
        order.setOrderId(uniqueID);

        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            em.persist(order);
            em.getTransaction().commit();
            em.close();
        }
        catch (Exception e){
            em.close();
            throw e;
        }
        return uniqueID;
    }

    @Override
    public Order cancelOrder(String orderId) {
        EntityManager em = emf.createEntityManager();

        try {
            Order order = em.find(Order.class, orderId);
            em.getTransaction().begin();
            order.setStatus("cancelled");
            order.setModifiedDate(this.getDateNow());
            em.getTransaction().commit();
            em.close();
            return order;
        } catch (NullPointerException e) {
            em.close();
            throw new OrderNotFoundException("id-" + orderId);
        }
    }

}
