import com.PetSitter.InitDB;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test") // test 프로필(yml)에서만 작동
@Component
public class InitDBRunner implements ApplicationRunner {

    private final InitDB initDB;

    public InitDBRunner(InitDB initDB) {
        this.initDB = initDB;
    }

    @Override
    public void run(ApplicationArguments args) {
        initDB.init();
    }
}
