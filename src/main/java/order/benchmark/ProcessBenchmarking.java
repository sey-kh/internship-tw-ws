package order.benchmark;

import order.WsApplication;
import order.constant.Consts;
import order.io.entity.ComplexOrder;
import order.io.repository.ComplexOrderRepository;
import order.model.request.OrderReqDetailsModel;
import order.service.OrderService;
import order.shared.Utils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)

public class ProcessBenchmarking {

    @Param({"100", "1000","10000"})
    private int N;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(ProcessBenchmarking.class.getSimpleName())
                .forks(0)
                .warmupIterations(2)
                .measurementIterations(2)
                .build();

        new Runner(opt).run();
    }

    private static ConfigurableApplicationContext context;
    private static ComplexOrderRepository complexOrderRepository;
    private static OrderService orderService;

    @Setup
    public void initialize() {

        String args = "Initialize Benchmarking";
        if (context == null) {
            context = SpringApplication.run(WsApplication.class, args);
        }

        complexOrderRepository = context.getBean(ComplexOrderRepository.class);
        orderService = context.getBean(OrderService.class);

        for (int i = 0; i < N; i++) {
            ComplexOrder order = makeComplexOrder("acc_1",
                    true, BigDecimal.valueOf(100), "aapl", 100, Consts.BUY, 100);
            complexOrderRepository.save(order);
        }
    }

    @Benchmark
    public void addSimpleOrder(Blackhole bh) {
        OrderReqDetailsModel req = new OrderReqDetailsModel();

        req.setAccount("acc_1");
        req.setBuy(true);
        req.setQuantity(100);
        req.setSymbol("aapl");
        req.setLimitPrice(BigDecimal.valueOf(100));

        orderService.addOrder(req);
    }


    private ComplexOrder makeComplexOrder(String acc,
                                          Boolean buy, BigDecimal limitPrice, String symbol, Integer quantity,
                                          String side, Integer minQuantity) {

        ComplexOrder order = new ComplexOrder();

        order.setAccount(acc);
        order.setBuy(buy);
        order.setLimitPrice(limitPrice);
        order.setSymbol(symbol);
        order.setQuantity(quantity);

        // update order properties where need be to generated by system
        String orderId = Utils.generateRandomString();
        order.setOrderId(orderId);
        order.setOrderDate(Utils.getDateNow());
        order.setModifiedDate(Utils.getDateNow());
        order.setStatus(Consts.CONFIRMED);

        order.setActivation(Consts.ByOtherOrder);
        order.setSide(side);
        order.setMinQuantity(minQuantity);

        return order;
    }
}


