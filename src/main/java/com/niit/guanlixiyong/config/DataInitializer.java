package com.niit.guanlixiyong.config;

import com.niit.guanlixiyong.entity.Dishes;
import com.niit.guanlixiyong.entity.SeckillActivity;
import com.niit.guanlixiyong.entity.SeckillItem;
import com.niit.guanlixiyong.entity.Users;
import com.niit.guanlixiyong.repository.DishesRepository;
import com.niit.guanlixiyong.repository.SeckillActivityRepository;
import com.niit.guanlixiyong.repository.SeckillItemRepository;
import com.niit.guanlixiyong.repository.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 数据初始化配置
 */
@Configuration
public class DataInitializer {

    @Resource
    private DishesRepository dishesRepository;

    @Resource
    private UsersRepository usersRepository;

    @Resource
    private SeckillActivityRepository seckillActivityRepository;

    @Resource
    private SeckillItemRepository seckillItemRepository;

    /**
     * 初始化示例数据
     */
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            try {
                // 初始化用户
                if (usersRepository.count() == 0) {
                    Users admin = new Users();
                    admin.setUsername("admin");
                    admin.setPassword("admin123");
                    admin.setEmail("admin@example.com");
                    admin.setPhone("13800000000");
                    admin.setRole("ADMIN");
                    admin.setAddress("管理系统");
                    usersRepository.save(admin);

                    Users merchant = new Users();
                    merchant.setUsername("merchant");
                    merchant.setPassword("123456");
                    merchant.setEmail("merchant@example.com");
                    merchant.setPhone("13800000001");
                    merchant.setRole("MERCHANT");
                    merchant.setAddress("北京市朝阳区餐厅街88号");
                    usersRepository.save(merchant);

                    Users customer = new Users();
                    customer.setUsername("customer");
                    customer.setPassword("123456");
                    customer.setEmail("customer@example.com");
                    customer.setPhone("13900000001");
                    customer.setRole("CUSTOMER");
                    customer.setAddress("北京市朝阳区某某小区");
                    usersRepository.save(customer);

                    Users delivery = new Users();
                    delivery.setUsername("delivery");
                    delivery.setPassword("123456");
                    delivery.setEmail("delivery@example.com");
                    delivery.setPhone("13900000002");
                    delivery.setRole("DELIVERY");
                    delivery.setAddress("配送站点");
                    usersRepository.save(delivery);

                    System.out.println(">>> 用户数据初始化完成");
                    System.out.println(">>> 测试账号 - admin/admin123, merchant/123456, customer/123456, delivery/123456");
                }

                // 仅在空库时初始化示例菜品，避免每次启动清空业务数据。
                if (dishesRepository.count() > 0) {
                    System.out.println(">>> 菜品数据已存在，跳过初始化");
                    return;
                }

                ensureImagesDirectory();

                // ==================== 热菜类 ====================
                createDish("宫保鸡丁", "经典川菜，鸡肉鲜嫩，花生酥脆", 32.00, "热菜", "kung-pao-chicken");
                createDish("鱼香肉丝", "传统川菜，酸甜微辣", 28.00, "热菜", "yuxiang-shredded-pork");
                createDish("麻婆豆腐", "麻辣鲜香，嫩滑可口", 18.00, "热菜", "mapo-tofu");
                createDish("糖醋里脊", "酸甜可口，外酥里嫩", 35.00, "热菜", "sweet-sour-pork");
                createDish("回锅肉", "川菜经典，肥而不腻", 38.00, "热菜", "twice-cooked-pork");
                createDish("水煮鱼", "麻辣鲜香，鱼肉嫩滑", 58.00, "热菜", "boiled-fish-chili");
                createDish("扬州炒饭", "粒粒分明，配料丰富", 22.00, "热菜", "yangzhou-fried-rice");

                // ==================== 凉菜类 ====================
                createDish("凉拌黄瓜", "清爽开胃，夏日必备", 12.00, "凉菜", "liangban-cucumber");
                createDish("口水鸡", "麻辣鲜香，肉质细嫩", 38.00, "凉菜", "kou-shui-chicken");
                createDish("拍黄瓜", "清脆爽口，简单美味", 10.00, "凉菜", "smashed-cucumber");
                createDish("夫妻肺片", "麻辣浓香，经典川菜", 42.00, "凉菜", "fuqi-feipian");
                createDish("凉拌木耳", "清脆爽口，营养丰富", 15.00, "凉菜", "liangban-wood-ear");

                // ==================== 主食类 ====================
                createDish("米饭", "东北大米，香软可口", 2.00, "主食", "white-rice");
                createDish("馒头", "北方主食，松软可口", 1.00, "主食", "steamed-bun");
                createDish("花卷", "层层分明，松软香甜", 2.00, "主食", "huajuan");
                createDish("烧饼", "外酥内软，香气扑鼻", 3.00, "主食", "chinese-sesame-cake");

                // ==================== 面食类 ====================
                createDish("水饺", "皮薄馅大，鲜美多汁", 18.00, "面食", "dumplings,chinese-dumpling");
                createDish("面条", "手工拉面，劲道十足", 15.00, "面食", "noodles,ramen");
                createDish("馄饨", "汤鲜味美，皮薄馅嫩", 16.00, "面食", "wonton,chinese-soup");
                createDish("包子", "鲜肉大包，汁多味美", 12.00, "面食", "baozi,steamed-bao");

                // ==================== 汤羹类 ====================
                createDish("西红柿鸡蛋汤", "酸甜可口，营养丰富", 15.00, "汤羹", "tomato-egg-soup,tomato-soup");
                createDish("酸辣汤", "酸辣开胃，回味无穷", 16.00, "汤羹", "hot-sour-soup,chinese-soup");
                createDish("紫菜蛋花汤", "清淡鲜美，营养健康", 12.00, "汤羹", "seaweed-soup,egg-drop-soup");
                createDish("排骨汤", "滋补养颜，味道鲜美", 28.00, "汤羹", "pork-rib-soup,rib-soup");
                createDish("玉米羹", "香甜浓郁，营养丰富", 14.00, "汤羹", "corn-soup,sweet-corn");

                // ==================== 饮品类 ====================
                createDish("可乐", "冰镇可乐，透心凉", 5.00, "饮品", "cola,coke");
                createDish("雪碧", "清爽柠檬味，透心凉", 5.00, "饮品", "sprite,lemon-soda");
                createDish("橙汁", "鲜榨橙汁，富含维生素 C", 12.00, "饮品", "orange-juice,fresh-orange");
                createDish("豆浆", "现磨豆浆，香浓可口", 6.00, "饮品", "soy-milk,soybean");
                createDish("酸梅汤", "消暑解渴，传统饮品", 8.00, "饮品", "plum-drink,dark-drink");

                // ==================== 甜品类 ====================
                createDish("冰糖银耳", "清甜滋润，养颜美容", 10.00, "甜品", "white-fungus");
                createDish("桂花糕", "香甜软糯，桂花飘香", 8.00, "甜品", "osmanthus-cake");
                createDish("红糖糍粑", "外酥内软，甜而不腻", 12.00, "甜品", "glutinous-rice-cake");
                createDish("酒酿圆子", "甜香可口，传统美食", 10.00, "甜品", "jiuniang-yuanzi");

                System.out.println(">>> 菜品数据初始化完成，共 " + dishesRepository.count() + " 道菜品");

                // 初始化秒杀活动数据
                initSeckillData();
            } catch (Exception e) {
                System.err.println(">>> 数据初始化失败: " + e.getMessage());
                e.printStackTrace();
                // 不抛出异常，避免应用停止
            }
        };
    }

    /**
     * 初始化秒杀活动数据
     */
    private void initSeckillData() {
        // 如果已有秒杀活动，跳过初始化
        if (seckillActivityRepository.count() > 0) {
            System.out.println(">>> 秒杀活动数据已存在，跳过初始化");
            return;
        }

        System.out.println(">>> 开始初始化秒杀活动数据...");

        // 创建午市特惠秒杀活动
        SeckillActivity lunchSeckill = new SeckillActivity();
        lunchSeckill.setName("午市特惠秒杀");
        lunchSeckill.setStartTime(LocalDateTime.now().plusHours(1)); // 1小时后开始
        lunchSeckill.setEndTime(LocalDateTime.now().plusHours(3)); // 3小时后结束
        lunchSeckill.setDiscount(new BigDecimal("0.5")); // 5折
        lunchSeckill.setDescription("午市特惠，精选菜品5折起！");
        lunchSeckill.setStatus("UPCOMING");
        lunchSeckill.setTotalStock(0);
        lunchSeckill.setSoldStock(0);
        lunchSeckill.setIsActive(true);
        lunchSeckill.setCreateTime(LocalDateTime.now());
        lunchSeckill.setUpdateTime(LocalDateTime.now());
        
        SeckillActivity savedLunch = seckillActivityRepository.save(lunchSeckill);
        System.out.println(">>> 创建午市特惠秒杀活动: " + savedLunch.getId());

        // 添加秒杀商品
        List<Dishes> allDishes = dishesRepository.findByAvailableTrue();
        if (!allDishes.isEmpty()) {
            // 选择前5个菜品作为秒杀商品
            for (int i = 0; i < Math.min(5, allDishes.size()); i++) {
                Dishes dish = allDishes.get(i);
                
                SeckillItem item = new SeckillItem();
                item.setActivity(savedLunch);
                item.setDishes(dish);
                item.setSeckillPrice(dish.getPrice().multiply(new BigDecimal("0.5"))); // 5折
                item.setLimitPerUser(2); // 每人限购2件
                item.setStock(20); // 每个菜品20份
                item.setSoldStock(0);
                item.setIsActive(true);
                item.setCreateTime(LocalDateTime.now());
                item.setUpdateTime(LocalDateTime.now());
                
                seckillItemRepository.save(item);
                
                // 更新活动总库存
                savedLunch.setTotalStock(savedLunch.getTotalStock() + 20);
            }
            seckillActivityRepository.save(savedLunch);
        }

        System.out.println(">>> 秒杀活动数据初始化完成");
    }

    private void createDish(String name, String description, double price, String category, String seed) {
        Dishes dish = new Dishes();
        dish.setName(name);
        dish.setDescription(description);
        dish.setPrice(new BigDecimal(String.valueOf(price)));
        dish.setCategory(category); // 设置分类
        // 使用本地图片路径
        dish.setImageUrl(getFoodImageUrl(seed));
        dish.setAvailable(true);
        dish.setStock(60);
        dish.setCreateTime(LocalDateTime.now());
        dish.setUpdateTime(LocalDateTime.now());
        Dishes saved = dishesRepository.save(dish);
        // 验证分类是否正确保存
        if (!saved.getCategory().equals(category)) {
            System.err.println(">>> 警告: 菜品 '" + name + "' 分类保存失败! 期望: " + category + ", 实际: " + saved.getCategory());
        }
    }

    private String getFoodImageUrl(String keywords) {
        // 使用本地图片路径
        // 从关键词中提取第一个作为文件名
        String fileName = keywords.split(",")[0] + ".jpg";
        String localPath = "/images/" + fileName;
        
        // 如果本地图片不存在，则从网络下载
        downloadImageIfNeeded(keywords, fileName);
        
        return localPath;
    }
    
    /**
     * 确保 images 目录存在
     */
    private void ensureImagesDirectory() {
        try {
            Path imagesDir = Paths.get("src/main/resources/static/images");
            if (!Files.exists(imagesDir)) {
                Files.createDirectories(imagesDir);
                System.out.println(">>> 创建 images 目录: " + imagesDir.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println(">>> 创建 images 目录失败: " + e.getMessage());
        }
    }
    
    /**
     * 如果需要则下载图片
     */
    private void downloadImageIfNeeded(String keywords, String fileName) {
        Path imagePath = Paths.get("src/main/resources/static/images/" + fileName);
        
        // 如果图片已存在，跳过下载
        if (Files.exists(imagePath)) {
            System.out.println(">>> 图片已存在，跳过下载: " + fileName);
            return;
        }
        
        try {
            System.out.println(">>> 开始下载图片: " + fileName);
            
            // 使用 Picsum Photos 服务（更稳定）
            // 为每个菜品使用固定的图片ID，确保图片一致性
            String imageUrl = "https://picsum.photos/seed/" + fileName.replace(".jpg", "") + "/400/300";
            
            URL url = new URL(imageUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setInstanceFollowRedirects(true);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(imagePath.toFile())) {
                    
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    int totalBytes = 0;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalBytes += bytesRead;
                    }
                    
                    // 验证文件大小（至少 5KB）
                    if (totalBytes > 5000) {
                        System.out.println(">>> ✓ 图片下载成功: " + fileName + " (" + totalBytes + " bytes)");
                    } else {
                        System.err.println(">>> ⚠ 警告: 图片太小可能下载失败: " + fileName + " (" + totalBytes + " bytes)");
                        Files.deleteIfExists(imagePath);
                    }
                }
            } else {
                System.err.println(">>> ✗ 下载失败，HTTP状态码: " + responseCode + ", 文件: " + fileName);
            }
            
            connection.disconnect();
            
            // 稍微延迟，避免请求过快
            Thread.sleep(200);
        } catch (Exception e) {
            System.err.println(">>> ✗ 下载图片失败: " + fileName + ", 错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
