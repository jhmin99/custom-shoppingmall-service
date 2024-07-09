package jihong99.shoppingmall.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;




@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final DataInitializationService dataInitializationService;

    @PostConstruct
    public void init() {
        dataInitializationService.initializeData();
    }
}