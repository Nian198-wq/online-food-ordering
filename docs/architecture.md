# 系统架构

## 分层结构

```mermaid
flowchart LR
    Browser[顾客端 / 商家端] --> MVC[Spring MVC REST API]
    MVC --> Service[订单 / 菜品 / 配送 / 秒杀服务]
    Service --> JPA[Spring Data JPA]
    JPA --> DB[(H2 / MySQL)]
```

## 下单流程

```mermaid
sequenceDiagram
    participant C as 顾客端
    participant O as OrderService
    participant D as DishesRepository
    participant DB as 数据库

    C->>O: POST /api/orders + clientRequestId
    O->>DB: 查询幂等请求号
    alt 请求号已存在
        DB-->>O: 返回已有订单
        O-->>C: 返回同一订单
    else 新订单
        O->>D: 条件更新并扣减库存
        alt 库存充足
            D-->>O: 更新 1 行
            O->>DB: 保存订单和订单项
            O-->>C: 创建成功
        else 库存不足
            D-->>O: 更新 0 行
            O-->>C: 400 + 库存不足
            Note over O,DB: 整个事务回滚
        end
    end
```

## 订单状态机

```mermaid
stateDiagram-v2
    [*] --> PENDING
    PENDING --> CONFIRMED
    PENDING --> CANCELLED
    CONFIRMED --> PREPARING
    CONFIRMED --> CANCELLED
    PREPARING --> DELIVERING
    PREPARING --> CANCELLED
    DELIVERING --> COMPLETED
    COMPLETED --> [*]
    CANCELLED --> [*]
```

取消订单会原子回补库存，已支付订单同时标记为 `REFUNDED`。非法状态跳转会被服务层拒绝。

## 一致性设计

- 库存扣减和订单写入使用同一个事务。
- 库存更新包含 `stock >= quantity` 条件，避免并发超卖。
- `clientRequestId` 在数据库层唯一，防止重复提交。
- 单元与集成测试覆盖库存不足回滚、幂等和 12 个并发下单请求。
