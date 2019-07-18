package order.service;

import order.entity.ComplexOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ComplexOrderRepository extends JpaRepository<ComplexOrder, String> {

    @Query(value = "SELECT o FROM ComplexOrder o " +
            "WHERE o.symbol = :symbol AND o.side <> :side " +
            "AND o.minQuantity <= :quantity AND o.status = 'confirmed'")
    List<ComplexOrder> findAllByParams(@Param("symbol") String symbol,
                                       @Param("side") String side,
                                       @Param("quantity") Integer quantity);


    @Query(value = "SELECT o FROM ComplexOrder o WHERE " +
            "o.activationDate <= :currentDate AND o.status = 'confirmed'")
    List<ComplexOrder> findAllWithCurrentDateBefore(
            @Param("currentDate") Date currentDate);

}
