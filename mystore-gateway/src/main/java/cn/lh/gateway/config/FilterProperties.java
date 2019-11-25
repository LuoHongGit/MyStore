package cn.lh.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "mystore.filter")
@Data
public class FilterProperties {
    private List<String> allowPaths;
}