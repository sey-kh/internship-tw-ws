package order.io.repository;

import order.io.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByAccount(String account);

    List<Order> findByAccountAndSymbol(String account, String symbol);
}
