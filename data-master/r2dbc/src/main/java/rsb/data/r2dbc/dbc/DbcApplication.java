package rsb.data.r2dbc.dbc;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@RequiredArgsConstructor
public class DbcApplication {

	public static void main(String args[]) {
		SpringApplication.run(DbcApplication.class, args);
	}

}
