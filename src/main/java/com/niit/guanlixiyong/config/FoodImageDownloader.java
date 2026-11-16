package com.niit.guanlixiyong.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 菜品图片下载器
 * 从 Unsplash 下载真实的菜品图片
 */
@Configuration
public class FoodImageDownloader {

    // 高优先级图片映射（需要替换的）
    private static final String[][] HIGH_PRIORITY_IMAGES = {
        {"kung-pao-chicken.jpg", "kung+pao+chicken+chinese+food"},
        {"yuxiang-shredded-pork.jpg", "chinese+shredded+pork+stir+fry"},
        {"chinese-sesame-cake.jpg", "chinese+sesame+flatbread+bread"},
        {"kou-shui-chicken.jpg", "chinese+chicken+chili+oil+cold+dish"}
    };

    // 中优先级图片映射（建议优化的）
    private static final String[][] MEDIUM_PRIORITY_IMAGES = {
        {"mapo-tofu.jpg", "mapo+tofu+chinese+spicy"},
        {"sweet-sour-pork.jpg", "sweet+and+sour+pork+chinese"},
        {"twice-cooked-pork.jpg", "chinese+pork+belly+stir+fry"},
        {"boiled-fish-chili.jpg", "chinese+boiled+fish+chili+oil"},
        {"yangzhou-fried-rice.jpg", "yangzhou+fried+rice+chinese"},
        {"liangban-cucumber.jpg", "chinese+cucumber+salad+cold"},
        {"smashed-cucumber.jpg", "smashed+cucumber+chinese+salad"},
        {"fuqi-feipian.jpg", "chinese+beef+offal+salad+spicy"},
        {"liangban-wood-ear.jpg", "wood+ear+mushroom+salad+chinese"},
        {"huajuan.jpg", "chinese+flower+roll+steamed+bread"},
        {"osmanthus-cake.jpg", "chinese+osmanthus+cake+dessert"},
        {"jiuniang-yuanzi.jpg", "chinese+fermented+rice+balls+soup"}
    };

    /**
     * 启动时自动下载高优先级图片
     */
    @Bean
    CommandLineRunner downloadHighPriorityImages() {
        return args -> {
            try {
                System.out.println("\n========================================");
                System.out.println("  开始下载高优先级菜品图片");
                System.out.println("========================================\n");

                Path imagesDir = Paths.get("src/main/resources/static/images");
                
                // 确保目录存在
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                    System.out.println(">>> 创建 images 目录: " + imagesDir.toAbsolutePath());
                }

                int successCount = 0;
                int failCount = 0;

                // 下载高优先级图片
                for (String[] imageInfo : HIGH_PRIORITY_IMAGES) {
                    String fileName = imageInfo[0];
                    String searchQuery = imageInfo[1];
                    
                    Path imagePath = imagesDir.resolve(fileName);
                    
                    // 如果文件已存在，跳过
                    if (Files.exists(imagePath)) {
                        System.out.println("⏭️  跳过已存在的图片: " + fileName);
                        successCount++;
                        continue;
                    }

                    System.out.println("📥 正在下载: " + fileName);
                    System.out.println("   搜索关键词: " + searchQuery.replace("+", " "));
                    
                    boolean downloaded = downloadImageFromUnsplash(imagePath, searchQuery);
                    
                    if (downloaded) {
                        System.out.println("✅ 下载成功: " + fileName);
                        successCount++;
                    } else {
                        System.out.println("❌ 下载失败: " + fileName);
                        failCount++;
                    }
                    
                    // 延迟，避免请求过快
                    Thread.sleep(1000);
                }

                System.out.println("\n========================================");
                System.out.println("  下载完成统计");
                System.out.println("========================================");
                System.out.println("✅ 成功: " + successCount + " 张");
                System.out.println("❌ 失败: " + failCount + " 张");
                System.out.println("📁 保存位置: " + imagesDir.toAbsolutePath());
                System.out.println("========================================\n");

                if (failCount > 0) {
                    System.out.println("💡 提示: 失败的图片可以手动从以下网站下载:");
                    System.out.println("   - Unsplash: https://unsplash.com/");
                    System.out.println("   - Pexels: https://www.pexels.com/");
                    System.out.println("   - Pixabay: https://pixabay.com/\n");
                }
            } catch (Exception e) {
                System.err.println(">>> 图片下载过程中发生错误: " + e.getMessage());
                e.printStackTrace();
                // 不抛出异常，避免应用停止
            }
        };
    }

    /**
     * 从 Unsplash 下载图片
     * 使用 source.unsplash.com API（简单但可能不稳定）
     */
    private boolean downloadImageFromUnsplash(Path imagePath, String searchQuery) {
        try {
            // 方法1: 使用 Unsplash Source API（已弃用但有时仍可用）
            String unsplashUrl = "https://source.unsplash.com/800x600/?" + searchQuery;
            
            // 方法2: 使用 Picsum Photos 作为备选（更稳定，但不是真实菜品）
            // String picsumUrl = "https://picsum.photos/seed/" + 
            //     imagePath.getFileName().toString().replace(".jpg", "") + "/800/600";
            
            URL url = new URL(unsplashUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            
            // 设置请求头，模拟浏览器
            connection.setRequestProperty("User-Agent", 
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
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
                    
                    // 验证文件大小（至少 10KB）
                    if (totalBytes > 10000) {
                        System.out.println("   文件大小: " + (totalBytes / 1024) + " KB");
                        return true;
                    } else {
                        System.err.println("   ⚠️  文件太小: " + totalBytes + " bytes，可能下载失败");
                        Files.deleteIfExists(imagePath);
                        return false;
                    }
                }
            } else {
                System.err.println("   HTTP 状态码: " + responseCode);
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("   错误: " + e.getMessage());
            return false;
        }
    }

    /**
     * 手动触发下载所有图片（包括中优先级）
     * 可以通过调用此方法来下载更多图片
     */
    public void downloadAllImages() {
        System.out.println("\n========================================");
        System.out.println("  开始下载所有菜品图片");
        System.out.println("========================================\n");

        Path imagesDir = Paths.get("src/main/resources/static/images");
        
        int count = 0;
        
        // 下载中优先级图片
        for (String[] imageInfo : MEDIUM_PRIORITY_IMAGES) {
            String fileName = imageInfo[0];
            String searchQuery = imageInfo[1];
            
            Path imagePath = imagesDir.resolve(fileName);
            
            if (Files.exists(imagePath)) {
                System.out.println("⏭️  跳过: " + fileName);
                continue;
            }

            System.out.println("📥 下载: " + fileName);
            boolean success = downloadImageFromUnsplash(imagePath, searchQuery);
            
            if (success) {
                count++;
                System.out.println("✅ 成功");
            } else {
                System.out.println("❌ 失败");
            }
            
            try {
                Thread.sleep(1500); // 更长延迟
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\n🎉 完成! 成功下载 " + count + " 张图片\n");
    }
}
