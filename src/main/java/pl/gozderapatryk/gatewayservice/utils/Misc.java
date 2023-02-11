package pl.gozderapatryk.gatewayservice.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@UtilityClass
public class Misc {

    public static String getHelloMessage(Environment environment) {
        return String.format(
                        """
                        
                        ==================================
                        
                        Application '%s' is running on port %s!
                        Profile(s): %s

                        ==================================
                        """,
                environment.getProperty("spring.application.name"),
                environment.getProperty("server.port"),
                Arrays.toString(environment.getActiveProfiles())
        );
    }
}
