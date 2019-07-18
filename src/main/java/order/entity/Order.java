package order.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "order_table")
public class Order extends BaseOrder{
}
